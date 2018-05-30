package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    /**
     * SpringMVC 文件上传，返回新文件名
     *
     * @param file 文件
     * @param path 路径
     */
    String uploadFile(MultipartFile file, String path);

}
