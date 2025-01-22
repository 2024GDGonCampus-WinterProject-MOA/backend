package org.example.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GitHubOAuthUserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest:"+userRequest);
        System.out.println("getClientRegistraion:"+userRequest.getClientRegistration());  //client에 대한 정보들이 받아짐
        System.out.println("getAccessToken:"+userRequest.getAccessToken());
        System.out.println("getAttributes:"+super.loadUser(userRequest).getAttributes()); //유저 정보를 받아옴
        return super.loadUser(userRequest);
    }

}

