package com.uin.exception;

/**
 * @author wanglufei
 * @description: TODO
 * @date 2022/4/22/3:12 PM
 */
public enum UinException {
    UNKOWN_EXCEPTION(10000, "系统位置错误"),
    VAILD_EXCEPTION(10001, "参数格式错误");

    private int code;
    private String message;

    UinException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
