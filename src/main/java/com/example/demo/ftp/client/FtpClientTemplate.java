package com.example.demo.ftp.client;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/10 15:12
 */
@Data
@Builder
public class FtpClientTemplate implements FtpOperations {

    private FTPClientConfig config;

    private String hostname;
    private Integer port = 21;
    private String username;
    private String password;
    private Integer clientMode;
    private String charset;
    private String fileType;

    public void execute(FtpClientCallback callback) throws IOException {
        FTPClient client = new FTPClient();
        if (this.config != null) {
            client.configure(this.config);
        }
        if (this.charset != null) {
            client.setControlEncoding(this.charset);
        }
        client.connect(this.hostname, this.port);
        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {

        }
    }


}
