package com.example.app.exception;

/** コンソールに原因出力用に追加 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * アプリケーション全体の例外を安全に処理するクラスです。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 例外内容をサーバー側に記録するロガーです
     *  コンソールに原因出力用に追加
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 入力値バリデーションエラーを処理します。
     *
     * @param exception バリデーション例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        // 最初のバリデーションエラーのみ返します
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("入力内容が正しくありません");

        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 認証失敗や重複登録などの業務エラーを処理します。
     *
     * @param exception 業務例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    /** ログイン認証失敗を401として安全に返します */
    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleAuthenticationFailedException(AuthenticationFailedException exception) {
        return new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    /** ログイン試行上限超過を429として返します */
    @ExceptionHandler(LoginAttemptLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiErrorResponse handleLoginAttemptLimitExceededException(LoginAttemptLimitExceededException exception) {
        return new ApiErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), exception.getMessage());
    }

    /**
     * 想定外エラーを処理します。
     *
     * @param exception 想定外例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(Exception exception) {
        // 内部エラーの詳細はクライアントへ返しません
        return new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "サーバー内部でエラーが発生しました"
        );
    }
}
