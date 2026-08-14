package com.campus.secondhand.common.exception;

import com.campus.secondhand.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 任何 Controller 抛出的异常都会被这里拦截，统一转成 Result 返回给前端。
 * 前端因此永远收到 {code, message, data} 格式，不会收到 Spring 默认的错误堆栈。
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   - @ControllerAdvice：声明这是个全局控制器增强，能拦截所有 Controller
 *   - @ResponseBody：让返回的 Result 自动转成 JSON 放进响应体
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     * 业务代码里 throw new BusinessException("xxx") 会被这里接住
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * 当 Controller 方法参数加了 @Valid，而请求参数不满足校验注解(如 @NotBlank)时触发
     * 把校验失败的提示信息取出来返回，而不是返回一坨校验错误对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数错误";
        log.warn("参数校验失败：{}", message);
        return Result.error(400, message);
    }

    /**
     * 兜底：处理所有其他未预料到的异常
     * 比如空指针、数据库连接失败等。这类异常不暴露给前端细节，统一返回"系统繁忙"，
     * 但用 log.error 把完整堆栈记到日志里，方便开发者排查。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
