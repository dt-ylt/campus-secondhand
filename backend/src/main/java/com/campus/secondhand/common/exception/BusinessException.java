package com.campus.secondhand.common.exception;

import lombok.Getter;

/**
 * 自定义业务异常
 *
 * 业务逻辑出错时抛出，比如"用户名已存在"、"密码错误"、"商品不存在"。
 * 继承 RuntimeException（运行时异常）：好处是不用在方法签名上写 throws，代码更干净。
 *
 * 用法：throw new BusinessException("用户名已存在");
 *      throw new BusinessException(401, "未登录");
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 状态码，默认 400（表示请求无法通过，如参数/业务校验失败） */
    private final Integer code;

    /** 只传提示信息，状态码默认 400 */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /** 传状态码 + 提示信息，用于特殊的 401未登录 / 403无权限 等 */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
