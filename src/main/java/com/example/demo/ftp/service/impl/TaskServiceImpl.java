package com.example.demo.ftp.service.impl;

import com.example.demo.ftp.dao.TaskDao;
import com.example.demo.ftp.model.FtpTask;
import com.example.demo.ftp.service.TaskService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/9 9:50
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskDao taskDao;

    public TaskServiceImpl(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public boolean executeInSession(FtpTask task) throws Exception {
        taskDao.addTask(task);
        try (FtpSession session = getFtpSession(task)) {
            task.setStartTime(new Date());
            downloadDirectoryRecursively(session, task.getRemoteBasePath(), task.getLocalBasePath());
            taskDao.success(task, null);
            return true;
        } catch (Exception e) {
            logger.error("execute task exception.", e);
            taskDao.failed(task, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean executeInSession(List<FtpTask> task) throws Exception {
        if (CollectionUtils.isEmpty(task)) {
            return true;
        }
        for (FtpTask ftpTask : task) {
            executeInSession(ftpTask);
        }
        return true;
    }

    @Override
    public boolean executeInSession() throws Exception {
        List<FtpTask> taskList = taskDao.getTaskList();
        if (CollectionUtils.isEmpty(taskList)) {
            logger.info("no task to execute.");
            return true;
        }
        List<FtpTask> unsuccessfulTaskList = taskList.stream().filter(task -> task.getStatus() != 1).collect(Collectors.toList());
        return executeInSession(unsuccessfulTaskList);
    }

    @Override
    public boolean executeInClient(FtpTask task) throws Exception {
        taskDao.addTask(task);
        FtpSession session = getFtpSession(task);
        FTPClient client = session.getClientInstance();
        try {
            downloadDirectoryRecursively(client, task.getRemoteBasePath(), task.getLocalBasePath());
            taskDao.success(task, null);
            return true;
        } catch (Exception e) {
            logger.error("execute task exception.", e);
            taskDao.failed(task, e.getMessage());
        } finally {
            session.close();
        }
        return false;
    }

    /**
     * 读取指定目录到本地文件夹
     * @param session
     * @param remoteDir
     * @param localDir
     * @throws IOException
     */
    public void downloadDirectoryRecursively(FtpSession session, String remoteDir, String localDir) throws IOException {
        remoteDir = remoteDir.endsWith("/") ? remoteDir : remoteDir + "/";
        localDir = localDir.endsWith(File.separator) ? localDir : localDir + File.separator;
        localDir = StringUtils.hasLength(localDir) ? localDir : FileUtils.getTempDirectoryPath();
        FTPFile[] files = session.list(remoteDir);
        for (FTPFile file : files) {
            String filename = file.getName();
            if (".".equals(filename) || "..".equals(filename)) {
                continue;
            }
            String remoteFilePath = remoteDir + filename;
            String localFilePath = localDir + filename;
            if (file.isFile()) {
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(localFilePath))) {
                    session.read(remoteFilePath, os);
                } catch (IOException e) {
                    logger.error("download ftp file[{}] exception", remoteFilePath, e);
                }
            } else if (file.isDirectory()) {
                new File(localFilePath).mkdirs();
                downloadDirectoryRecursively(session, remoteFilePath, localFilePath);
            }
        }
    }

    /**
     * 读取指定目录到本地文件夹
     * @param client
     * @param remoteDir
     * @param localDir
     * @throws IOException
     */
    public void downloadDirectoryRecursively(FTPClient client, String remoteDir, String localDir) throws IOException {
        localDir = StringUtils.hasLength(localDir) ? localDir : FileUtils.getTempDirectoryPath();
        remoteDir = remoteDir.endsWith("/") ? remoteDir : remoteDir + "/";
        localDir = localDir.endsWith(File.separator) ? localDir : localDir + File.separator;
        FTPFile[] files = client.listFiles(remoteDir);
        for (FTPFile file : files) {
            String filename = file.getName();
            if (".".equals(filename) || "..".equals(filename)) {
                continue;
            }
            String remoteFilePath = remoteDir + filename;
            String localFilePath = localDir + filename;
            if (file.isFile()) {
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(localFilePath))) {
                    client.retrieveFile(remoteFilePath, os);
                } catch (IOException e) {
                    logger.error("download ftp file[{}] exception", remoteFilePath, e);
                }
            } else if (file.isDirectory()) {
                new File(localFilePath).mkdirs();
                downloadDirectoryRecursively(client, remoteFilePath, localFilePath);
            }
        }
    }

    /**
     * 获取FTPSession
     * @param task 任务配置
     * @return
     */
    public FtpSession getFtpSession(FtpTask task) {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        if (task.getFileType() != null) {
            factory.setFileType(task.getFileType());
        }
        if (task.getCharset() != null) {
            factory.setControlEncoding(task.getCharset());
        }
        if (task.getServerType() != null) {
            FTPClientConfig clientConfig = new FTPClientConfig(task.getServerType());
            factory.setConfig(clientConfig);
        }
        if (task.getBufferSize() != null) {
            factory.setBufferSize(task.getBufferSize());
        }
        factory.setHost(task.getHost());
        if (task.getPort() != null) {
            factory.setPort(task.getPort());
        }
        factory.setUsername(task.getUsername());
        factory.setPassword(task.getPassword());
        if (task.getClientMode() != null) {
            factory.setClientMode(task.getClientMode());
        }
        return factory.getSession();
    }

    /**
     * 获取FTPClient
     * @param task
     * @return
     */
    public FTPClient getFTPClient(FtpTask task) {
        FtpSession session = getFtpSession(task);
        return session.getClientInstance();
    }
}
