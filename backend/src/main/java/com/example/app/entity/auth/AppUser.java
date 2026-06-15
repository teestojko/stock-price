package com.example.app.entity.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * アプリケーション利用者を表すEntityです。
 * DBのユーザー情報と対応します。
 */
@Entity
@Table(name = "app_users")
public class AppUser {

    /** ユーザーを一意に識別するIDです */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // Oracleで GenerationType.IDENTITY がエラーになる場合は、以下のように SEQUENCE を使用する方法もあります。
    
    // @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_users_seq_generator")
    // @SequenceGenerator(
    //     name = "app_users_seq_generator",
    //     sequenceName = "APP_USERS_SEQ",
    //     allocationSize = 1
    // )
    // @Column(name = "ID")
    // private Long id;

    /** ログインIDとして使用するメールアドレスです */
    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    private String email;

    /** BCryptでハッシュ化されたパスワードです */
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    /** 画面表示用のユーザー名です */
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    /** 権限を表す文字列です */
    @Column(name = "ROLE", nullable = false, length = 50)
    private String role;

    /** ユーザー作成日時です */
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    /**
     * JPAがEntityを生成するためのデフォルトコンストラクタです。
     */
    protected AppUser() {
    }

    /**
     * 新規ユーザー作成時に使用するコンストラクタです。
     *
     * @param email メールアドレス
     * @param password ハッシュ化済みパスワード
     * @param name ユーザー名
     * @param role 権限
     */
    public AppUser(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    /** IDを取得します */
    public Long getId() {
        return id;
    }

    /** メールアドレスを取得します */
    public String getEmail() {
        return email;
    }

    /** ハッシュ化済みパスワードを取得します */
    public String getPassword() {
        return password;
    }

    /** ユーザー名を取得します */
    public String getName() {
        return name;
    }

    /** 権限を取得します */
    public String getRole() {
        return role;
    }

    /** 作成日時を取得します */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}