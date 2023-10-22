package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

/**
 * service가 controller에게 모든 정보를 전달할 필요 없음
 * 그래서 Dto 클래스를 만들어서 Entity의 정보 중 필요한 정보 선별하여 전달
 *
 * AccountDto : Controller와 Service 사이 담당
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {

    private Long userId;
    private String accountNumber;
    private Long balance;
    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnRegisteredAt())
                .build();
    }

}
