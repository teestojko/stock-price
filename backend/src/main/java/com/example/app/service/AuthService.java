package com.example.app.service;

import com.example.app.dto.auth.AuthResponse;
import com.example.app.dto.auth.LoginRequest;
import com.example.app.dto.auth.RegisterRequest;

/**
 * 認証処理のビジネスロジックを定義するインターフェースです。
 */
public interface AuthService {

    /**
     * ユーザー登録を行います。
     *
     * @param request 登録リクエスト
     * @return 認証トークンを含むレスポンス
     */
    AuthResponse register(RegisterRequest request);

    /**
     * ログイン認証を行います。
     *
     * @param request ログインリクエスト
     * @return 認証トークンを含むレスポンス
     */
    AuthResponse login(LoginRequest request);
}