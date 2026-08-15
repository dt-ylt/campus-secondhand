package com.campus.secondhand.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务
 */
public interface FileService {

    /**
     * 上传文件到 MinIO
     * @param file 前端传来的文件
     * @return 文件的可访问 URL（存到数据库的商品图片字段就是这个）
     */
    String upload(MultipartFile file);
}
