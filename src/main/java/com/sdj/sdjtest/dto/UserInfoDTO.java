package com.sdj.sdjtest.dto;

import java.util.Date;
@lombok.Data
@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String nickName;
    private int sex;
    private String address;
    private Date birthday;
}
