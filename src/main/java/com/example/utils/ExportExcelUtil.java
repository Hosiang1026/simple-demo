package com.example.utils;

import com.example.annotation.ExcelEntity;
import com.example.annotation.ExcelField;
import com.example.constants.CommonConstant;
import com.example.support.Encodes;
import com.example.support.Reflections;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * Export导出工具
 *
 * @author Howe Hsiang
 */
public class ExportExcelUtil {

    private static Logger log = LoggerFactory.getLogger(ExportExcelUtil.class);

    /**
     * 工作薄对象
     */
    private SXSSFWorkbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     *  绘图对象
     *
     *  注：每个sheet，只能创建一次
     */
    private Drawing patriarch;

    /**
     * 样式列表
     */
    private Map<String, CellStyle> styles;

    /**
     * 当前行号
     */
    private int rownum;

    /**
     * 注解列表（Object[]{ ExcelField, Field/Method }）
     */
    List<Object[]> annotationList = new ArrayList<>();

    /**
     * 构造函数
     *
     * @param title 表格标题，传“空值”，表示无标题
     * @param cls   实体对象，通过annotation.ExportField获取标题
     */
    public ExportExcelUtil(String title, Class<?> cls) throws Exception {
        this(title, cls, 1);
    }

    /**
     * 构造函数
     *
     * @param title  表格标题，传“空值”，表示无标题
     * @param cls    实体对象，通过annotation.ExportField获取标题
     * @param type   导出类型（1:导出模板；2：导出数据）
     * @param groups 导入分组
     */
    public ExportExcelUtil(String title, Class<?> cls, int type, int... groups) throws Exception {
        // Get annotation field
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            //获取字段上加的@Excel注解
            getExcelField(f, type, groups);
            ExcelEntity excelEntity = f.getAnnotation(ExcelEntity.class);
            if (excelEntity != null) {
                String className = excelEntity.name();
                Class<?> ecls = Class.forName(className);
                Field[] efs = ecls.getDeclaredFields();
                for (Field ef : efs) {
                    //获取字段上加的@Excel注解
                    getExcelField(ef, type, groups);
                }
            }
        }
        // Get annotation method
        Method[] ms = cls.getDeclaredMethods();
        for (Method m : ms) {
            //获取方法上的Excel注解字段
            getExcelMethod(m, type, groups);
        }
        // 对字段进行排序 Field sorting
        Collections.sort(annotationList, (o1, o2) -> new Integer(((ExcelField) o1[0]).sort()).compareTo(
                ((ExcelField) o2[0]).sort()));
        // Initialize
        List<String> headerList = new ArrayList<>();
        for (Object[] os : annotationList) {
            //获取注解title属性值
            String t = ((ExcelField) os[0]).title();
            // 如果是导出，则去掉注释
            if (type == 1) {
                String[] ss = StringUtils.split(t, "**", 2);
                if (ss.length == 2) {
                    t = ss[0];
                }
            }
            //将字段名称保存在一个list中，交给初始化方法使用
            headerList.add(t);
        }
        initialize(title, headerList);
    }

    /**
     * 获取Excel注解字段
     */
    private void getExcelField(Field f, int type, int... groups) {

        ExcelField ef = f.getAnnotation(ExcelField.class);
        if (ef != null && (ef.type() == 0 || ef.type() == type)) {
            //根据字段注解中配置的groups进行分组
            if (groups != null && groups.length > 0) {
                boolean inGroup = false;
                for (int g : groups) {
                    if (inGroup) {
                        break;
                    }
                    for (int efg : ef.groups()) {
                        if (g == efg) {
                            inGroup = true;
                            annotationList.add(new Object[]{ef, f});
                            break;
                        }
                    }
                }
            } else {
                //若无group属性，则直接将字段和对应的注解加入到一个全局的注解链表中，用于之后进行统一的排序
                annotationList.add(new Object[]{ef, f});
            }
        }

    }

    /**
     * 获取方法上的Excel注解字段
     */
    private void getExcelMethod(Method m, int type, int... groups) {

        ExcelField ef = m.getAnnotation(ExcelField.class);
        if (ef != null && (ef.type() == 0 || ef.type() == type)) {
            if (groups != null && groups.length > 0) {
                boolean inGroup = false;
                for (int g : groups) {
                    if (inGroup) {
                        break;
                    }
                    for (int efg : ef.groups()) {
                        if (g == efg) {
                            inGroup = true;
                            annotationList.add(new Object[]{ef, m});
                            break;
                        }
                    }
                }
            } else {
                annotationList.add(new Object[]{ef, m});
            }
        }
    }

    /**
     * 构造函数
     *
     * @param title   表格标题，传“空值”，表示无标题
     * @param headers 表头数组
     */
    public ExportExcelUtil(String title, String[] headers) {
        initialize(title, Arrays.stream(headers).collect(Collectors.toList()));
    }

    /**
     * 构造函数
     *
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头列表
     */
    public ExportExcelUtil(String title, List<String> headerList) {
        initialize(title, headerList);
    }

    /**
     * 初始化函数
     *
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头列表
     */
    private void initialize(String title, List<String> headerList) {
        this.wb = new SXSSFWorkbook(500);
        this.sheet = wb.createSheet("Export");
        //创建绘图对象
        this.patriarch = sheet.createDrawingPatriarch();
        this.styles = createStyles(wb);
        // Create title
        if (StringUtils.isNotBlank(title)) {
            Row titleRow = sheet.createRow(rownum++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
                    titleRow.getRowNum(), titleRow.getRowNum(), headerList.size() - 1));
        }
        // Create header
        if (headerList == null) {
            throw new RuntimeException("headerList not null!");
        }
        Row headerRow = sheet.createRow(rownum++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String[] ss = StringUtils.split(headerList.get(i), "**", 2);
            if (ss.length == 2) {
                cell.setCellValue(ss[0]);
                Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
                        new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            } else {
                cell.setCellValue(headerList.get(i));
            }
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = sheet.getColumnWidth(i) * 2;
            sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
        }



        log.debug("Initialize success.");
    }

    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        styles.put("data3", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
//		style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }

    /**
     * 添加一行
     *
     * @return 行对象
     */
    public Row addRow() {
        return sheet.createRow(rownum++);
    }


    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, 0, Class.class);
    }

    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @param align  对齐方式（1：靠左；2：居中；3：靠右）
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType) {
        Cell cell = row.createCell(column);
        CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else if (val instanceof Integer) {
                cell.setCellValue((Integer) val);
            } else if (val instanceof Long) {
                cell.setCellValue((Long) val);
            } else if (val instanceof Double) {
                cell.setCellValue((Double) val);
            } else if (val instanceof Float) {
                cell.setCellValue((Float) val);
            } else if (val instanceof Date) {
                DataFormat format = wb.createDataFormat();
                style.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
                cell.setCellValue((Date) val);
            } else {
                if (fieldType != Class.class) {
                    cell.setCellValue((String) fieldType.getMethod("setValue", Object.class).invoke(null, val));
                } else {
                    cell.setCellValue((String) Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                            "fieldtype." + val.getClass().getSimpleName() + "Type")).getMethod("setValue", Object.class).invoke(null, val));
                }
            }
        } catch (Exception ex) {
            log.info("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
            cell.setCellValue(val.toString());
        }
        cell.setCellStyle(style);
        return cell;
    }


    /**
     * 添加数据（通过annotation.ExportField添加数据）
     *
     * @return list 数据列表
     */
    public <E> ExportExcelUtil setDataList(List<E> list) {
        int imgNum = 0;
        //存储最大列宽
        Map<Integer,Integer> maxWidth = new HashMap<>();
        for (E e : list) {
            int colunm = 0;
            Row row = this.addRow();
            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList) {
                ExcelField ef = (ExcelField) os[0];
                Object val = null;
                // Get entity value
                String dictType = ef.dictType();
                try {
                    //枚举类型dictType
                    if (StringUtils.isNotBlank(dictType)) {
                        Object obj = Reflections.invokeGetter(e, ((Field) os[1]).getName());
                        if (obj instanceof Enum) {
                            val = Reflections.invokeGetter(obj, dictType);
                        }
                    } else {
                        if (StringUtils.isNotBlank(ef.value())) {
                            val = Reflections.invokeGetter(e, ef.value());
                        } else {
                            Class<?> cls = e.getClass();
                            Field[] fs = cls.getDeclaredFields();
                            for (Field f : fs) {
                                //获取字段上加的@Excel注解
                                ExcelEntity excelEntity = f.getAnnotation(ExcelEntity.class);
                                if (excelEntity != null) {
                                    Object excelEntityObj = Reflections.invokeGetter(e, (f).getName());
                                    //获取级联实体类
                                    String className = excelEntity.name();
                                    Class<?> ecls = Class.forName(className);
                                    Field[] efs = ecls.getDeclaredFields();
                                    for (Field efss : efs) {
                                        //获取字段上加的@Excel注解
                                        ExcelField efsss = efss.getAnnotation(ExcelField.class);
                                        if (efsss == ef) {
                                            if (efss instanceof Field) {
                                                val = Reflections.invokeGetter(excelEntityObj, (efss).getName());
                                            }
                                        }
                                    }
                                }
                            }

                            if (val == null) {
                                if (os[1] instanceof Field) {
                                    val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
                                } else if (os[1] instanceof Method) {
                                    val = Reflections.invokeMethod(e, ((Method) os[1]).getName(), new Class[]{}, new Object[]{});
                                }



                            }
                        }


                    }

                } catch (Exception ex) {
                    // Failure to ignore
                    log.info(ex.toString());
                }


                String fileType = ef.fileType();
                if(("image").equals(fileType)){
                    row.setHeight((short) 2000);
                    Cell cellContent =  this.addCell(row, colunm++, "", ef.align(), ef.fieldType());
                    //绘制图片
                    if(Objects.nonNull(val)){
                        createPicture(val.toString(),cellContent);
                    }
                }else{
                       this.addCell(row, colunm++, val, ef.align(), ef.fieldType());
                }

                sb.append(val + ", ");

            }


            log.debug("Write success: [" + row.getRowNum() + "] " + sb.toString());
        }
        return this;
    }


    /**
     * 绘制图片
     * @param basepath
     * @param cellContent
     * @throws Exception
     */
    private void createPicture(String basepath, Cell cellContent) {

        try{

            //图片转化为流
            BufferedImage bufferImg = readPicture(basepath);

            if (null != bufferImg){

                //先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
                ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

                String pictureSuffix = basepath.substring(basepath.lastIndexOf(".")+1);
                if(StringUtils.isNotBlank(pictureSuffix)){
                    pictureSuffix = pictureSuffix.toLowerCase();
                }
                // 将图片写入流中
                ImageIO.write(bufferImg, pictureSuffix, byteArrayOut);

                /**
                 * 该构造函数有8个参数
                 * 前四个参数是控制图片在单元格的位置，分别是图片距离单元格left，top，right，bottom的像素距离
                 * 后四个参数，前两个表示图片左上角所在的cellNum和 rowNum，后两个参数对应的表示图片右下角所在的cellNum和 rowNum，
                 * excel中的cellNum和rowNum的index都是从0开始的
                 *
                 */
                //图片导出到指定位置的单元格中
                ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,(short) cellContent.getColumnIndex(), cellContent.getRowIndex(), (short) (cellContent.getColumnIndex()+1), cellContent.getRowIndex()+1);
                //插入图片
                patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), CommonConstant.PICTURE_TYPE_MAP.get(pictureSuffix)));
            }

        }catch (Exception e){

        }


    }

    /**
     * 读取图片流
     * @param basepath
     * @return
     * @throws Exception
     */
    private BufferedImage readPicture(String basepath) throws Exception {

        BufferedImage bufferImg = null;

        //处理掉URL里的特殊字符
        String urlLink = URLDecoder.decode(basepath,"utf-8");

        //网络URL
        if (-1 !=basepath.indexOf("http")){
            //开始连接
            URL netUrl = new URL(urlLink);
            HttpURLConnection httpUrl = (HttpURLConnection)netUrl.openConnection();
            httpUrl.setRequestMethod("GET");
            httpUrl.connect();

            //获取连接的状态码
            if (httpUrl.getResponseCode() == 200) {
                //如果连接成功,返回结果:成功
                //将图片读到BufferedImage
                bufferImg = ImageIO.read(httpUrl.getInputStream());
            }

        //本地URL
        }else{
            bufferImg = ImageIO.read(new File(urlLink));
        }

        return bufferImg;
    }

    /**
     * 输出数据流
     *
     * @param os 输出数据流
     */
    public ExportExcelUtil write(OutputStream os) throws IOException {
        wb.write(os);
        return this;
    }

    /**
     * 输出到客户端
     *
     * @param fileName 输出文件名
     */
    public ExportExcelUtil write(HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
        write(response.getOutputStream());
        return this;
    }

    /**
     * 输出到文件
     *
     * @param name 输出文件名
     */
    public ExportExcelUtil writeFile(String name) throws FileNotFoundException, IOException {
        FileOutputStream os = new FileOutputStream(name);
        this.write(os);
        return this;
    }

    /**
     * 清理临时文件
     */
    public ExportExcelUtil dispose() {
        wb.dispose();
        return this;
    }

//	/**
//	 * 导出测试
//	 */
//	public static void main(String[] args) throws Throwable {
//
//		List<String> headerList = Lists.newArrayList();
//		for (int i = 1; i <= 10; i++) {
//			headerList.add("表头"+i);
//		}
//
//		List<String> dataRowList = Lists.newArrayList();
//		for (int i = 1; i <= headerList.size(); i++) {
//			dataRowList.add("数据"+i);
//		}
//
//		List<List<String>> dataList = Lists.newArrayList();
//		for (int i = 1; i <=1000000; i++) {
//			dataList.add(dataRowList);
//		}
//
//		ExportExcel ee = new ExportExcel("表格标题", headerList);
//
//		for (int i = 0; i < dataList.size(); i++) {
//			Row row = ee.addRow();
//			for (int j = 0; j < dataList.get(i).size(); j++) {
//				ee.addCell(row, j, dataList.get(i).get(j));
//			}
//		}
//
//		ee.writeFile("target/export.xlsx");
//
//		ee.dispose();
//
//		log.debug("Export success.");
//	}
}
