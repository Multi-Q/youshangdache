package com.qrh.youshangdache.common.result;


import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> {

    //返回码
    private Integer code;

    //返回消息
    private String message;

    //返回数据
    private T data;

    public Result() {
    }

    // 返回数据
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    /**
     * @param body    需要返回的数据
     * @param code    响应码
     * @param message 响应消息
     * @param <T>     泛型，任意类型
     * @return JSON格式的响应数据
     */
    public static <T> Result<T> build(T body, Integer code, String message) {
        Result<T> result = build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * @param body           需要返回的数据
     * @param resultCodeEnum 统一返回结果状态信息枚举类对象
     * @param <T>            泛型，任意类型
     * @return JSON格式的响应数据
     */
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    /**
     * 操作成功
     *
     * @return JSON格式的响应数据
     */
    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    /**
     * 操作成功
     *
     * @param data 需要返回的数据
     * @return JSON格式的响应数据
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }
    /**
     * 操作失败
     *
     * @return JSON格式的响应数据
     */
    public static <T> Result<T> fail() {
        return Result.fail(null);
    }

    /**
     * 操作失败
     *
     * @param data JSON格式的响应数据
     * @return JSON格式的响应数据
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg) {
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }
}
