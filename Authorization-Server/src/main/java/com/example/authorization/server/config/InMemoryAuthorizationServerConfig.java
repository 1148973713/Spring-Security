package com.example.authorization.server.config;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/*@Configuration
@EnableAuthorizationServer*/
public class InMemoryAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


/*    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }*/

    //1???????????????????????????????????????
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //clients.withClientDetails(clientDetailsService);            //??????in???memory??????
        clients.inMemory().withClient("c1")
                .secret(new BCryptPasswordEncoder().encode("123456"))//$2a$10$0uhIO.ADUFv7OQ/kuwsC1.o3JYvnevt5y3qX/ji0AUXs4KYGio3q6
                .resourceIds("r1")
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")//???client?????????????????????
                .scopes("all")			//????????????
                .autoApprove(false)
                .redirectUris("https://www.baidu.com");
    }

    //------------------------------------------------------------------------------------------------------------------
    //???????????????????????????
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    //???????????????????????????
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setClientDetailsService(clientDetailsService);
        // token ????????????????????????????????? 12 ??????
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 12);
        // refresh token ????????????????????????????????? 30 ???
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        return tokenServices;
    }

    //???????????????????????????
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
        //return new JdbcAuthorizationCodeServices(dataSource);
    }

    // ????????????????????????
    @Bean
    public ApprovalStore approvalStore() {
        return new InMemoryApprovalStore();
    }

    //2?????????????????????????????????
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenServices(tokenServices())
                //???????????????????????????????????????????????????????????? authorization_code ????????????????????????
                .authorizationCodeServices(authorizationCodeServices())
                //????????????-??????????????????????????????????????????
                .approvalStore(approvalStore())
                //???????????????????????????????????????
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    //------------------------------------------------------------------------------------------------------------------
    //3?????????????????????????????????
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }
}