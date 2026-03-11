package com.example.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // 테이블을 의미
@Table(name="MSA_USER_TABLE") // 테이블의 네이밍 가능, 없이 사용하면 class명이 테이블명이 됨
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
        @Id
    private String email;

    @Column(unique = true, nullable = false, length = 255)
    private String password;

    private String name;

    private String role;
}
