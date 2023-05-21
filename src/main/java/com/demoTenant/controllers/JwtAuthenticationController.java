package com.demoTenant.controllers;

import com.demoTenant.dto.CreateUserRequest;
import com.demoTenant.models.User;
import com.demoTenant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping(value = "/jwt")
public class JwtAuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Optional<User> userRegister(@RequestBody @Valid CreateUserRequest request) throws ValidationException {
        return userService.create(request);
    }
}
