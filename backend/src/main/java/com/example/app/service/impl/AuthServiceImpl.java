package com.example.app.service.impl;

import com.example.app.dto.auth.AuthResponse;
import com.example.app.dto.auth.LoginRequest;
import com.example.app.dto.auth.RegisterRequest;
import com.example.app.entity.auth.AppUser;
import com.example.app.repository.auth.AppUserRepository;
import com.example.app.security.JwtTokenProvider;
import com.example.app.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証処理のビジネスロジックを実装するServiceです。
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** ユーザー情報をDB操作するRepositoryです */
    private final AppUserRepository appUserRepository;

    /** パスワードを安全にハッシュ化・照合する部品です */
    private final PasswordEncoder passwordEncoder;

    /** JWTを作成・検証する部品です */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 認証Serviceを生成します。
     *
     * @param appUserRepository ユーザーRepository
     * @param passwordEncoder パスワードハッシュ化部品
     * @param jwtTokenProvider JWT作成部品
     */
    public AuthServiceImpl(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * ユーザー登録を行います。
     *
     * @param request 登録リクエスト
     * @return JWTを含むレスポンス
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // メールアドレスの前後空白を除去して、入力ゆれを防ぎます
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 同じメールアドレスが登録済みか確認します
        if (appUserRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("このメールアドレスは使用できません");
        }

        // 平文パスワードをBCryptでハッシュ化します
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 新規ユーザーEntityを作成します
        AppUser appUser = new AppUser(
                normalizedEmail,
                hashedPassword,
                request.getName().trim(),
                "ROLE_USER"
        );

        // ユーザー情報をDBに保存します
        AppUser savedUser = appUserRepository.save(appUser);

        // 保存したユーザー情報からJWTを発行します
        String token = jwtTokenProvider.createToken(savedUser.getEmail(), savedUser.getRole());

        return new AuthResponse(token);
    }

    /**
     * ログイン認証を行います。
     *
     * @param request ログインリクエスト
     * @return JWTを含むレスポンス
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // メールアドレスの前後空白を除去して、入力ゆれを防ぎます
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // メールアドレスに紐づくユーザーを取得します
        AppUser appUser = appUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("メールアドレスまたはパスワードが正しくありません"));

        // 入力されたパスワードとハッシュ化済みパスワードを照合します
        boolean passwordMatched = passwordEncoder.matches(request.getPassword(), appUser.getPassword());

        if (!passwordMatched) {
            throw new IllegalArgumentException("メールアドレスまたはパスワードが正しくありません");
        }

        // 認証成功後にJWTを発行します
        String token = jwtTokenProvider.createToken(appUser.getEmail(), appUser.getRole());

        return new AuthResponse(token);
    }
}