package org.example.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GitHubUserDetails implements OAuth2User {
    private GitHubUser gitHubUser;
    private Map<String, Object> attributes;

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
        collect.add(() -> gitHubUser.getRole());
        return collect;
    }

    @Override
    public String getName(){
        return gitHubUser.getId()+"";
    }
}
