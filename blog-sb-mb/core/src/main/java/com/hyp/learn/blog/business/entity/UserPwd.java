package com.hyp.learn.blog.business.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = false)
public class UserPwd {
    @NotNull(message = "用户ID不可为空")
    private Long id;
    @NotNull(message = "原密码不可为空")
    private String password;
    @NotNull(message = "新密码不可为空")
    @Length(max = 20, min = 6, message = "新密码长度建议保持在6~20个字符以内")
    private String newPassword;
    @NotNull(message = "新密码不可为空")
    @Length(max = 20, min = 6, message = "新密码长度建议保持在6~20个字符以内")
    private String newPasswordRepeat;
}
