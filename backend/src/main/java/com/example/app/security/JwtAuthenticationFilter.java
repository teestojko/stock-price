package com.example.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * リクエストごとにJWTを確認する認証フィルタです。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWTの作成・検証を行う部品です */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT認証フィルタを生成します。
     *
     * @param jwtTokenProvider JWT処理部品
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * リクエストのAuthorizationヘッダーからJWTを検証します。
     *
     * @param request リクエスト
     * @param response レスポンス
     * @param filterChain 次のフィルタ
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorizationヘッダーを取得します
        String authorizationHeader = request.getHeader("Authorization");

        // Bearer形式でなければ、認証処理をせず次へ進みます
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " の後ろにあるJWT本体を取り出します
        String token = authorizationHeader.substring(7);

        // JWTが有効な場合のみ認証情報を作成します
        if (jwtTokenProvider.validateToken(token)) {
            // JWTからメールアドレスを取得します
            String email = jwtTokenProvider.getEmailFromToken(token);

            // JWTから権限を取得します
            String role = jwtTokenProvider.getRoleFromToken(token);

            // Spring Securityで扱う権限リストを作成します
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            // 認証済みユーザーとして登録するための認証情報を作成します
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            // 現在のリクエストを認証済みにします
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 次のフィルタへ処理を渡します
        filterChain.doFilter(request, response);
    }
}