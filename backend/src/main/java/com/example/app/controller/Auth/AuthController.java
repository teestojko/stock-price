package com.example.app.controller.auth;

import com.example.app.dto.auth.AuthResponse;
import com.example.app.dto.auth.LoginRequest;
import com.example.app.dto.auth.RegisterRequest;
import com.example.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 認証関連APIを提供するControllerです。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** 認証処理を担当するServiceです */
    private final AuthService authService;

    /**
     * 認証Controllerを生成します。
     *
     * @param authService 認証Service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * ユーザー登録APIです。
     *
     * @param request 登録リクエスト
     * @return JWTを含むレスポンス
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * ログインAPIです。
     *
     * @param request ログインリクエスト
     * @return JWTを含むレスポンス
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}