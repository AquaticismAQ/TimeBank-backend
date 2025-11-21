package org.hdschools.timebank.controller;

import org.hdschools.timebank.model.ApiResponse;
import org.hdschools.timebank.model.LoginRequest;
import org.hdschools.timebank.model.LoginResponse;
import org.hdschools.timebank.model.StuUser;
import org.hdschools.timebank.repository.StuUserRepository;
import org.hdschools.timebank.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles login operations for student users.
 */
@RestController
@RequestMapping("/stu")
public class StuLoginController {

    private final StuUserRepository stuUserRepository;
    private final TokenService tokenService;

    public StuLoginController(StuUserRepository stuUserRepository, TokenService tokenService) {
        this.stuUserRepository = stuUserRepository;
        this.tokenService = tokenService;
    }

    /**
     * Authenticates a student user by matching hashed password.
     *
     * @param request login credentials (userId and hashed password)
     * @return {@link ApiResponse} containing login details on success
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return stuUserRepository.findByUserId(request.getUserId())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .map(user -> {
                    String token = tokenService.generateToken(user.getId(), "student");
                    return ApiResponse.success(
                            "Login successful",
                            LoginResponse.builder()
                                    .id(user.getId())
                                    .userId(user.getUserId())
                                    .userType("student")
                                    .token(token)
                                    .build()
                    );
                })
                .orElse(ApiResponse.error("Invalid credentials", null));
    }
}
