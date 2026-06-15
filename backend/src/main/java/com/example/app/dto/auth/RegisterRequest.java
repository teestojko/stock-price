package com.example.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録時の入力値を受け取るリクエストDTOです。
 * Controllerで入力値検証を行うために使用します。
 */
public class RegisterRequest {

    /** ログインIDとして使用するメールアドレスです */
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    /** ログイン時に使用するパスワードです */
    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以内で入力してください")
    private String password;

    /** 画面表示用のユーザー名です */
    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = 100, message = "ユーザー名は100文字以内で入力してください")
    private String name;

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

    /**
     * ユーザー名を取得します。
     *
     * @return ユーザー名
     */
    public String getName() {
        return name;
    }

    /**
     * ユーザー名を設定します。
     *
     * @param name ユーザー名
     */
    public void setName(String name) {
        this.name = name;
    }
}