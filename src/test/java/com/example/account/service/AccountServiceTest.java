package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository userRepository;

    @InjectMocks
    private AccountService accountService;


    /**
     * given : 임의의 userId가 입력되어도 테스트에서 정해진 AccountUser 객체 반환하도록
     * mock 방식의 userRepository가 설정
     * <p>
     * given : id가 가장 큰 Account가 반환되었을 때 그 결과는 항상
     * 계좌번호가 1000000012인 Account가 반환되도록 mock 방식의 accountRepository가 설정
     * <p>
     * given : accountRepository가 무엇을 저장하든 항상 사용자는 미리 정의된 사용자이고
     * 계좌번호가 1000000015인 Account가 저장되도록 mock 방식의 accountRepository가 설정
     * <p>
     * when : given과 같은 상황에서 해당 코드가 실행(실행하는 주체는 Mock X)
     * <p>
     * then : when에서 실행되고 난 이후에 대한 결과 검증
     */
    @Test
    void createAccountSuccess() {

        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("pobi")
                .build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012")
                        .build()));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);


        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000013", captor.getValue().getAccountNumber());


    }


    @Test
    void deleteAccountSuccess() {

        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("pobi")
                .build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());

    }


    @Test
    void createFirstAccount() {

        // given
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pobi")
                .build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);


        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());

    }


    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound() {

        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.createAccount(1L, 1000L));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("유저 당 최대 계좌는 10개 ")
    void createAccount_maxAccountIs10() {

        // given
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("pobi")
                .build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.createAccount(1L, 1000L));

        // then
        assertEquals(ErrorCode.MAX_COUNT_PER_USER_10, exception.getErrorCode());

    }


    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void deleteAccount_UserNotFound() {

        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.deleteAccount(1L, "1234567890"));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    void deleteAccount_AccountNotFound() {

        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("pobi")
                .build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.deleteAccount(1L, "1234567890"));

        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }


    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccountFailed_userUnMatch() {

        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();
        AccountUser harry = AccountUser.builder()
                .id(13L)
                .name("Harry")
                .build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.deleteAccount(1L, "1234567890"));

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());

    }


    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다")
    void deleteAccountFailed_balanceNotEmpty() {

        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();
        AccountUser harry = AccountUser.builder()
                .id(13L)
                .name("Harry")
                .build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .balance(100L)
                        .accountNumber("1000000012")
                        .build()));

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.deleteAccount(1L, "1234567890"));

        // then
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());

    }


    @Test
    @DisplayName("해지된 계좌는 해지할 수 없다")
    void deleteAccountFailed_alreadyUnregistered() {

        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .balance(0L)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1000000012")
                        .build()));

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.deleteAccount(1L, "1234567890"));

        // then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());

    }


    @Test
    void successGetAccountsByUserId() {

        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountNumber("1111111111")
                        .accountUser(pobi)
                        .balance(1000L).build(),
                Account.builder()
                        .accountNumber("2222222222")
                        .accountUser(pobi)
                        .balance(2000L).build(),
                Account.builder()
                        .accountNumber("3333333333")
                        .accountUser(pobi)
                        .balance(3000L).build()
        );

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        // when
        List<AccountDto> accountDtoList = accountService.getAccountsByUserId(1L);

        // then
        assertEquals(3, accountDtoList.size());
        assertEquals("1111111111", accountDtoList.get(0).getAccountNumber());
        assertEquals(1000L, accountDtoList.get(0).getBalance());
        assertEquals("2222222222", accountDtoList.get(1).getAccountNumber());
        assertEquals(2000L, accountDtoList.get(1).getBalance());
        assertEquals("3333333333", accountDtoList.get(2).getAccountNumber());
        assertEquals(3000L, accountDtoList.get(2).getBalance());

    }

    @Test
    void failedToGetAccount() {

        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception =
                assertThrows(AccountException.class,
                        () -> accountService.getAccountsByUserId(1L));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

}