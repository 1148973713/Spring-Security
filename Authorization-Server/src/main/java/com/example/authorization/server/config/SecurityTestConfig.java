package com.example.authorization.server.config;

import com.example.authorization.server.serivce.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class SecurityTestConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private MyUserDetailService userDetailsService;

    @Resource
    private DataSource dataSource;

    //AuthenticationManager对象在OAuth2认证服务中要使用，提前放入IOC容器中
    //认证管理器
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //注入数据源配置对象
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

/*    @Bean
    public PasswordEncoder password() {
        return new BCryptPasswordEncoder();
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                //.loginPage("/login.html")//登录页面设置
                //.loginProcessingUrl("/user/login")//登录访问路径
                .defaultSuccessUrl("/test/hello").permitAll()//登录成功后去哪
                .and().authorizeRequests().antMatchers( "/user/login").permitAll()//定义哪些方法需要被认证哪些不需要认证,此处表示/test/hello与/user/login不需要认证;
                .antMatchers("/test/hello").hasAuthority("admin")//当前登录的用户只有具有admin权限时才能真正进入
                //.antMatchers().hasAnyAuthority("admin","manager")//表示只要登录用户有其中一个权限就可以登录
                //.anyRequest().authenticated()//表示所有请求都可以直接访问
                .anyRequest().authenticated().and().rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60)//设置token时间
                .userDetailsService(userDetailsService)
                .and().csrf().disable();//关闭csr的防护
    }
/*    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable().authorizeRequests().antMatchers("/test/hello").hasAuthority("admin").anyRequest().permitAll();
    }*/
}
