package com.sdj.sdjtest.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sdj.sdjtest.dto.CourseDTO;
import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.entity.CourseUser;
import com.sdj.sdjtest.mapper.CourseMapper;
import com.sdj.sdjtest.service.CourseService;
import com.sdj.sdjtest.vo.CourseUserVO;
import com.sdj.sdjtest.vo.CourseVO;
@Service
public class CourseServiceImpl implements CourseService{
    @Autowired
    private CourseMapper courseMapper;
    @Override
    public boolean addCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseType(courseDTO.getCourseType());
        course.setTeacherName(courseDTO.getTeacherName());
        course.setPayType(courseDTO.getPayType());
        int result =  courseMapper.addCourse(course);
        if(result > 0){
            return true;
        } else {
            return false;
        }
    }
    @Override
    public PageInfo<Course> findCourseByName(Integer page, Integer limit, String courseName, String teacherName) {
        PageHelper.startPage(page, limit);
        List<Course> courseList = courseMapper.selectCourseByName(courseName, teacherName);
        PageInfo<Course> pageInfo = new PageInfo<>(courseList);
        courseList.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtils.copyProperties(course, courseVO);
            return courseVO;
        }).collect(Collectors.toList());
        return pageInfo;
    }
    @Override
    public boolean deleteCourse(Long id) {
        int result = courseMapper.deleteCourse(id);
        if(result > 0){
            return true;
        } else {
            return false;
        }
    }
    @Override
    public Course selectCourseById(Long id) {
        Course course = courseMapper.selectCourseById(id);
        return course;
    }
    @Override
    public PageInfo<CourseUser> selectCourseUserById(Integer page, Integer limit, Long id) {
        PageHelper.startPage(page, limit);
        List<CourseUser> courseUserList = courseMapper.selectCourseUserById(id);
        PageInfo<CourseUser> pageInfo = new PageInfo<>(courseUserList);
        courseUserList.stream().map(courseUser -> {
            CourseUserVO courseUserVO = new CourseUserVO();
            BeanUtils.copyProperties(courseUser, courseUserVO);
            return courseUserVO;
        }).collect(Collectors.toList());
        return pageInfo;
    }
    @Override
    public boolean updateCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseType(courseDTO.getCourseType());
        course.setTeacherName(courseDTO.getTeacherName());
        course.setPayType(courseDTO.getPayType());
        int result = courseMapper.updateCourse(course);
        if(result > 0){
            return true;
        } else {
            return false;
        }
    }
}
