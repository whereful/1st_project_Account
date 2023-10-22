package com.example.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;


/**
 * 사용자
 * database에 미리 정의된 user 테이블과 이름이 동일하면 오류 발생할 수 있음
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class AccountUser extends BaseEntity {
    private String name;
}
