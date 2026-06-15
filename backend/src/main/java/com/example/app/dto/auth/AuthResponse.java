package com.example.app.dto.auth;

/**
 * 認証成功時にクライアントへ返すレスポンスDTOです。
 * JWTなどの認証情報を安全に受け渡すために使用します。
 */
public class AuthResponse {

    /** 認証後にクライアントへ返すトークンです */
    private String token;

    /**
     * AuthResponseを生成します。
     *
     * @param token 認証後に発行されたトークン
     */
    public AuthResponse(String token) {
        this.token = token;
    }

    /**
     * トークンを取得します。
     *
     * @return 認証トークン
     */
    public String getToken() {
        return token;
    }

    /**
     * トークンを設定します。
     *
     * @param token 認証トークン
     */
    public void setToken(String token) {
        this.token = token;
    }
}