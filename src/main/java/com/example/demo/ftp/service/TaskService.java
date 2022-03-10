package com.example.demo.ftp.service;

import com.example.demo.ftp.model.FtpTask;

import java.io.IOException;
import java.util.List;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/9 9:49
 */
public interface TaskService {

    /**
     * 执行任务
     * @param task
     * @return
     * @throws Exception
     */
    boolean executeInSession(FtpTask task) throws Exception;

    /**
     * 执行任务列表
     * @param task
     * @return
     * @throws Exception
     */
    boolean executeInSession(List<FtpTask> task) throws Exception;

    /**
     * 执行已配置的任务
     * @return
     * @throws Exception
     */
    boolean executeInSession() throws Exception;

    /**
     * 执行任务
     * @param task
     * @return
     * @throws Exception
     */
    boolean executeInClient(FtpTask task) throws Exception;

}
