package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * SpringMVC 文件上传
 * 上传文件到 FTP 服务器，返回目标文件名
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path) {

        String fileName = file.getOriginalFilename(); // 原始文件名
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1); // 文件扩展名
        String newFileName = UUID.randomUUID().toString() + "." + ext; // 生成唯一新文件名，使用 UUID 处理文件名重复的问题

        logger.info("开始上传文件，上传文件的文件名：{}，上传的路径：{}，新文件名：{}", fileName, path, newFileName);

        // 如果上传目录不存在，则创建目录
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true); // 赋予写权限
            fileDir.mkdirs(); // 创建多级目录成功
        }

        // 创建目标文件，然后将收到的文件传输（复制）到给定的目标文件
        File targetFile = new File(path, newFileName);
        try {
            file.transferTo(targetFile);
            logger.info("文件已经上传到本地！");

            // 将文件上传到 FTP 文件服务器上，然后删除本地的文件
            if (FTPUtils.uploadFile2FTP(Lists.newArrayList(targetFile))) {
                logger.info("文件上传到 FTP 服务器成功！");

                if (targetFile.delete()) {
                    logger.info("本地文件已删除");
                }
            } else {
                logger.info("文件已经上传到 FTP 服务器失败~");
            }
        } catch (IllegalStateException | IOException e) {
            logger.error("文件上传异常", e);
            return null;
        }

        return targetFile.getName();
    }

}
