package com.magicliang.experiments.service;

import com.magicliang.experiments.entity.User;
import com.magicliang.experiments.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by magicliang on 2016/6/11.
 */
//Can also refer to this:
//http://javapointers.com/tutorial/spring-custom-userdetailsservice-example/
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository uerRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //This will break the database looking
//        Authentication authentication = SecurityContextHolder.getContext()
//                .getAuthentication();
//
//        log.info("The request principal is: " +  authentication.getPrincipal());
        List<User> users = uerRepository.findByName(username);
        //TODO: use optional to refactor here.
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("User details not found with this username: " + username);
        }
        User user = users.get(0);

        List<SimpleGrantedAuthority> authList = getAuthorities(user.getRole());
        return new CustomUserDetails(user.getName(), user.getPassword(), authList);
    }

    private List<SimpleGrantedAuthority> getAuthorities(String role) {
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));

        //you can also add different roles here
        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
        //so that he can view pages that are ROLE_ADMIN specific
        if (role != null && role.trim().length() > 0) {
            if (role.equals("ADMIN")) {
                authList.add(new SimpleGrantedAuthority("Authority_ADMIN"));
            } else {
                authList.add(new SimpleGrantedAuthority("Authority_USER"));
            }
        }

        return authList;
    }

    private static class CustomUserDetails implements UserDetails {
        private Collection<? extends GrantedAuthority> GrantedAuthorities;
        private String password;
        private String name;
        private Boolean isAccountNonExpired = true;
        private Boolean isAccountNonLocked = true;
        private Boolean isCredentialsNonExpired = true;
        private Boolean isEnabled = true;

        public CustomUserDetails() {
            super();
        }

        public CustomUserDetails(String name, String password, Collection<? extends GrantedAuthority> grantedAuthorities) {
            this(name, password, grantedAuthorities, null, null, null, null);
        }

        public CustomUserDetails(String name, String password, Collection<? extends GrantedAuthority> grantedAuthorities, Boolean isEnabled, Boolean isCredentialsNonExpired, Boolean isAccountNonLocked, Boolean isAccountNonExpired) {
            this();
            this.name = name;
            this.password = password;
            this.GrantedAuthorities = grantedAuthorities;
            if (isEnabled != null) {
                this.isEnabled = isEnabled;
            }
            if (isCredentialsNonExpired != null) {
                this.isCredentialsNonExpired = isCredentialsNonExpired;
            }
            if (isAccountNonLocked != null) {
                this.isAccountNonLocked = isAccountNonLocked;
            }
            if (isAccountNonExpired != null) {
                this.isAccountNonExpired = isAccountNonExpired;
            }
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return GrantedAuthorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return name;
        }

        @Override
        public boolean isAccountNonExpired() {
            return isAccountNonExpired;
        }

        @Override
        public boolean isAccountNonLocked() {
            return isAccountNonLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return isCredentialsNonExpired;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }
    }
}
