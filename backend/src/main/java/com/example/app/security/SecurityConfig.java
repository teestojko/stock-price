package com.example.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security全体の設定クラスです。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** JWT認証フィルタです */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * セキュリティ設定を生成します。
     *
     * @param jwtAuthenticationFilter JWT認証フィルタ
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * パスワードをBCryptでハッシュ化する部品をBean登録します。
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * APIの認証・認可ルールを定義します。
     *
     * @param http Spring Security設定
     * @return SecurityFilterChain
     * @throws Exception セキュリティ設定エラー
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                // JWT認証ではサーバー側セッションを使わないためCSRFを無効化します
                .csrf(csrf -> csrf.disable())

                // JWT認証ではセッションを作成しません
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // エンドポイントごとの認可ルールです
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )

                // フォームログインはAPIでは使わないため無効化します
                .formLogin(form -> form.disable())

                // Basic認証も使わないため無効化します
                .httpBasic(basic -> basic.disable())

                // JWT認証フィルタをSpring Securityの認証処理前に差し込みます
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}