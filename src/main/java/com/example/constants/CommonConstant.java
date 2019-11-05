package com.example.constants;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

/**
 *  常量
 * @author Howe Hsiang
 */
public class CommonConstant {

    /**
     * 图片类型
     */
    public final static Map<String, Integer> PICTURE_TYPE_MAP = new HashMap();
    static {
        PICTURE_TYPE_MAP.put("emf", XSSFWorkbook.PICTURE_TYPE_EMF );
        PICTURE_TYPE_MAP.put("wmf", XSSFWorkbook.PICTURE_TYPE_WMF );
        PICTURE_TYPE_MAP.put("pict", XSSFWorkbook.PICTURE_TYPE_PICT );
        PICTURE_TYPE_MAP.put("png", XSSFWorkbook.PICTURE_TYPE_PNG );
        PICTURE_TYPE_MAP.put("jpe", XSSFWorkbook.PICTURE_TYPE_JPEG );
        PICTURE_TYPE_MAP.put("dib", XSSFWorkbook.PICTURE_TYPE_DIB );
    }

}
