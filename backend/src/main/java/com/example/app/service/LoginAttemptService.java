package com.example.app.service;

/**
 * ログイン試行回数を管理するインターフェースです。
 */
public interface LoginAttemptService {

    /** 指定したログイン識別子が試行可能か確認します */
    boolean isAllowed(String loginIdentifier);

    /** 認証失敗を記録します */
    void recordFailure(String loginIdentifier);

    /** 認証成功後に失敗記録を消去します */
    void clearFailures(String loginIdentifier);
}
