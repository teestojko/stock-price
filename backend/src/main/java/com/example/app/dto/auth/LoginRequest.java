package com.example.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ログイン時の入力値を受け取るリクエストDTOです。
 * Controllerで入力値検証を行うために使用します。
 */
public class LoginRequest {

    /** ログインに使用するメールアドレスです */
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    /** ログインに使用するパスワードです */
    @NotBlank(message = "パスワードは必須です")
    private String password;

    /**
     * メールアドレスを取得します。
     *
     * @return メールアドレス
     */
    public String getEmail() {
        return email;
    }

    /**
     * メールアドレスを設定します。
     *
     * @param email メールアドレス
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * パスワードを取得します。
     *
     * @return パスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定します。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }
}