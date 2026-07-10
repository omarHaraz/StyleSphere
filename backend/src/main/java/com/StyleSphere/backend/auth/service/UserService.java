package com.StyleSphere.backend.auth.service;


import com.StyleSphere.backend.auth.dto.SignupRequest;
import com.StyleSphere.backend.auth.model.User;
import com.StyleSphere.backend.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public User createUser(SignupRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.getRoles().add("ROLE_CUSTOMER");

        return userRepository.save(user);
    }
}
