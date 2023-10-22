package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import com.example.account.type.AccountStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Mock = 가짜
 * ObjectMapper : json과 문자열을 다루는 클래스
 * <p>
 * Mock : accountService, redisTestService
 * 진짜 : AccountController
 * <p>
 * AccountController 안으로 Mock으로 설정된 accountService, redisTestService가 주입
 * <p>
 * mockMvc : Mock을 이용하여 테스트가 진행되도록 하는 클래스
 */


/**
 * successCreateAccount에서의 given, when, then 설정
 * <p>
 * given : 임의의 userId, initialBalance가 입력되어도 테스트에서 정해진 AccountDto 객체 반환하도록
 * mock 방식의 accountService가 설정
 * <p>
 * when
 * <p>
 * then : 새로운 post 요청을 보냈을 때 반환되는 결과는 기존에 정해진 AccountDto 객체의 정보와 일치하는 값들이 반환
 * 된다고 예상
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successCreateAccount() throws Exception {

        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unregisteredAt(LocalDateTime.now())
                        .build());

        // when

        // then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(1L, 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    /**
     * successCreateAccount와 같은 맥락
     */
    @Test
    void successDeleteAccount() throws Exception {

        // given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unregisteredAt(LocalDateTime.now())
                        .build());

        // when

        // then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(1L, "0987654321")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    /**
     * successCreateAccount와 같은 맥락
     */
    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when

        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    /**
     * successCreateAccount와 같은 맥락
     */
    @Test
    void successGetAccountByUserId() throws Exception {

        // given
        List<AccountDto> accountDtos = Arrays.asList(
                AccountDto.builder()
                        .accountNumber("1111111111")
                        .balance(1000L).build(),
                AccountDto.builder()
                        .accountNumber("2222222222")
                        .balance(2000L).build(),
                AccountDto.builder()
                        .accountNumber("3333333333")
                        .balance(3000L).build()
        );

        // when

        // then
        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);

        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[0].balance").value(1000L))
                .andExpect(jsonPath("$[1].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[1].balance").value(2000L))
                .andExpect(jsonPath("$[2].accountNumber").value("3333333333"))
                .andExpect(jsonPath("$[2].balance").value(3000L));
    }
}