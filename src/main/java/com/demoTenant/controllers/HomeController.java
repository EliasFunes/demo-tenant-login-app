package com.demoTenant.controllers;

import com.demoTenant.config.security.UserIdAuthenticationProvider;
import com.demoTenant.dto.CreateUserRequest;
import com.demoTenant.models.User;
import com.demoTenant.services.UserDetailsService;
import com.demoTenant.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.web.client.RestTemplate;


@Controller
public class HomeController {

    @Value("${qr.server.username}")
    private String qrServerUserName;

    @Value("${qr.server.password}")
    private String qrServerPassword;

    @Value("${qr.server.host}")
    private String qrServerHost;

    //TODO: esto no deberia ser necesario para el cliente?
    @Autowired
    private RestTemplate restTemplate;

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

    @GetMapping("/vincular_usuario")
    public String showVincularUsuario(Model model, HttpSession session) {

        logger.info("showVincularUsuario");

        logger.info(session.getAttribute("userId").toString());

        Long userId = (Long) session.getAttribute("userId");
        User user = userDetailsService.loadByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("qr_server_host", qrServerHost);
        return "vincularUsuario";
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) throws URISyntaxException, JsonProcessingException {

        //TODO: esto no deberia ser necesario para el cliente?
        String url = qrServerHost+"/jwt/user_tenant/authenticate"; // Replace with the actual URL of the server's endpoint

        //TODO: Desde aqui - debe ser un codigo injectado con parametros e instalable desde maven.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonPayload = "{\"username\": \""+qrServerUserName+"\", \"password\": \""+qrServerPassword+"\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(new URI(url), requestEntity, String.class);
        String data = responseEntity.getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(data);
        String token = jsonNode.get("token").asText();

        model.addAttribute("token", token);
        model.addAttribute("qr_server_host", qrServerHost);
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

    @PostMapping("/login_by_userId")
    public void loginByUserId(@RequestParam("username") String usernameToken, HttpServletResponse response)
            throws IOException, URISyntaxException, NumberFormatException {

        //TODO: esto no deberia ser necesario para el cliente?
        String url = qrServerHost+"/jwt/getReference";

        //TODO: Desde aqui - debe ser un codigo inyectado con parametros
        // e instalable desde maven repository, o simil.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonPayload = "{\"tokenReference\": \""+usernameToken+"\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(new URI(url), requestEntity, String.class);
        String data = responseEntity.getBody();
        //TODO: hasta aqui

        logger.info("loginByUserId: " + data);

        User user = null;
        if (data != null) {
            Long userId = Long.parseLong(data);
            user = userDetailsService.loadByUserId(userId);
        }

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
