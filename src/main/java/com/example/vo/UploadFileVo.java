package com.example.vo;

import lombok.Data;

/**
 * 上传文件VO
 */
@Data
public class UploadFileVo {

    /**
     * 上传文件名称
     */
    private String fileName;

    /**
     * 上传文件路径
     */
    private String filePath;

    /**
     * 上传文件URL
     */
    private String fileUrl;


}
