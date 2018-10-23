package com.imooc.useredgeservice.controller;


import com.imooc.dto.TeacherDTO;
import com.imooc.thrift.message.MessageService;
import com.imooc.thrift.user.UserInfo;
import com.imooc.thrift.user.UserService;
import com.imooc.useredgeservice.dto.UserDTO;
import com.imooc.useredgeservice.redis.RedisClient;
import com.imooc.useredgeservice.response.LoginRsp;
import com.imooc.useredgeservice.response.Response;
import com.imooc.useredgeservice.thrift.ServiceProvider;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService.Client userClient;

    @Autowired
    private MessageService.Client msgClient;

    @Autowired
    private MessageService.Client mClient;
    @Autowired
    private RedisTemplate<String, String> redisClient;
    @Autowired
    private RedisTemplate<String, UserDTO> redisClientUser;

    @GetMapping("/login")
    public Object login() {
        return "please login first!";
    }

    @PostMapping("/login")
    public Object login(@RequestParam("username") String username,
                      @RequestParam("password") String password) {
        UserInfo userInfo;
        try {
            userInfo = userClient.getUserByName(username);
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
        System.out.println(token);
        UserDTO userDTO = toDTO(userInfo);
        System.out.println(userDTO);
        redisClientUser.opsForValue().set(token, userDTO, 3600, TimeUnit.SECONDS);
        System.out.println(redisClient.opsForValue().get(token));

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
                result = mClient.sendMobileMessage(mobile, message + code);
                redisClient.opsForValue().set(mobile, code);
            } else if(!StringUtils.isEmpty(email)) {
                result = mClient.sendEmailMessage(email, message + code);
                redisClient.opsForValue().set(email, code);
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
            String code = redisClient.opsForValue().get(mobile);
            if (!verifyCode.equals(code)) {
                return Response.VERIFY_CODE_ERROR;
            }
        } else {
            String code = redisClient.opsForValue().get(email);
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
            userClient.registerUser(userInfo);
        } catch (TException e) {
            e.printStackTrace();
            return Response.exception(e);
        }
        return Response.SUCCESS;

    }

    @GetMapping("/authentication")
    public UserDTO authentication(@RequestHeader("token") String token) {
        return redisClientUser.opsForValue().get(token);
    }

    @GetMapping("/teacher")
    public UserInfo getTeacher(@RequestParam("id") int id ) throws TException {
        return userClient.getTeacherById(id);
    }

    @GetMapping("/user")
    public UserInfo getUser(@RequestParam("id") int id ) throws TException {
        return userClient.getUserById(id);
    }

    private UserDTO toDTO(UserInfo userInfo) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userInfo, userDTO);
        System.out.println(userDTO.toString());
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
