package com.example.app.service.impl;

import com.example.app.dto.auth.AuthResponse;
import com.example.app.dto.auth.LoginRequest;
import com.example.app.entity.auth.AppUser;
import com.example.app.exception.AuthenticationFailedException;
import com.example.app.exception.LoginAttemptLimitExceededException;
import com.example.app.repository.auth.AppUserRepository;
import com.example.app.security.JwtTokenProvider;
import com.example.app.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * 認証Serviceのログイン処理を確認する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    /** テスト用のRepositoryです */
    @Mock
    private AppUserRepository appUserRepository;

    /** テスト用のパスワード照合部品です */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** テスト用のJWT発行部品です */
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    /** テスト用の試行回数管理部品です */
    @Mock
    private LoginAttemptService loginAttemptService;

    /** テスト対象の認証Serviceです */
    private AuthServiceImpl authService;

    /** 各テストの前に共通部品を準備します */
    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("dummy-hash");
        authService = new AuthServiceImpl(
                appUserRepository,
                passwordEncoder,
                jwtTokenProvider,
                loginAttemptService
        );
    }

    /** 正しい認証情報でJWTが返ることを確認します */
    @Test
    void loginReturnsTokenWhenCredentialsAreValid() {
        // ログイン入力を作成します
        LoginRequest request = createLoginRequest(" USER@EXAMPLE.COM ", "valid-password");
        AppUser appUser = new AppUser("user@example.com", "stored-hash", "利用者", "ROLE_USER");

        when(loginAttemptService.isAllowed("user@example.com")).thenReturn(true);
        when(appUserRepository.findByEmail("user@example.com")).thenReturn(Optional.of(appUser));
        when(passwordEncoder.matches("valid-password", "stored-hash")).thenReturn(true);
        when(jwtTokenProvider.createToken("user@example.com", "ROLE_USER")).thenReturn("issued-token");

        AuthResponse response = authService.login(request);

        assertEquals("issued-token", response.getToken());
        verify(loginAttemptService).clearFailures("user@example.com");
    }

    /** 未登録利用者でもダミーハッシュ照合後に同じ認証例外となることを確認します */
    @Test
    void loginUsesSafeFailureForUnknownUser() {
        LoginRequest request = createLoginRequest("unknown@example.com", "invalid-password");

        when(loginAttemptService.isAllowed("unknown@example.com")).thenReturn(true);
        when(appUserRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.matches("invalid-password", "dummy-hash")).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        verify(loginAttemptService).recordFailure("unknown@example.com");
    }

    /** 試行上限を超えた場合にRepositoryへアクセスしないことを確認します */
    @Test
    void loginRejectsAttemptWhenLimitIsExceeded() {
        LoginRequest request = createLoginRequest("user@example.com", "password");
        when(loginAttemptService.isAllowed("user@example.com")).thenReturn(false);

        assertThrows(LoginAttemptLimitExceededException.class, () -> authService.login(request));
        verifyNoInteractions(appUserRepository);
    }

    /** テスト用のログイン入力を作成します */
    private LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}
