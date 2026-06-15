package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

/**
 * フロントエンドからAPIへアクセスするためのCORS設定です。
 */
@Configuration
public class CorsConfig {

    /**
     * CORSルールを定義します。
     *
     * @return CORS設定
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS設定オブジェクトを作成します
        CorsConfiguration configuration = new CorsConfiguration();

        // 許可するフロントエンドのURLです
        configuration.addAllowedOrigin("http://localhost:5173");

        // 許可するHTTPメソッドです
        configuration.addAllowedMethod("*");

        // 許可するHTTPヘッダーです
        configuration.addAllowedHeader("*");

        // Authorizationヘッダーを扱うために必要です
        configuration.setAllowCredentials(true);

        // すべてのAPIパスにCORS設定を適用します
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // API全体にCORS設定を登録します
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}