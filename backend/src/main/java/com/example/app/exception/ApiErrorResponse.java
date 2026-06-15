package com.example.app.exception;

import java.time.LocalDateTime;

/**
 * APIエラー時にクライアントへ返す共通レスポンスです。
 */
public class ApiErrorResponse {

    /** エラー発生日時です */
    private final LocalDateTime timestamp;

    /** HTTPステータスコードです */
    private final int status;

    /** クライアント表示用のエラーメッセージです */
    private final String message;

    /**
     * エラーレスポンスを生成します。
     *
     * @param status HTTPステータスコード
     * @param message エラーメッセージ
     */
    public ApiErrorResponse(int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    /** エラー発生日時を取得します */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** HTTPステータスコードを取得します */
    public int getStatus() {
        return status;
    }

    /** エラーメッセージを取得します */
    public String getMessage() {
        return message;
    }
}