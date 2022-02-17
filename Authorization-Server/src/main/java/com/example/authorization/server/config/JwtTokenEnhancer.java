package com.example.authorization.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;

//拓展jwt
public class JwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("enhance","enhance info");
        //DefaultOAuth2AccessToken.setAdditionalInformation:令牌授予者想要添加到令牌中的其他信息，例如支持新的令牌类型。
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(hashMap);
        return accessToken;
    }
}
