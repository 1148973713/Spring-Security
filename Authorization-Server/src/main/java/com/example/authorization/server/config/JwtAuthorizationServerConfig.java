package com.example.authorization.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class JwtAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("myJwtTokenStore")
    @Autowired
    private TokenStore tokenStore;

    //在JWT编码的令牌值和OAuth认证信息之间进行转换的助手(在两个方向上)。当授予令牌时，还充当令牌增强器。
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    @Autowired
    private ClientDetailsService clientDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   //提供关于OAuth2客户端的详细信息的服务,这里意识是采用数据库存储客户端信息
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }


    //客户端详细信息服务配置器
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    //------------------------------------------------------------------------------------------------------------------

/*    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }*/

    //授权服务器令牌服务
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setClientDetailsService(clientDetailsService);
        // token 有效期自定义设置，默认 12 小时
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 12);
        // refresh token 有效期自定义设置，默认 30 天
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        return tokenServices;
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource){
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    //2、授权服务器端点配置器
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //设置jwt增强内容
        TokenEnhancerChain chain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        //JWT自定义声明，默认是Outh2生成的，所以要再加入jwtAccessTokenConverter
        tokenEnhancers.add(jwtTokenEnhancer);
        tokenEnhancers.add(jwtAccessTokenConverter);
        chain.setTokenEnhancers(tokenEnhancers);

        endpoints.authenticationManager(authenticationManager)
                //使用JWT方式生成token
                .tokenStore(tokenStore)
                //访问令牌转换器
                .accessTokenConverter(jwtAccessTokenConverter)
                //设置jwt拓展信息
                .tokenEnhancer(chain)
               // .tokenServices(tokenServices())
                //这个属性是用来设置授权码服务的，主要用于 authorization_code 授权码类型模式。
                //.authorizationCodeServices(authorizationCodeServices)

                //获取允许的令牌端点请求方法
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    //------------------------------------------------------------------------------------------------------------------
    //3、授权服务器安全配置器
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //security.tokenKeyAccess("permitAll()")
        security.tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }
}