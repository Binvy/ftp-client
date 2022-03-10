package com.example.demo.ftp.controller;

import com.example.demo.ftp.model.ApiResult;
import com.example.demo.ftp.model.FtpTask;
import com.example.demo.ftp.dao.TaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Ftp任务相关
 * @author hanbinwei
 * @date 2022/3/7 15:33
 */
@RestController("/ftp/task")
@RequestMapping("/ftp/task")
public class FtpTaskController {

    private static final Logger logger = LoggerFactory.getLogger(FtpTaskController.class);

    private final TaskDao ftpTaskDao;

    public FtpTaskController(TaskDao ftpTaskDao) {
        this.ftpTaskDao = ftpTaskDao;
    }

    @GetMapping("/list")
    public ApiResult getTaskList() {
        HttpStatus status;
        List<FtpTask> taskList = null;
        try {
            taskList = ftpTaskDao.getTaskList();
            status = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("get task list exception", e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ApiResult(status.value(), status.getReasonPhrase(), taskList);
    }

    @GetMapping("/query")
    public ApiResult getTaskByName(String taskName) {
        HttpStatus status;
        FtpTask task = null;
        try {
            task = ftpTaskDao.getTaskByName(taskName);
            status = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("get task by name exception. taskName:{}", taskName, e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ApiResult(status.value(), status.getReasonPhrase(), task);
    }

    @PostMapping("/add")
    public ApiResult addTask(@RequestBody FtpTask task) {
        HttpStatus status;
        boolean success = false;
        try {
            success = ftpTaskDao.addTask(task);
            status = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("add task exception. task:{}", task, e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ApiResult(status.value(), status.getReasonPhrase(), success);
    }

    @PostMapping("/delete")
    public ApiResult deleteTask(String taskName) {
        HttpStatus status;
        boolean success = false;
        try {
            success = ftpTaskDao.deleteTask(taskName);
            status = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("delete task exception. taskName:{}", taskName, e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ApiResult(status.value(), status.getReasonPhrase(), success);
    }

    @PostMapping("/update")
    public ApiResult updateTask(@RequestBody FtpTask task) {
        HttpStatus status;
        boolean success = false;
        try {
            success = ftpTaskDao.updateTask(task);
            status = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("update task exception. task:{}", task, e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ApiResult(status.value(), status.getReasonPhrase(), success);
    }

    @GetMapping("/config")
    public String execute() {
        return "ftp task execute success";
    }

}
