package com.example.demo.ftp.dao.impl;

import com.example.demo.ftp.config.FtpProperties;
import com.example.demo.ftp.dao.TaskDao;
import com.example.demo.ftp.model.FtpTask;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/7 15:02
 */
@Service
public class TaskDaoImpl implements TaskDao {

    private static final Logger logger = LoggerFactory.getLogger(TaskDaoImpl.class);

    @Value("${ftp.task.config.path:'classpath:ftp-task.config'}")
    private String ftpTaskConfig;

    private final FtpProperties ftpProperties;

    private final ObjectMapper objectMapper;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    public TaskDaoImpl(FtpProperties ftpProperties, ObjectMapper objectMapper) {
        this.ftpProperties = ftpProperties;
        this.objectMapper = objectMapper;
        this.objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public List<FtpTask> getTaskList() throws Exception {
        readLock.lock();
        try {
            File file = ResourceUtils.getFile(ftpTaskConfig);
            return objectMapper.readValue(file, new TypeReference<List<FtpTask>>() {});
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public FtpTask getTaskByName(String taskName) throws Exception {
        List<FtpTask> taskList = getTaskList();
        return taskList.stream().filter(task -> task.getName().equals(taskName)).findFirst().orElse(null);
    }

    @Override
    public boolean addTask(FtpTask task) throws Exception {
        validateTask(task);
        List<FtpTask> taskList = getTaskList();
        writeLock.lock();
        try {
            FtpTask exist = taskList.stream().filter(t -> t.getName().equals(task.getName())).findFirst().orElse(null);
            Assert.isNull(exist, "任务名不能重复");
            task.setCreateTime(new Date());
            taskList.add(task);
            File file = ResourceUtils.getFile(ftpTaskConfig);
            objectMapper.writeValue(file, taskList);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean addTaskBatch(List<FtpTask> taskList) throws Exception {
        Assert.isTrue(!CollectionUtils.isEmpty(taskList), "任务列表不能为空");
        taskList.forEach(this::validateTask);
        Set<String> taskNames = taskList.stream().map(task -> task.getName()).collect(Collectors.toSet());
        List<FtpTask> records = getTaskList();
        writeLock.lock();
        try {
            boolean existName = records.stream().anyMatch(o -> taskNames.contains(o.getName()));
            Assert.isTrue(!existName, "任务名不能重复");
            records.addAll(taskList);
            File file = ResourceUtils.getFile(ftpTaskConfig);
            objectMapper.writeValue(file, records);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean deleteTask(String taskName) throws Exception {
        List<FtpTask> taskList = getTaskList();
        writeLock.lock();
        try {
            Iterator<FtpTask> iterator = taskList.iterator();
            while (iterator.hasNext()) {
                FtpTask next = iterator.next();
                if (next.getName().equals(taskName)) {
                    iterator.remove();
                    break;
                }
            }
            File file = ResourceUtils.getFile(ftpTaskConfig);
            objectMapper.writeValue(file, taskList);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean updateTask(FtpTask task) throws Exception {
        Assert.notNull(task, "task must not be null");
        List<FtpTask> taskList = getTaskList();
        FtpTask record = taskList.stream().filter(o -> o.getName().equals(task.getName())).findFirst().orElse(null);
        Assert.notNull(record, "task record not exist");
        writeLock.lock();
        try {
            boolean remove = taskList.remove(record);
            Assert.isTrue(remove, "update task failed");

            taskList.add(task);
            File file = ResourceUtils.getFile(ftpTaskConfig);
            objectMapper.writeValue(file, taskList);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean success(FtpTask task, String message) throws Exception {
        Assert.notNull(task, "task is null");
        task.setStatus(1);
        task.setMessage(message);
        task.setRemark("执行成功");
        task.setUpdateTime(new Date());
        updateTask(task);
        return true;
    }

    @Override
    public boolean failed(FtpTask task, String message) throws Exception {
        Assert.notNull(task, "task is null");
        task.setStatus(-1);
        task.setMessage(message);
        task.setRemark("执行失败");
        task.setUpdateTime(new Date());
        updateTask(task);
        return true;
    }

    /**
     * 校验任务
     * @param task
     */
    public void validateTask(FtpTask task) {
        Assert.notNull(task, "任务不能为空");
        Assert.hasText(task.getName(), "任务名称不能为空");
        Assert.hasText(task.getHost(), "连接地址不能为空");
        Assert.hasText(task.getUsername(), "登录用户名不能为空");
        Assert.hasText(task.getPassword(), "登录密码不能为空");
        Assert.hasText(task.getRemoteBasePath(), "远程文件目录不能为空");
    }

}
