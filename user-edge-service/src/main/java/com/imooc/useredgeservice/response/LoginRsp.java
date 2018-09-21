package com.imooc.useredgeservice.response;

public class LoginRsp extends Response {
    private String token;

    public LoginRsp(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
