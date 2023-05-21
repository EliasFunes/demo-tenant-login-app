package com.demoTenant.controllers;

import com.demoTenant.config.security.UserIdAuthenticationProvider;
import com.demoTenant.models.User;
import com.demoTenant.services.UserDetailsService;
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
import java.io.IOException;


@Controller
public class HomeController {

    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserIdAuthenticationProvider provider2;



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

    @GetMapping("/user/id")
    public @ResponseBody String getUserId(HttpServletRequest request) {
        logger.info(request.getSession().getId());
        Long userId = (Long) request.getSession().getAttribute("userId");
        return userId.toString();
    }

    @PostMapping("/login_by_userId")
    public void loginByUserId(@RequestParam("username") Long username, HttpServletResponse response) throws IOException {
        logger.info("loginByUserId: " + username.toString());

        User user = userDetailsService.loadByUserId(username);

        // Create an authentication token with the extra parameters
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        // Authenticate using the custom authentication provider
        Authentication result = provider2.authenticate(authentication);

        // Set authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(result);

        // Send success response or redirect to a secure endpoint
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Login successful");
    }

}
