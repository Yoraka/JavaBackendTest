package com.sdj.sdjtest.dto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    public UserDTO(String username, String password2) {
    }
    @NotNull(message = "手机号不能为空")
    @NotEmpty(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "密码不能为空")
    @NotEmpty(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotNull(message = "验证码不能为空")
    @NotEmpty(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    private String code;
}
