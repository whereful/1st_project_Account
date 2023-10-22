package com.example.account.exception;

import com.example.account.dto.ErrorResponse;
import com.example.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 맨 처음 : 사용자가 작성한 예외에 대한 코드
 * 중간 : 사용자가 정의하지 않았지만 자주 발생하는 예외에 대한 코드
 * 마지막 : 나머지 예외에 대한 코드
 *
 * DataIntegrityViolationException : database에서 중복 키 오류 등을 다룸
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e) {
        log.error("{} is occured.", e.getErrorCode());
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occured.", e);

        return new ErrorResponse(ErrorCode.INVALID_NUMBER,
                ErrorCode.INVALID_NUMBER.getDescription());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is occured.", e);
        return new ErrorResponse(ErrorCode.INVALID_REQUEST,
                ErrorCode.INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception is occured.", e);
        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
    }
}
