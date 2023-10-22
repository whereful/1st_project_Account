package com.example.account.service;

import com.example.account.aop.AccountLockIdInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Around : aspectj을 어떤 경우에 적용할 것인가에 대한 설명
 * <p>
 * lockService.lock : lock 취득 시도
 * <p>
 * lockService.unlock : lock 취득 해제
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    @Around("@annotation(com.example.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            AccountLockIdInterface request
    ) throws Throwable {
        lockService.lock(request.getAccountNumber());
        try {
            return pjp.proceed();
        } finally {
            lockService.unlock(request.getAccountNumber());
        }

    }
}
