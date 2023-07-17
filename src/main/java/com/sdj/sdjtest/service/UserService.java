package com.sdj.sdjtest.service;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.tomcat.jni.User;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.sdj.sdjtest.dto.UserDTO;
import com.sdj.sdjtest.dto.UserInfoDTO;
import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.token.JwtToken;

@Service
public interface UserService {

    boolean reg(UserDTO userDTO);

    JwtToken login(UserDTO userDTO);

    boolean updatePassword(String phone, String newPwd);

    List<Map<String, Object>> getUserPower(int userId);

    boolean signToCourse(Long courseId);

    boolean signOutCourse(Long courseId);

    UserDTO getUserInfo(int userId);

    PageInfo<Course> getUserCourse(Integer page, Integer limit, int userId);

    boolean updateUserInfo(UserInfoDTO userInfoDTO);
}
