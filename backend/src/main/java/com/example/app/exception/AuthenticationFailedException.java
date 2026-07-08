package com.example.app.exception;

/**
 * ログイン認証に失敗したことを表す例外です。
 */
public class AuthenticationFailedException extends RuntimeException {

    /** クライアントへ返す共通メッセージです */
    private static final String SAFE_MESSAGE = "メールアドレスまたはパスワードが正しくありません";

    /**
     * 利用者の存在を推測されない共通メッセージで例外を生成します。
     */
    public AuthenticationFailedException() {
        super(SAFE_MESSAGE);
    }
}
