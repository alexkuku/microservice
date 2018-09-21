package com.imooc.useredgeservice.controller;


import com.imooc.thrift.user.UserInfo;
import com.imooc.useredgeservice.response.Response;
import com.imooc.useredgeservice.thrift.ServiceProvider;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.util.Random;

@RestController
public class UserController {

    @Autowired
    private ServiceProvider serviceProvider;

    public Object login(@RequestParam("username") String username,
                      @RequestParam("password") String password) {
        UserInfo userInfo;
        try {
            userInfo = serviceProvider.getUserService().getUserByName(username);
        } catch (TException e) {
            e.printStackTrace();
            return Response.USERNAME_INVALID;
        }
        if (userInfo == null) {
            return Response.USERNAME_INVALID;
        }
        if (!userInfo.getPassword().equalsIgnoreCase(md5(password))) {
            return Response.USERNAME_PASSWORD_INVALID;
        }

        String token = genToken();

    }

    private String genToken() {
        return randomCode("0123456789abcdefghijklmnopqrstuvwxyz", 32);

    }

    private String randomCode(String s, int size) {
        StringBuffer result = new StringBuffer(size);
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int loc = random.nextInt(s.length());
            result.append(s.charAt(loc));
        }
        return result.toString();
    }

    private String md5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(password.getBytes());
            return HexUtils.toHexString(md5Bytes)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
