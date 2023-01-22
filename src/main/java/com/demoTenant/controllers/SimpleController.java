package com.demoTenant.controllers;

import com.demoTenant.config.security.JwtTokenUtil;
import com.demoTenant.models.Student;
import com.demoTenant.services.SimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/main")
public class SimpleController {

    @Autowired
    SimpleService simpleService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "/getUserIdReference")
    public @ResponseBody
    String getUserIdReference(@RequestHeader HttpHeaders headers) {
        String bearerToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String token = bearerToken.split(" ")[1];
        final Long userID = jwtTokenUtil.getUserIdFromToken(token);
        return userID.toString();
    }

}
