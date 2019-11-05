package com.example.vo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @Author: Howe Hsiang
 * @Date: 2019/10/18 11:07
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss.file")
public class AliOssProperties {

    private String endPoint;

    private String keyId;

    private String accessKeySecret;

    private String bucketName;

    private String fileHost;

}
