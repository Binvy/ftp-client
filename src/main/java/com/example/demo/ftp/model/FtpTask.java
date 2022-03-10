package com.example.demo.ftp.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * FtpTask实体
 *
 * @author hanbinwei
 * @date 2022/3/8 10:38
 */
@Data
public class FtpTask {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 系统类型，默认UNIX
     */
    private String serverType;

    /**
     * FTP连接地址，默认：localhost
     */
    private String host;

    /**
     * FTP连接端口，默认：21
     */
    private Integer port;

    /**
     * FTP连接用户名
     */
    private String username;

    /**
     * FTP连接密码
     */
    private String password;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 客户端模式： 0: active-主动 1: passive-被动，默认：1
     */
    private Integer clientMode;

    /**
     * 编码，默认：UTF-8
     */
    private String charset;

    /**
     * 任务类型：
     *    upload: 上传
     *    download: 下载
     *    delete: 删除
     */
    private String operationType;

    /**
     * FTP服务器基础路径
     */
    private String remoteBasePath;

    /**
     * 文件夹层级
     */
    private Integer deepLevel;

    /**
     * 本地基础路径
     */
    private String localBasePath;

    /**
     * 包含的文件夹列表
     */
    private List<String> includeDir;

    /**
     * 包含的文件后缀列表
     */
    private List<String> includeFileSuffix;

    /**
     * 不包含的文件夹列表
     */
    private List<String> excludeDir;

    /**
     * 不包含的文件后缀列表
     */
    private List<String> excludeFileSuffix;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 是否显示隐藏文件
     */
    private boolean listHiddenFiles;

    /**
     * 解析文件的正则表达式，默认解析器，返回列表为空。
     */
    private String parseRegex;

    /**
     * bufferSize
     */
    private Integer bufferSize;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 开始用户
     */
    private String startUser;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 任务状态： -1-执行失败 0-初始值（默认） 1-执行成功
     */
    private Integer status;

    /**
     * 消息
     */
    private String message;

    /**
     * 备注
     */
    private String remark;

}
