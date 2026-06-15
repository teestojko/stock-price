package com.example.app.repository.auth;

import com.example.app.entity.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ユーザー情報をDBから取得・保存するRepositoryです。
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * メールアドレスでユーザーを検索します。
     *
     * @param email 検索対象のメールアドレス
     * @return 該当ユーザー。存在しない場合は空
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * メールアドレスがすでに登録済みか確認します。
     *
     * @param email 確認対象のメールアドレス
     * @return 登録済みならtrue
     */
    boolean existsByEmail(String email);
}