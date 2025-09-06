package com.wcw.backend.Common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)   // 禁止 new
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> fail(String msg) {
        return new Result<>(400, msg, null);
    }
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
