package com.example.app.exception;

/**
 * ログイン試行回数が安全上の上限を超えたことを表す例外です。
 */
public class LoginAttemptLimitExceededException extends RuntimeException {

    /**
     * 再試行を控えるための安全なメッセージで例外を生成します。
     */
    public LoginAttemptLimitExceededException() {
        super("ログイン試行回数が上限を超えました。時間をおいて再度お試しください");
    }
}
