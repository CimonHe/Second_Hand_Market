package com.cimonhe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        if (userService.queryUserByName(s).isAdmin())
            return new User(userService.queryUserByName(s).getUsername(), userService.queryUserByName(s).getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        else
            return new User(userService.queryUserByName(s).getUsername(), userService.queryUserByName(s).getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("user"));
    }
}