package com.example.authorization.server.serivce;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.authorization.server.entity.UserEntity;
import com.example.authorization.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;
    //调用数据库查账户密码，用户名查询

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        UserEntity entity = userMapper.selectOne(queryWrapper);
        if (entity == null){
            throw new UsernameNotFoundException("用户名不存在");
        }

        String encode = new BCryptPasswordEncoder().encode(entity.getPassword());
        System.out.println(encode);

        //设置用户权限，role为权限，可自己设置，只要与SecurityTestConfig中.antMatchers("/test/index").hasAnyAuthority("admin")匹配就行
        //因为role≠admin，所以即使用户密码正确也进不去
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        return new User(entity.getUsername(),encode,auths);
    }
}
