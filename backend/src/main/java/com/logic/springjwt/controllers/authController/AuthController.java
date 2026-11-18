package com.logic.springjwt.controllers.authController;

import com.logic.springjwt.DTO.SecurityDTO.LoginRequest;
import com.logic.springjwt.DTO.SecurityDTO.SignupRequest;
import com.logic.springjwt.DTO.SecurityDTO.TokenRefreshRequest;
import com.logic.springjwt.DTO.SecurityDTO.TokenRefreshResponse;
import com.logic.springjwt.ExceptionHandler.TokenRefreshException;
import com.logic.springjwt.models.SecurityModel.RefreshToken;
import com.logic.springjwt.response.JwtResponse;
import com.logic.springjwt.response.MessageResponse;
import com.logic.springjwt.security.jwt.JwtUtils;
import com.logic.springjwt.security.services.AuthService;
import com.logic.springjwt.security.services.RefreshTokenService;
import com.logic.springjwt.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);

            ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwtResponse.getAccessToken())
                    .path("/")
                    .maxAge(86400)
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid username or password: " + e.getMessage()));
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    ResponseCookie jwtCookie = ResponseCookie.from("accessToken", token)
                            .path("/")
                            .maxAge(86400)
                            .httpOnly(false)
                            .secure(false)
                            .sameSite("Lax")
                            .build();

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                            .body(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        String result = authService.registerUser(signUpRequest);

        if (result.startsWith("Error")) {
            return ResponseEntity.badRequest().body(new MessageResponse(result));
        }

        return ResponseEntity.ok(new MessageResponse(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cleanCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
                .build();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            refreshTokenService.deleteByUserId(userDetails.getUserid());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                    .body(new MessageResponse("Log out successful!"));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(new MessageResponse("Log out completed."));
    }
}
