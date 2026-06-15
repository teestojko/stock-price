package com.example.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWTの作成・検証を担当するクラスです。
 */
@Component
public class JwtTokenProvider {

    /** JWT署名に使用する秘密鍵です */
    private final SecretKey secretKey;

    /** JWTの有効期限です */
    private final long expirationMs;

    /**
     * JWT設定を環境変数から受け取ります。
     *
     * @param jwtSecret JWT署名用の秘密文字列
     * @param expirationMs JWT有効期限
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        // 署名鍵として利用するため、文字列からSecretKeyを作成します
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // トークンの有効期限を保持します
        this.expirationMs = expirationMs;
    }

    /**
     * JWTを作成します。
     *
     * @param email 認証済みユーザーのメールアドレス
     * @param role ユーザー権限
     * @return JWT文字列
     */
    public String createToken(String email, String role) {
        // 現在日時を取得します
        Date now = new Date();

        // 有効期限日時を計算します
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWTからメールアドレスを取得します。
     *
     * @param token JWT文字列
     * @return メールアドレス
     */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * JWTから権限を取得します。
     *
     * @param token JWT文字列
     * @return 権限
     */
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * JWTが有効か確認します。
     *
     * @param token JWT文字列
     * @return 有効ならtrue
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * JWTの中身を検証して取り出します。
     *
     * @param token JWT文字列
     * @return JWTのClaims
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}