package com.example.demo.ftp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FTP任务配置类
 * @author hanbinwei
 * @date 2022/3/7 14:15
 */
@ConfigurationProperties(prefix = "ftp")
@Data
public class FtpProperties {

    private final List<Task> task = new ArrayList<>();

    @Data
    public static class Task {

        private String name;

        private String host = "localhost";

        private Integer port = 21;

        private String username;

        private String password;

        private String clientMode;

        private String charset;

        private String remoteBasePath;

        private Integer deepLevel;

        private String localBasePath;

        private List<String> includeDir = new ArrayList<>(Collections.singleton("*"));

        private List<String> includeFileSuffix = new ArrayList<>(Collections.singleton("*"));

        private List<String> excludeDir;

        private List<String> excludeFileSuffix;

    }
}
