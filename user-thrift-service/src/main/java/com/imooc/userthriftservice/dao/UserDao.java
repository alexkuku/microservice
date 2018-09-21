package com.imooc.userthriftservice.dao;

import com.imooc.thrift.user.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {

    @Select("SELECT id, username, password, real_name realName, mobile, email FROM pe_user" +
            "WHERE id = #{id}")
    UserInfo getUserById(@Param("id") int id);

    @Select("SELECT id, username, password, real_name realName, mobile, email FROM pe_user" +
            "WHERE username = #{name}")
    UserInfo getUserByName(@Param("name") String name);

    @Insert("INSERT INTO pe_user(id, username, password, real_name, mobile, email) " +
            "VALUES(#{id}, #{userName}, #{password}, #{realName}, #{mobile}, #{email})")
    void registerUser(UserInfo userInfo);
}
