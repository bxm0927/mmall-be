package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FTP 工具类
 * 1. 开始连接 FTP 服务器
 * 2. FTP 服务器上传结果：{}
 */
public class FTPUtils {

    private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);

    // FTPClient 对象
    private static FTPClient ftpClient;

    private static final int FTP_PORT = 21;
    private static final String FTP_IP = PropertiesUtil.getProperty("ftp.ip");
    private static final String FTP_USERNAME = PropertiesUtil.getProperty("ftp.username");
    private static final String FTP_PASSWORD = PropertiesUtil.getProperty("ftp.password");

    /**
     * 文件上传到 FTP 服务器
     *
     * @param fileList   文件列表，支持批量上传
     * @param remotePath 远端存储路径
     */
    public static boolean uploadFile2FTP(List<File> fileList) throws IOException {
        boolean uploaded = false;
        FileInputStream fis = null;

        if (connectFTPServer(FTP_IP, FTP_PORT, FTP_USERNAME, FTP_PASSWORD)) {
            try {
                // TODO 更改工作目录。不设置此项则传到 FTP 共享目录 /ftpfile 下，但是设置了由传不进去，无语 - -
                // ftpClient.changeWorkingDirectory("img");

                ftpClient.setBufferSize(1024); // 设置缓冲区
                ftpClient.setControlEncoding("UTF-8"); // 设置编码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型
                ftpClient.enterLocalPassiveMode(); // 打开本地被动模式

                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fis);
                }

                uploaded = true;
            } catch (IOException e) {
                logger.error("上传文件异常~", e);
            } finally {
                if (fis != null) {
                    fis.close();
                }
                ftpClient.disconnect();
            }
        }

        return uploaded;
    }

    /**
     * 连接 FTP 服务器（辅助方法）
     */
    private static boolean connectFTPServer(String ip, int port, String username, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();

        try {
            ftpClient.connect(ip, port);
            isSuccess = ftpClient.login(username, password); // 登录
            logger.info("连接 FTP 服务器成功！");
        } catch (IOException e) {
            logger.error("连接 FTP 服务器异常~", e);
        }

        return isSuccess;
    }

}
