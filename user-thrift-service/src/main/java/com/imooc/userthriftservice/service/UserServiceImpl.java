package com.imooc.userthriftservice.service;

import com.imooc.thrift.user.UserInfo;
import com.imooc.thrift.user.UserService;
import com.imooc.userthriftservice.dao.UserDao;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService.Iface {

    @Autowired
    UserDao userDao;

    @Override
    public UserInfo getUserById(int id) throws TException {
        return userDao.getUserById(id);
    }

    @Override
    public UserInfo getUserByName(String username) throws TException {
        System.out.println("==========ssss=========");
        return userDao.getUserByName(username);

    }

    @Override
    public void registerUser(UserInfo userInfo) throws TException {
        userDao.registerUser(userInfo);
    }
}
