package com.imooc.coursedubboservice.dao;

import com.imooc.course.dto.CourseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseDao {

    @Select("SELECT * FROM pe_course")
    List<CourseDTO> listCourse();

    @Select("select user_id from pr_user_course where course_id = #{courseId}")
    Integer getCourseTeacher(@Param("courseId") int courseId);
}
