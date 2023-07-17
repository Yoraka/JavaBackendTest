package com.sdj.sdjtest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.sdj.sdjtest.dto.CourseDTO;
import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.entity.CourseUser;
@Service
public interface CourseService {

    boolean addCourse(CourseDTO courseDTO);

    PageInfo<Course> findCourseByName(Integer page, Integer limit, String courseName, String teacherName);

    boolean deleteCourse(Long id);

    Course selectCourseById(Long id);

    PageInfo<CourseUser> selectCourseUserById(Integer page, Integer limit, Long id);
    
}
