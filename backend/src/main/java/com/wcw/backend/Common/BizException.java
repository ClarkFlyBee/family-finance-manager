package com.wcw.backend.Common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BizException extends RuntimeException{
    private final int code;
    private final String message;
}
