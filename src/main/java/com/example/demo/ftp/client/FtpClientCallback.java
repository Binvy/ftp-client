package com.example.demo.ftp.client;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/10 15:22
 */
public interface FtpClientCallback {

    void execute(FTPClient client) throws IOException;

}
