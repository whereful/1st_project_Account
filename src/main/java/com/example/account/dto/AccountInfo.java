package com.example.account.dto;

import lombok.*;

/**
 * AccountInfo : Client와 Controller 사이 담당
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    private String accountNumber;
    private Long balance;
}
