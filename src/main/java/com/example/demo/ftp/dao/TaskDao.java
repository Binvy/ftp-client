package com.example.demo.ftp.dao;

import com.example.demo.ftp.model.FtpTask;

import java.util.List;

/**
 * @author hanbinwei
 * @date 2022/3/7 15:01
 */
public interface TaskDao {

    /**
     * 获取任务列表
     * @return task list
     * @throws Exception
     */
    List<FtpTask> getTaskList() throws Exception;

    /**
     * 根据名称获取任务
     * @param taskName
     * @return task
     * @throws Exception
     */
    FtpTask getTaskByName(String taskName) throws Exception;

    /**
     * 新增任务
     * @param task
     * @return true-新增成功，false-新增失败
     * @throws Exception
     */
    boolean addTask(FtpTask task) throws Exception;

    /**
     * 批量新增任务
     * @param taskList
     * @return true-新增成功，false-新增失败
     * @throws Exception
     */
    boolean addTaskBatch(List<FtpTask> taskList) throws Exception;

    /**
     * 删除任务
     * @param taskName 任务名
     * @return true-删除成功，false-删除失败
     * @throws Exception
     */
    boolean deleteTask(String taskName) throws Exception;

    /**
     * 修改任务
     * @param task
     * @return true-修改成功，false-修改失败
     * @throws Exception
     */
    boolean updateTask(FtpTask task) throws Exception;

    /**
     * 执行成功
     * @param task
     * @param message
     * @return
     * @throws Exception
     */
    boolean success(FtpTask task, String message) throws Exception;

    /**
     * 执行失败
     * @param task
     * @param message
     * @return
     * @throws Exception
     */
    boolean failed(FtpTask task, String message) throws Exception;


}
