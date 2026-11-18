package com.logic.springjwt.security.services;

import com.logic.springjwt.DTO.SecurityDTO.LoginRequest;
import com.logic.springjwt.DTO.SecurityDTO.SignupRequest;
import com.logic.springjwt.models.SecurityModel.RefreshToken;
import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.models.SecurityModel.UserRole;
import com.logic.springjwt.repository.Securityrepo.UserRepository;
import com.logic.springjwt.response.JwtResponse;
import com.logic.springjwt.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public String registerUser(SignupRequest signUpRequest) {
        if (userRepository.findUserByUsername(signUpRequest.getUsername()).isPresent()) {
            return "Error: Username is already taken!";
        }

        // Convert role string to UserRole enum
        String roleStr = signUpRequest.getRole() != null ? signUpRequest.getRole().toUpperCase() : "USER";
        if (!roleStr.startsWith("ROLE_")) {
            roleStr = "ROLE_" + roleStr;
        }
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            userRole = UserRole.ROLE_USER; // Default role if invalid
        }

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(userRole);
        user.setActive(signUpRequest.isActive());

        userRepository.save(user);

        return "User registered successfully!";
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getUserid());

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                refreshToken.getToken(),
                userPrincipal.getUserid(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles);
    }
}