package com.imooc.courseedgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class CourseEdgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseEdgeServiceApplication.class, args);
    }
}
