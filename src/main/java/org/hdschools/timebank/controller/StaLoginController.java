package org.hdschools.timebank.controller;

import org.hdschools.timebank.model.ApiResponse;
import org.hdschools.timebank.model.LoginRequest;
import org.hdschools.timebank.model.LoginResponse;
import org.hdschools.timebank.model.StaUser;
import org.hdschools.timebank.repository.StaUserRepository;
import org.hdschools.timebank.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles login operations for staff users.
 */
@RestController
@RequestMapping("/sta")
public class StaLoginController {

    private final StaUserRepository staUserRepository;
    private final TokenService tokenService;

    public StaLoginController(StaUserRepository staUserRepository, TokenService tokenService) {
        this.staUserRepository = staUserRepository;
        this.tokenService = tokenService;
    }

    /**
     * Authenticates a staff user by matching hashed password.
     *
     * @param request login credentials (userId and hashed password)
     * @return {@link ApiResponse} containing login details on success
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return staUserRepository.findByUserId(request.getUserId())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .map(user -> {
                    String token = tokenService.generateToken(user.getId(), "staff");
                    return ApiResponse.success(
                            "Login successful",
                            LoginResponse.builder()
                                    .id(user.getId())
                                    .userId(user.getUserId())
                                    .userType("staff")
                                    .token(token)
                                    .build()
                    );
                })
                .orElse(ApiResponse.error("Invalid credentials", null));
    }
}
