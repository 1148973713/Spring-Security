package com.example.authorization.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("users")
public class UserEntity implements Serializable {
    private Integer id;
    private String username;
    private String password;
}
