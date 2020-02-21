package com.luxiang.swaggerdemo.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "响应对象")
public class ResponseResult<T> {
    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MESSAGE = "成功";

    @ApiModelProperty(value = "响应码", name = "code", required = true, example = "" + SUCCESS_CODE)
    private int code;

    @ApiModelProperty(value = "响应消息", name = "msg", required = true, example = SUCCESS_MESSAGE)
    private String msg;

    @ApiModelProperty(value = "响应数据", name = "data")
    private T data;

    public static <T> ResponseResult<T> ok(int code, String msg, T data) {
        return new ResponseResult<T>(code, msg, data);
    }

    public static <T> ResponseResult<T> ok(String msg, T data) {
        return new ResponseResult<>(200, msg, data);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return new ResponseResult<>(200, "请求成功", data);
    }

    public static <T> ResponseResult ok(String msg) {
        return new ResponseResult<T>(200, msg, null);
    }

    public static <T> ResponseResult<T> fail(int code, String msg, T data) {
        return new ResponseResult<T>(code, msg, data);
    }

    public static <T> ResponseResult<T> fail(String msg, T data) {
        return new ResponseResult<>(200, msg, data);
    }

    public static <T> ResponseResult<T> fail(T data) {
        return new ResponseResult<>(200, "请求失败", data);
    }

    public static <T> ResponseResult fail(String msg) {
        return new ResponseResult<T>(200, msg, null);
    }

    public ResponseResult() {
    }

    public ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
