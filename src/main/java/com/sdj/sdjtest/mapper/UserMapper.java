package com.sdj.sdjtest.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdj.sdjtest.dto.UserDTO;
import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.entity.SysUser;
@Repository
public interface UserMapper {
    @Insert("<script>"
    + "insert into sys_user(phone,password,salt,nickname,state,create_time,update_time) values(#{phone},#{password},#{salt},#{nickname},#{state},#{createTime},#{updateTime})"
    + "INSERT INTO user_role (user_id, roleId) VALUES (#{userId}, 200);"
    + "</script>")
    public int insertUser(SysUser user);
    @Select("select * from sys_user where phone = #{phone}")
    public SysUser selectUserByPhone(String phone);
    @Update("update sys_user set password = #{newPwd} where phone = #{phone}")
    public int updatePassword(String phone, String newPwd);
    @Select("<script>"
    + "SELECT user.user_id ,user.phone,role.roleName,role.roleId,per.permissionsName ,per.perId,per.perRemarks FROM sys_user AS user,"
    + "sys_role AS role,"
    + "sys_permissions AS per,"
    + "role_per,"
    + "user_role"
    + " WHERE user.user_id=#{userId}"
    + " AND user.user_id=user_role.user_id"
    + " AND user_role.roleId=role.roleId"
    + " AND role_per.roleId=role.roleId"
    + " AND role_per.perId=per.perId"
    + "</script>")
    public List<Map<String, Object>> getUserPower(int userId);
    @Insert("insert into course_user(course_id,user_id) values(#{courseId},#{userId})")
    public int signToCourse(int userId, Long courseId);
    @Update("update course_user set state = 1 where course_id = #{courseId} and user_id = #{userId}")
    public int signOutCourse(int userId, Long courseId);
    @Select("select * from sys_user where user_id = #{userId}")
    public UserDTO getUserInfo(int userId);
    @Select("select * from course_user where user_id = #{userId} and state = 0")
    public List<Course> getUserCourse(int userId);
    @Update("<script>"
    + "update sys_user"
    + "<set>"
    + "nickname = #{nickname},"
    + "sex = #{sex}"
    + "birthday = #{birthday}"
    + "address = #{address}"
    + "</set>"
    + "where user_id = #{userId}"
    + "</script>")
    public int updateUserInfo(SysUser user);
}
