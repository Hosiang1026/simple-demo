package com.example.service;

import com.example.vo.ResultVo;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Howe Hsiang
 * @since 2019-08-05
 */
public interface CommonService {

    /**
     * 上传文件
     *
     * @param file
     * @param fileName
     * @return
     * @throws Exception
     */
    ResultVo uploadFile(MultipartFile file, String fileName) throws Exception;

    /**
     * 图片显示/下载文件
     *
     * @param fileName
     * @param response
     */
    void downloadFile(String fileName, HttpServletResponse response);

}
