package com.campus.secondhand.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * 所有接口都返回这个对象，格式统一为：{ "code": 200, "message": "success", "data": ... }
 * 前端处理时逻辑统一：看 code 是不是 200，是就取 data，不是就提示 message。
 *
 * @param <T> 泛型：data 字段可以是任意类型（User、List<Product> 等），写代码时再具体指定
 */
@Data
public class Result<T> implements Serializable {

    /** 状态码：200成功，401未登录/token失效，403无权限，500服务器错误 */
    private Integer code;

    /** 提示信息，成功是 "success"，失败是具体原因 */
    private String message;

    /** 返回的数据，类型由泛型 T 决定 */
    private T data;

    /**
     * 成功（不带数据）
     * 用于注册、删除这类不需要返回内容的操作
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    /**
     * 成功（带数据）
     * 用于查询类操作，把查到的数据塞进 data
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 失败（自定义状态码 + 提示）
     * 比如 Result.error(401, "未登录")、Result.error(403, "无权限")
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败（默认 500 服务器错误，只传提示信息）
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
