package com.demoTenant.config.security;

import com.demoTenant.models.User;
import com.demoTenant.services.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class UserIdAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(UserIdAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("entra en la utenticacion");
        String userName = authentication.getName();
        User user = userDetailsService.loadUserByUsername(userName);


        if (user != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(userName, null, Collections.emptyList());
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            return auth;
        } else {
            throw new BadCredentialsException("Invalid user id");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

