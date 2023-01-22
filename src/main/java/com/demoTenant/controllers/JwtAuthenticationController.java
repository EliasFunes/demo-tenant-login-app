package com.demoTenant.controllers;

import com.demoTenant.config.security.JwtTokenUtil;
import com.demoTenant.dto.CreateUserRequest;
import com.demoTenant.dto.JwtRequest;
import com.demoTenant.dto.JwtResponse;
import com.demoTenant.models.User;
import com.demoTenant.services.JwtUserDetailsService;
import com.demoTenant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping(value = "/jwt")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid JwtRequest authRequest) throws Exception {
        authenticate(authRequest.getUsername(), authRequest.getPassword());
        final User user = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/register")
    public Optional<User> userRegister(@RequestBody @Valid CreateUserRequest request) throws ValidationException {
        return userService.create(request);
    }
}
