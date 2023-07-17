package com.sdj.sdjtest.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.ToString
public class SysUser{
    private Long userId;
    private String phone;
    private String password;
    private String salt;
    private String nickname;
    private Integer state;
    private Date createTime;
    private Date updateTime;
    private int sex;
    private String address;
    private Date birthday;
}