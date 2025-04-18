package com.spark.adminserver.common;

import com.spark.adminserver.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理业务参数异常 (如 Service 层直接抛出的 IllegalArgumentException)
     * 通常建议封装为 BusinessException 抛出，但保留此处理作为兼容
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 明确参数错误返回 400
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 处理 @Validated 注解校验失败的异常 (用于 @RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("请求体参数校验失败: {}", errorMsg);
        return Result.fail(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + errorMsg);
    }

    /**
     * 处理 @Validated 注解校验失败的异常 (用于 @RequestParam, @PathVariable, 或对象参数绑定)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        log.warn("请求参数绑定校验失败: {}", errorMsg);
        return Result.fail(HttpStatus.BAD_REQUEST.value(), "参数绑定失败: " + errorMsg);
    }

    /**
     * 处理不支持的 HTTP 方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方法: Method={}, URI={}", e.getMethod(), e.getMessage().split("'?")[1]); // Extract URI if possible
        return Result.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), String.format("不支持的请求方法: %s", e.getMethod()));
    }

    /**
     * 处理其他未捕获的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("未捕获的运行时异常: ", e); // 记录详细堆栈信息
        // 不将具体异常信息暴露给前端
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误，请联系管理员");
    }

    /**
     * 处理顶级异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        // 不将具体异常信息暴露给前端
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统发生未知错误，请联系管理员");
    }
} 