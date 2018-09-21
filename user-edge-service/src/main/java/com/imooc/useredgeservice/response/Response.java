package com.imooc.useredgeservice.response;

import java.io.Serializable;

public class Response implements Serializable {

    public static final Response USERNAME_INVALID = new Response("1001", "用户名不存在！");
    public static final Response USERNAME_PASSWORD_INVALID = new Response("1002", "用户名或密码错误！");
    private String code;
    private String message;

    public Response() {
        this.code = "200";
        message = "success";
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
