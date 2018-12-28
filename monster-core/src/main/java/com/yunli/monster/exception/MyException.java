package com.yunli.monster.exception;

/**
 * 异常处理
 *
 * @author zhouchao
 * @create 2018-12-28 9:28
 */
public class MyException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 异常构造器
     * @param message
     */
    public MyException(String message) {
        super(message);
    }

    /**
     * 构造器重载，主要是自己考虑某些异常自定义一些返回码
     *
     * @param code
     * @param message
     */
    public MyException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
