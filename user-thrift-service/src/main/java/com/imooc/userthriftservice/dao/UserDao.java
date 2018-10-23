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

    @Select("SELECT id, username, password, real_name realName, mobile, email FROM pe_user " +
            "WHERE id = #{id}")
    UserInfo getUserById(@Param("id") int id);

    @Select("SELECT id, username, password, real_name realName, mobile, email FROM pe_user " +
            "WHERE username = #{name}")
    UserInfo getUserByName(@Param("name") String name);

    @Insert("INSERT INTO pe_user(id, username, password, real_name, mobile, email) " +
            "VALUES(#{id}, #{username}, #{password}, #{realName}, #{mobile}, #{email})")
    void registerUser(UserInfo userInfo);

    @Select("SELECT u.id, u.username, u.password, u.real_name realName, " +
            "u.mobile, u.email, t.intro, t.stars FROM pe_user u, pe_teacher t WHERE u.id = #{id} " +
            "AND u.id = t.user_id")
    UserInfo getTeacherById(@Param("id") int id);
}
