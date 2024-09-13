package com.example.algoyai.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author JSW
 *
 * GlobalExceptionHandler 클래스는 애플리케이션 전역에서 발생하는 예외를 처리하는 역할을 합니다.
 * 이 클래스는 Spring의 @RestControllerAdvice를 사용하여 전역 예외 처리 기능을 제공합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 모든 종류의 예외를 처리하는 메서드입니다.
	 * 발생한 예외를 잡아 HTTP 500 상태 코드와 함께 사용자에게 응답을 반환합니다.
	 *
	 * @param e 처리할 예외 객체
	 * @return HTTP 500 상태 코드와 예외 메시지를 포함한 ResponseEntity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
	}
}