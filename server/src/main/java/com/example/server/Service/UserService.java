package com.example.server.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.server.DTO.LoginDTO;
import com.example.server.DTO.SignUpDTO;
import com.example.server.Entity.UserEntity;
import com.example.server.Exception.SingleMessageException;
import com.example.server.Repository.UserRepository;
import com.example.server.Utils.JwtService;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // add new user
    public String addUser(SignUpDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new SingleMessageException("User already exists");
        }

        return "on going";
    }

    // login user
    public Map<String, Object> loginUser(LoginDTO request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getEmail());
        
    }
}
