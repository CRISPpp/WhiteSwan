package com.crisp.saleproject.dto;

import com.crisp.saleproject.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;//传过来的验证码
}
