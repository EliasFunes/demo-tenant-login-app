package com.demoTenant.controllers;

import com.demoTenant.config.security.UserIdAuthenticationProvider;
import com.demoTenant.dto.CreateUserRequest;
import com.demoTenant.models.User;
import com.demoTenant.services.UserDetailsService;
import com.demoTenant.services.UserService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Optional;

import io.jsonwebtoken.Jwts;


@Controller
public class HomeController {

    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserIdAuthenticationProvider userIdAuthenticationProvider;



    @GetMapping("/")
    public String showMain(Model model, HttpSession session) {

        logger.info("showMain");

        logger.info(session.getAttribute("userId").toString());

        Long userId = (Long) session.getAttribute("userId");
        User user = userDetailsService.loadByUserId(userId);
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        // populate model with data if needed
        return "register";
    }

    @PostMapping("/register")
    public Optional<User> userRegister(@RequestBody @Valid CreateUserRequest request) throws ValidationException {
        return userService.create(request);
    }

    @GetMapping("/user/id")
    public @ResponseBody String getUserId(HttpServletRequest request) {
        logger.info(request.getSession().getId());
        Long userId = (Long) request.getSession().getAttribute("userId");
        return userId.toString();
    }



    //TODO: refactorizar esta funcion en un util con un try
    private Long getUserIdFromToken(String token) {
        String secretKey = "yourSecretKey";
//        String server2Audience = "server2";

        //TODO: el secret key deberia ser un env param.
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
//                .requireAudience(server2Audience)
                .parseClaimsJws(token)
                .getBody();

        Long id = Long.parseLong(claims.get("userReference").toString());
        return id;
    }


    @PostMapping("/login_by_userId")
    public void loginByUserId(@RequestParam("username") String usernameToken, HttpServletResponse response) throws IOException {

        Long username = getUserIdFromToken(usernameToken);

        logger.info("loginByUserId: " + username.toString());

        User user = userDetailsService.loadByUserId(username);

        // Create an authentication token with the extra parameters
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        // Authenticate using the custom authentication provider
        Authentication result = userIdAuthenticationProvider.authenticate(authentication);

        // Set authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(result);

        // Send success response or redirect to a secure endpoint
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Login successful");
    }

}
