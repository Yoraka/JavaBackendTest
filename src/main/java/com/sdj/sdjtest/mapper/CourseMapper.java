package com.sdj.sdjtest.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.entity.CourseUser;

@Repository
public interface CourseMapper {
    @Insert("insert into course(course_name,teacher_name,course_type,pay_type) values(#{courseName},#{teacherName},#{courseType},#{payType})")
    int addCourse(Course course);
    @Select("<script>" +
            "select id,course_name,teacher_name,course_type,pay_type,state from course"+
            "<where>" +
            "<if test ='courseName!=null and courseName !=\" \"'>" +
            " and course_name LIKE '%${courseName}%'" +
            "</if>"+
            "<if test ='teacherName!=null and teacherName !=\" \"'>" +
            " and teacher_name LIKE '%${teacherName}%'" +
            "</if>"+
            "and state = 0"+
            "</where>"+
            "</script>")
    List<Course> selectCourseByName(String courseName, String teacherName);
    @Update("update course set state = 1 where id = #{id}")
    int deleteCourse(Long id);
    @Select("select id,course_name,teacher_name,course_type,pay_type,state from course where id = #{id}")
    Course selectCourseById(Long id);
    @Select("select * from course_user where course_id = #{id} and state = 0")
    List<CourseUser> selectCourseUserById(Long id);
    @Update("update course set course_name = #{courseName},teacher_name = #{teacherName},course_type = #{courseType},pay_type = #{payType} where id = #{id}")
    int updateCourse(Course course);
}
