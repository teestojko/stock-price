package com.example.app.service.impl;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ログイン試行回数管理Serviceの単体テストです。
 */
class InMemoryLoginAttemptServiceTest {

    /** 連続失敗が上限に達すると拒否されることを確認します */
    @Test
    void rejectsAttemptAfterFiveFailures() {
        // テスト中に変化しない現在時刻を用意します
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        InMemoryLoginAttemptService service = new InMemoryLoginAttemptService(clock);

        for (int failureCount = 0; failureCount < 5; failureCount++) {
            service.recordFailure("user@example.com");
        }

        assertFalse(service.isAllowed("user@example.com"));
        service.clearFailures("user@example.com");
        assertTrue(service.isAllowed("user@example.com"));
    }
}
