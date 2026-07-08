package com.example.app.service.impl;

import com.example.app.service.LoginAttemptService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * アプリケーション内でログイン試行回数を安全に管理するServiceです。
 */
@Service
public class InMemoryLoginAttemptService implements LoginAttemptService {

    /** 一つの識別子で許可する連続失敗回数です */
    private static final int MAX_FAILURES = 5;

    /** 失敗回数を保持する期間です */
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    /** ログイン識別子ごとの失敗状況です */
    private final ConcurrentHashMap<String, FailureState> failureStates = new ConcurrentHashMap<>();

    /** 現在時刻を取得する、テストで差し替え可能な部品です */
    private final Clock clock;

    /** システム時刻を利用してServiceを生成します */
    public InMemoryLoginAttemptService() {
        this(Clock.systemUTC());
    }

    /** テスト用の時刻部品を受け取ってServiceを生成します */
    InMemoryLoginAttemptService(Clock clock) {
        this.clock = clock;
    }

    @Override
    public boolean isAllowed(String loginIdentifier) {
        // 現在の失敗状況を取得します
        FailureState state = failureStates.get(loginIdentifier);
        if (state == null) {
            return true;
        }

        // 保持期間を過ぎた記録は削除して再試行を許可します
        if (!state.lastFailure().plus(LOCK_DURATION).isAfter(clock.instant())) {
            failureStates.remove(loginIdentifier, state);
            return true;
        }

        return state.count() < MAX_FAILURES;
    }

    @Override
    public void recordFailure(String loginIdentifier) {
        // 同時アクセスでも失敗回数を安全に更新します
        failureStates.compute(loginIdentifier, (key, currentState) -> {
            Instant now = clock.instant();
            if (currentState == null || !currentState.lastFailure().plus(LOCK_DURATION).isAfter(now)) {
                return new FailureState(1, now);
            }
            return new FailureState(currentState.count() + 1, now);
        });
    }

    @Override
    public void clearFailures(String loginIdentifier) {
        // 認証に成功した識別子の失敗記録を削除します
        failureStates.remove(loginIdentifier);
    }

    /** 失敗回数と最後に失敗した日時を保持します */
    private record FailureState(int count, Instant lastFailure) {
    }
}
