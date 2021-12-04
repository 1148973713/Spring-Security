package com.example.authorization.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.authorization.server.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<UserEntity> {
}
