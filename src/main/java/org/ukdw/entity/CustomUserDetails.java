package org.ukdw.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {
    @Setter
    private UserAccountEntity userAccountEntity;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserAccountEntity userAccountEntity) {
        this.userAccountEntity = userAccountEntity;

        // Initialize authorities
        this.authorities = userAccountEntity.getGroups().stream()
                .map(group -> new SimpleGrantedAuthority(group.getGroupname().toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userAccountEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccountEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
