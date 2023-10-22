package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JpaRepository : 함수명으로 sql 쿼리를 자동 생성
 * Optional : Null에 대한 예외를 포함하는 타입
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();
    Integer countByAccountUser(AccountUser accountUser);
    Optional<Account> findByAccountNumber(String AccountNumber);
    List<Account> findByAccountUser(AccountUser accountUser);
}
