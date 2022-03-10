package com.example.demo.ftp.service;

import com.example.demo.ftp.model.FtpTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/10 16:05
 */
@SpringBootTest
public class TaskServiceTests {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceTests.class);

    @Value("${ftp.host:localhost}")
    private String host;
    @Value("${ftp.port:21}")
    private Integer port;
    @Value("${ftp.username:hanbinwei}")
    private String username;
    @Value("${ftp.password:13752330376}")
    private String password;

    private static final String FTP_BASE_PATH = "/";

    private static final String LOCALE_BASE_PATH = "F:\\work\\venus\\file";

    @Autowired
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    public FtpTask defaultTask() {
        FtpTask task = new FtpTask();
        task.setName("test-task-" + LocalDateTime.now());
        task.setHost(this.host);
        task.setPort(this.port);
        task.setUsername(this.username);
        task.setPassword(this.password);
        task.setRemoteBasePath(FTP_BASE_PATH);
        task.setLocalBasePath(LOCALE_BASE_PATH);
        return task;
    }

    @Test
    public void testExecuteInSession() throws Exception {
        taskService.executeInSession(defaultTask());
    }

    @Test
    public void testExecuteInClient() throws Exception{
        taskService.executeInClient(defaultTask());
    }

    @Test
    public void writeJson() throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter();
        File jsonFile = new File(FileUtils.getTempDirectory() + File.separator + "json");
        objectMapper.writeValue(jsonFile, defaultTask());
    }

}
