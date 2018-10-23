package com.imooc.coursedubboservice.ServiceImpl;


import com.alibaba.dubbo.config.annotation.Service;
import com.imooc.course.dto.CourseDTO;
import com.imooc.course.service.CourseService;
import com.imooc.coursedubboservice.dao.CourseDao;
import com.imooc.dto.TeacherDTO;
import com.imooc.thrift.user.UserInfo;
import org.apache.thrift.TException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private ServiceProvider serviceProvider;


    @Override
    public List<CourseDTO> courseList() {
        List<CourseDTO> courseDTOS = courseDao.listCourse();
        if (courseDTOS != null) {
            for (CourseDTO courseDTO : courseDTOS) {
                System.out.println("courseDTO:" + courseDTO.toString());
                Integer teacherId = courseDao.getCourseTeacher(courseDTO.getId());
                System.out.println("teacherId:" + teacherId);
                if (teacherId != null) {
                    try {
                        UserInfo userInfo = serviceProvider.getUserService().getTeacherById(teacherId);
                        System.out.println(userInfo.toString());
                        courseDTO.setTeacherDTO(trans2Teacher(userInfo));
                    } catch (TException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return courseDTOS;
    }

    private TeacherDTO trans2Teacher(UserInfo userInfo) {

        TeacherDTO teacherDTO = new TeacherDTO();
        BeanUtils.copyProperties(userInfo, teacherDTO);
        return teacherDTO;
    }
}
