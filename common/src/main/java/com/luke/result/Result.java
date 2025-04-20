package com.luke.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;


    private static <T> Result<T> build(T data) {
        Result<T> result = new Result<>();
        if (data != null)
            result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> ok(T data){
        return build(data, ResultCodeEnum.SUCCESS);
    }
    public static <T> Result<T> ok(){
        return ok(null);
    }
    public static <T> Result<T> fail(T data){
        return build(data, ResultCodeEnum.FAIL);
    }
    public static <T> Result<T> fail(){
        return fail(null);
    }
}
