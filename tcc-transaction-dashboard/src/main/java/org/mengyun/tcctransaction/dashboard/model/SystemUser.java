package org.mengyun.tcctransaction.dashboard.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

/**
 * @Author huabao.fang
 * @Date 2022/6/6 14:52
 */
public class SystemUser implements UserDetails {

    private String username;

    private String password;

    public SystemUser() {
    }

    public SystemUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isTrue();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isTrue();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isTrue();
    }

    @Override
    public boolean isEnabled() {
        return isTrue();
    }

    private boolean isTrue() {
        return true;
    }
}
