package com.example.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.example.service.CommonService;
import com.example.vo.AliOssProperties;
import com.example.vo.ResultVo;
import com.example.vo.UploadFileVo;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Value("${file.uploadPath}")
    private String uploadPath;

    @Value("${file.uploadUrl}")
    private String uploadUrl;

    @Autowired
    private AliOssProperties aliOssProperties;

    /**
     * 上传文件
     *
     * @param file
     * @param fileName
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo uploadFile(MultipartFile file, String fileName) throws Exception {
        try {
            String oldName = file.getOriginalFilename();
            //获取后缀名
            String imgType = oldName.substring(oldName.lastIndexOf("."));
            //时间格式化格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            //获取当前时间并作为时间戳
            String timeStamp = simpleDateFormat.format(new Date());
            //拼接新的文件名
            String newName = timeStamp + imgType;

            //上传本地路径或指定服务器
            return writeUploadFile(file, uploadPath, newName);

            //上传阿里云服务器
            //return aliYunUploadFile(file.getInputStream(), newName);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * 文件上传处理
     *
     * @param file
     * @param realpath
     * @param fileName
     * @throws Exception
     */
    private ResultVo writeUploadFile(MultipartFile file, String realpath, String fileName) throws Exception {
        File fileDir = new File(realpath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        InputStream input = null;
        FileOutputStream fos = null;
        try {
            input = file.getInputStream();
            fos = new FileOutputStream(realpath + "/" + fileName);
            IOUtils.copy(input, fos);
        } catch (Exception e) {
            return ResultVo.fail("上传失败: "+e.getMessage());
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fos);
        }

        UploadFileVo uploadFile = new UploadFileVo();

        String filePath = uploadPath + fileName;
        String url = uploadUrl + fileName;

        uploadFile.setFileName(fileName);
        uploadFile.setFilePath(filePath);
        uploadFile.setFileUrl(url);

        return ResultVo.success(uploadFile);
    }

    /**
     * AliYun OSS 上传接口
     *
     * @param file
     * @param fileName
     * @return
     */
    public ResultVo aliYunUploadFile(InputStream file, String fileName) {

        String endpoint = aliOssProperties.getEndPoint();
        String accessKeyId = aliOssProperties.getKeyId();
        String accessKeySecret = aliOssProperties.getAccessKeySecret();
        String bucketName = aliOssProperties.getBucketName();
        String fileHost = aliOssProperties.getFileHost();

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            //容器不存在，就创建
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            UploadFileVo callResult = new UploadFileVo();
            callResult.setFileName(fileName);
            //创建文件路径
            String key = fileHost + fileName;
            callResult.setFilePath(key);
            //上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, key, file));
            //设置权限 这里是公开读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);

            if (null != result) {
                String callUrl = uploadUrl + key;
                callResult.setFileUrl(callUrl);
            }
            return ResultVo.success(callResult);
        } catch (OSSException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return ResultVo.fail("上传失败");
    }

    /**
     * 图片显示/下载文件
     *
     * @param fileName
     * @param response
     */
    @Override
    public void downloadFile(String fileName, HttpServletResponse response) {
        ServletOutputStream out = null;
        FileInputStream inputStream = null;

        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        //2.设置文件头：最后一个参数是设置下载文件名
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);

        //通过文件路径获得File对象
        File file = new File(uploadPath + fileName);

        try {
            inputStream = new FileInputStream(file);
            //3.通过response获取ServletOutputStream对象(out)
            out = response.getOutputStream();
            int i = inputStream.available();
            //byte数组用于存放文件字节数据
            byte[] buffer = new byte[i];
            inputStream.read(buffer);
            //4.写到输出流(out)中
            out.write(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
