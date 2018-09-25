package com.imooc.useredgeservice.response;

import java.io.Serializable;

public class Response implements Serializable {

    public static final Response SUCCESS = new Response();

    public static final Response USERNAME_INVALID = new Response("1001", "用户名不存在！");
    public static final Response USERNAME_PASSWORD_INVALID = new Response("1002", "用户名或密码错误！");
    public static final Response VERIFY_CODE_ERROR = new Response("1003", "验证码错误");
    public static final Response MOBILE_OR_EMAIL_REQUIRED = new Response("2001", "需要电话或邮箱");
    public static final Response SEND_VERIFY_CODE_FAILED = new Response("2002", "验证码发送失败");

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

    public static Response exception(Exception e) {
        return new Response("9999", e.getMessage());
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
