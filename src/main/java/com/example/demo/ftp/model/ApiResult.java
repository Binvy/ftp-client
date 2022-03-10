package com.example.demo.ftp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc
 *
 * @author hanbinwei
 * @date 2022/3/8 13:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    private int code;

    private String message;

    private T data;

}