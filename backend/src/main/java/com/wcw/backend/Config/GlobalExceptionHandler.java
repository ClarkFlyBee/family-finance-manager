package com.wcw.backend.Config;

import com.wcw.backend.Common.BizException;
import com.wcw.backend.Common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

@Slf4j
@RestControllerAdvice // = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    /* 参数检验失败（@Valid 失败） */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败: {}", msg);
        return Result.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /* 业务自定义异常（可选） */
    @ExceptionHandler(BizException.class)
    public Result<String> handleBiz(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /* 所有未捕获异常 */
    @ExceptionHandler(Exception.class)
    public Result<String> handleAll(Exception e){
        log.error("系统异常", e);
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}
