package com.luke.result;

import lombok.Getter;


@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201,"失败"),
    NOT_LOGIN(202,"用户未登录");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code,String message) {
        this.message = message;
        this.code = code;
    }
}