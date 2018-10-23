package com.imooc.courseedgeservice.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.imooc.course.dto.CourseDTO;
import com.imooc.course.service.CourseService;
import com.imooc.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Reference
    private CourseService courseService;

    @GetMapping("/courseList")
    public List<CourseDTO> courseList(HttpServletRequest request) {
        UserDTO userDTO = (UserDTO) request.getAttribute("user");
        System.out.println("====");
        System.out.println(userDTO.toString());
        return courseService.courseList();
    }
}
