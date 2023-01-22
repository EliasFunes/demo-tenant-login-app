package com.demoTenant.services;

import com.demoTenant.dto.CreateUserRequest;
import com.demoTenant.models.User;
import com.demoTenant.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Optional<User> create(CreateUserRequest request) throws ValidationException {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists!");
        }

        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Long id = userRepository.save(user).getId();
        return userRepository.findById(id);
    }

}
