package com.example.controller;

import com.google.common.collect.Lists;

import com.example.param.UserParam;
import com.example.service.CommonService;
import com.example.utils.DateUtil;
import com.example.utils.ExportExcelPlusUtil;
import com.example.utils.ExportExcelUtil;
import com.example.utils.ImportExcelUtil;
import com.example.vo.ResultVo;
import com.example.vo.VmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传下载接口
 * Excel导入导出接口
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

    private final Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * 上传文件接口
     *
     * @param file
     * @param fileName
     * @return
     */
    @PostMapping(value = "/uploadfile")
    public ResultVo uploadFile(@RequestParam("file") MultipartFile file, String fileName) throws Exception {
        return commonService.uploadFile(file, fileName);
    }


    /**
     * 图片显示/下载文件接口
     *
     * @param fileName 文件名称
     * @return
     */
    @GetMapping(value = "/downloadfile/{fileName}")
    public void downloadFile(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        commonService.downloadFile(fileName, response);
    }

    /**
     * 下载输入数据的模板接口
     *
     * @param response
     */
    @RequestMapping("downtemplate")
    public void importFileTemplate(HttpServletResponse response) throws Exception {
        try {
            //定义文件名称
            String fileName = "User_Data_import_template.xlsx";
            List<VmUser> list = Lists.newArrayList();
            new ExportExcelUtil("User Data", VmUser.class, 1).setDataList(list).write(response, fileName).dispose();
            //new ExportExcelPlusUtil("User Data", VmUser.class).setSingleData(10).setSubClass(VmUser.class, 1).setDataList(list).write(response, fileName).dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 导入已经填好数据的Excel接口
     *
     * @param file
     */
    @PostMapping(value = "import")
    public void importFile(@RequestParam("file") MultipartFile file) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcelUtil ei = new ImportExcelUtil(file.getOriginalFilename(), file.getInputStream(), 1, 0);
            List<VmUser> list = ei.getDataList(VmUser.class);
            for (VmUser user : list) {
                try {
                    //to do: 保存处理数据
                    //userService.save(user);
                    logger.info(user.toString());
                    successNum++;
                } catch (ConstraintViolationException ex) {
                    failureNum++;
                } catch (Exception ex) {
                    failureNum++;
                }
            }

            if (failureNum > 0) {
                failureMsg.insert(0, ", Failures: " + failureNum);
            }
            logger.info("Had Operation " + successNum + " Data;" + " " + "Failure " + failureNum);
        } catch (Exception e) {
            logger.error("导入失败", e);
        }
    }

    /**
     * 导出Excel文件接口
     *
     * @param response
     */
    @RequestMapping("export")
    public void export(HttpServletResponse response) {
        try {
            String fileName = "User Data" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<UserParam> users = new ArrayList<>();
            UserParam user1 = new UserParam();
            user1.setUserName("小明");
            user1.setDisplayName("猪小明");
            user1.setPhone("13552342511");
            user1.setCreateTime(DateUtil.parseDate("2019-08-12 17:00:00"));
            users.add(user1);
            UserParam user2 = new UserParam();
            user2.setUserName("小红");
            user2.setDisplayName("小小红");
            user2.setPhone("19952342511");
            user2.setCreateTime(DateUtil.parseDate("2019-08-11 09:00:00"));
            users.add(user2);
            new ExportExcelUtil("User Data", UserParam.class, 2).setDataList(users).write(response, fileName).dispose();
        } catch (Exception e) {
        }
    }

}
