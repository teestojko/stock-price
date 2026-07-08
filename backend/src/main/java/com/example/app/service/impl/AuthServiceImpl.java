package com.example.app.service.impl;

import com.example.app.dto.auth.AuthResponse;
import com.example.app.dto.auth.LoginRequest;
import com.example.app.dto.auth.RegisterRequest;
import com.example.app.entity.auth.AppUser;
import com.example.app.exception.AuthenticationFailedException;
import com.example.app.exception.LoginAttemptLimitExceededException;
import com.example.app.repository.auth.AppUserRepository;
import com.example.app.security.JwtTokenProvider;
import com.example.app.service.AuthService;
import com.example.app.service.LoginAttemptService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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

    /** ログイン失敗回数を管理する部品です */
    private final LoginAttemptService loginAttemptService;

    /** 未登録利用者でも同じパスワード照合を行うためのダミーハッシュです */
    private final String dummyPasswordHash;

    /**
     * 認証Serviceを生成します。
     *
     * @param appUserRepository ユーザーRepository
     * @param passwordEncoder パスワードハッシュ化部品
     * @param jwtTokenProvider JWT作成部品
     * @param loginAttemptService ログイン失敗回数管理部品
     */
    public AuthServiceImpl(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            LoginAttemptService loginAttemptService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginAttemptService = loginAttemptService;
        this.dummyPasswordHash = passwordEncoder.encode("authentication-timing-protection");
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
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

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
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // 短時間に繰り返されるログイン試行を拒否します
        if (!loginAttemptService.isAllowed(normalizedEmail)) {
            throw new LoginAttemptLimitExceededException();
        }

        // メールアドレスに紐づくユーザーを取得します
        AppUser appUser = appUserRepository.findByEmail(normalizedEmail).orElse(null);

        // 未登録の場合もダミーハッシュと照合し、処理時間による登録状況の推測を防ぎます
        String passwordHash = appUser == null ? dummyPasswordHash : appUser.getPassword();

        // 入力されたパスワードとハッシュ化済みパスワードを照合します
        boolean passwordMatched = passwordEncoder.matches(request.getPassword(), passwordHash);

        if (appUser == null || !passwordMatched) {
            loginAttemptService.recordFailure(normalizedEmail);
            throw new AuthenticationFailedException();
        }

        // 認証成功後は過去の失敗回数を消去します
        loginAttemptService.clearFailures(normalizedEmail);

        // 認証成功後にJWTを発行します
        String token = jwtTokenProvider.createToken(appUser.getEmail(), appUser.getRole());

        return new AuthResponse(token);
    }
}
