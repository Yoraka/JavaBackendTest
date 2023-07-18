package com.sdj.sdjtest.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.sdj.sdjtest.dto.CourseDTO;
import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.entity.CourseUser;
import com.sdj.sdjtest.service.CourseService;
import com.sdj.sdjtest.vo.Result;
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@RestController
public class CourseController {
    @Autowired
    private CourseService courseService;
    //添加课程
    @RequiresGuest
    @PostMapping("/addCourse")
    public ResponseEntity<Result> addCourse(@RequestBody CourseDTO courseDTO){
        boolean success = courseService.addCourse(courseDTO);
        if(success){
            return ResponseEntity.status(200).body(Result.ok("添加成功"));
        } else {
            return ResponseEntity.status(200).body(Result.error("添加失败"));
        }
    }
    //查询课程
    @RequiresGuest
    @GetMapping("/selectCourse")
    public ResponseEntity<Result> selectCourse (@RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "10") Integer limit,
                                                String courseName,
                                                String teacherName){
        PageInfo<Course> pageInfo = courseService.findCourseByName(page, limit,
        courseName, teacherName);
        return ResponseEntity.ok(Result.ok("查询成功",pageInfo));
    }
    //修改课程
    @RequiresGuest
    @PostMapping("/updateCourse")
    public ResponseEntity<Result> updateCourse(@RequestBody CourseDTO courseDTO){
        boolean success = courseService.updateCourse(courseDTO);
        return success ? ResponseEntity.status(200).body(Result.ok("修改成功")) :
        ResponseEntity.status(200).body(Result.error("修改失败"));
    }
    //删除课程
    @RequiresGuest
    @GetMapping("deleteCourse")
    public ResponseEntity<Result>deleteCourse(Long id){
        boolean success = courseService.deleteCourse(id);
        return success ? ResponseEntity.status(200).body(Result.ok("删除成功")) :
        ResponseEntity.status(200).body(Result.error("删除失败"));
    }
    //根据课程id查询课程course表信息
    @RequiresGuest
    @GetMapping("/selectCourseById")
    public ResponseEntity<Result> selectCourseById(Long id){
        Course course = courseService.selectCourseById(id);
        return ResponseEntity.ok(Result.ok("查询成功",course));
    }
    //根据课程id查询课程course_user表信息
    @RequiresGuest
    @GetMapping("/selectCourseUserById")
    public ResponseEntity<Result> selectCourseUserById(@RequestParam(required = false,defaultValue = "1") Integer page,
                                                       @RequestParam(required = false,defaultValue = "10") Integer limit,
                                                       Long id){
        PageInfo<CourseUser> courseUserPage = courseService.selectCourseUserById(page, limit, id);
        return ResponseEntity.ok(Result.ok("查询成功",courseUserPage));
    }
}
