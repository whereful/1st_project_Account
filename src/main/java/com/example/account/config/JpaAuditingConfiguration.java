package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @EntityListeners(AuditingEntityListener.class) 생성 날짜, 수정 날짜 자동 설정을 위한 클래스
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
