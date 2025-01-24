package org.example.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GitHubUserDetails implements OAuth2User {
    private GitHubUser gitHubUser; // 데이터베이스에 저장된 사용자 정보
    private Map<String, Object> attributes; // GitHub API로부터 가져온 추가 정보

    public GitHubUserDetails(GitHubUser gitHubUser, Map<String, Object> attributes) {
        this.gitHubUser = gitHubUser;
        this.attributes = attributes;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<GrantedAuthority>();
        collect.add(new SimpleGrantedAuthority(gitHubUser.getRole().getAuthority()));
        return collect;
    }

    @Override
    public String getName(){
        return gitHubUser.getId()+"";
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getAvatarUrl() {
        return (String) attributes.get("avatar_url");
    }

}
