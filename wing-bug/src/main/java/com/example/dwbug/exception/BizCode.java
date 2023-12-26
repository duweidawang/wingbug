package com.example.dwbug.exception;


/**
 *
 */
public enum BizCode {

    UNKNOWN_EXCEPTION(10002,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验问题");

    private int code;
    private String msg;
    BizCode(int code,String msg){
        this.code =code;
       this. msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
