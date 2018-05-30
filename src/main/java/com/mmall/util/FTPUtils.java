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

    private static final String FTP_IP = PropertiesUtil.getProperty("ftp.ip");
    private static final String FTP_USERNAME = PropertiesUtil.getProperty("ftp.username");
    private static final String FTP_PASSWORD = PropertiesUtil.getProperty("ftp.password");

    private String ip;
    private String username;
    private String password;
    private static FTPClient ftpClient;

    public FTPUtils(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static FTPClient getFtpClient() {
        return ftpClient;
    }

    public static void setFtpClient(FTPClient ftpClient) {
        FTPUtils.ftpClient = ftpClient;
    }

    /**
     * 文件上传到 FTP 服务器
     *
     * @param fileList 文件列表，支持批量上传
     */
    public static boolean uploadFile2FTP(List<File> fileList) throws IOException {

        FTPUtils ftp = new FTPUtils(FTP_IP, FTP_USERNAME, FTP_PASSWORD);

        logger.info("开始连接 FTP 服务器");
        boolean result = ftp.uploadFile(fileList, "img");
        logger.info("FTP 服务器上传结果：{}", result);

        return result;
    }

    /**
     * 文件上传到 FTP 服务器（辅助方法）
     *
     * @param fileList   文件列表，支持批量上传
     * @param remotePath 远端存储路径
     */
    private boolean uploadFile(List<File> fileList, String remotePath) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;

        if (connectFTPServer(this.ip, this.username, this.password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath); // 更改工作目录
                ftpClient.setBufferSize(1024); // 设置缓冲区
                ftpClient.setControlEncoding("UTF-8"); // 设置编码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型
                ftpClient.enterLocalPassiveMode(); // 打开本地被动模式

                logger.info("开始存文件");
                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fis);
                }

            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常", e);
                e.printStackTrace();
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
    private boolean connectFTPServer(String ip, String username, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();

        try {
            ftpClient.connect(ip); // 连接
            isSuccess = ftpClient.login(username, password); // 登录
        } catch (IOException e) {
            logger.error("连接 FTP 服务器异常", e);
        }

        return isSuccess;
    }

}
