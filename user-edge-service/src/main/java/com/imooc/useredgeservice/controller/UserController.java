package com.imooc.useredgeservice.controller;


import com.imooc.thrift.user.UserInfo;
import com.imooc.useredgeservice.dto.UserDTO;
import com.imooc.useredgeservice.redis.RedisClient;
import com.imooc.useredgeservice.response.LoginRsp;
import com.imooc.useredgeservice.response.Response;
import com.imooc.useredgeservice.thrift.ServiceProvider;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.util.Random;

@RestController
public class UserController {

    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private RedisClient redisClient;

    @PostMapping("/login")
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

        redisClient.set(token, toDTO(userInfo), 3600);
        return new LoginRsp(token);
    }

    @GetMapping("/code/send")
    public Object SendVerifyCode(@RequestParam(value = "mobile", required = false) String mobile,
                                 @RequestParam(value = "email",required = false) String email) {
        String message = "Verify code is :";
        String code = randomCode("0123456789", 4);
        try {
            boolean result;
            if (!StringUtils.isEmpty(mobile)) {
                result = serviceProvider.getMessageService().sendMobileMessage(mobile, message + code);
                redisClient.set(mobile, code);
            } else if(!StringUtils.isEmpty(email)) {
                result = serviceProvider.getMessageService().sendEmailMessage(email, message + code);
                redisClient.set(email, code);
            } else {
                return Response.MOBILE_OR_EMAIL_REQUIRED;
            }
            if (!result) {
                return Response.SEND_VERIFY_CODE_FAILED;
            }
        } catch (TException e) {
            e.printStackTrace();
            return Response.exception(e);
        }
        return Response.SUCCESS;
    }

    @PostMapping("/register")
    public Object register(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "mobile", required = false) String mobile,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam("verify_code") String verifyCode) {
        if (StringUtils.isEmpty(mobile) && StringUtils.isEmpty(email)) {
            return Response.MOBILE_OR_EMAIL_REQUIRED;
        }
        if (!StringUtils.isEmpty(mobile)) {
            String code = redisClient.get(mobile);
            if (!verifyCode.equals(code)) {
                return Response.VERIFY_CODE_ERROR;
            }
        } else {
            String code = redisClient.get(email);
            if (!verifyCode.equals(code)) {
                return Response.VERIFY_CODE_ERROR;
            }
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(md5(password));
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        try {
            serviceProvider.getUserService().registerUser(userInfo);
        } catch (TException e) {
            e.printStackTrace();
            return Response.exception(e);
        }
        return Response.SUCCESS;

    }

    private UserDTO toDTO(UserInfo userInfo) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userInfo, userDTO);
        return userDTO;
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
            return HexUtils.toHexString(md5Bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
