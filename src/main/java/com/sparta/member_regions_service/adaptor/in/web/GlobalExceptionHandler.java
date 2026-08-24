package com.sparta.member_regions_service.adaptor.in.web;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.member_regions_service.application.exception.ForbiddenException;
import com.sparta.member_regions_service.application.exception.UnauthorizedException;
import com.sparta.member_regions_service.domain.exception.DuplicateMemberRegionException;
import com.sparta.member_regions_service.domain.exception.MemberRegionLimitExceededException;
import com.sparta.member_regions_service.domain.exception.MemberRegionNotFoundException;
import com.sparta.member_regions_service.domain.exception.RegionNotFoundException;
import com.sparta.member_regions_service.domain.exception.RegionVerificationFailedException;

import jakarta.servlet.http.HttpServletRequest;

// API 예외 → 표준 Error Response
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpServletRequest request
	) {
		List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(this::toFieldError)
				.toList();
		Map<String, Object> body = baseError(
				HttpStatus.BAD_REQUEST,
				"VALIDATION_FAILED",
				"요청 값이 올바르지 않습니다.",
				request.getRequestURI()
		);
		body.put("fieldErrors", fieldErrors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex,
			HttpServletRequest request
	) {
		return buildError(
				HttpStatus.BAD_REQUEST,
				"INVALID_ARGUMENT",
				"요청 본문을 읽을 수 없습니다.",
				request.getRequestURI()
		);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgument(
			IllegalArgumentException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Map<String, Object>> handleUnauthorized(
			UnauthorizedException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.UNAUTHORIZED, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Map<String, Object>> handleForbidden(
			ForbiddenException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.FORBIDDEN, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MemberRegionNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleMemberRegionNotFound(
			MemberRegionNotFoundException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(RegionNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleRegionNotFound(
			RegionNotFoundException ex,
			HttpServletRequest request
	) {
		return buildError(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler({
			MemberRegionLimitExceededException.class,
			DuplicateMemberRegionException.class,
			RegionVerificationFailedException.class
	})
	public ResponseEntity<Map<String, Object>> handleDomainConflict(
			RuntimeException ex,
			HttpServletRequest request
	) {
		String code = resolveConflictCode(ex);
		return buildError(HttpStatus.CONFLICT, code, ex.getMessage(), request.getRequestURI());
	}

	private String resolveConflictCode(RuntimeException ex) {
		if (ex instanceof MemberRegionLimitExceededException limitEx) {
			return limitEx.getCode();
		}
		if (ex instanceof DuplicateMemberRegionException duplicateEx) {
			return duplicateEx.getCode();
		}
		if (ex instanceof RegionVerificationFailedException verificationEx) {
			return verificationEx.getCode();
		}
		return "CONFLICT";
	}

	private Map<String, String> toFieldError(FieldError fieldError) {
		Map<String, String> item = new LinkedHashMap<>();
		item.put("field", fieldError.getField());
		item.put("code", fieldError.getCode() == null ? "INVALID" : fieldError.getCode());
		item.put(
				"message",
				fieldError.getDefaultMessage() == null ? "값이 올바르지 않습니다." : fieldError.getDefaultMessage()
		);
		return item;
	}

	private ResponseEntity<Map<String, Object>> buildError(
			HttpStatus status,
			String code,
			String message,
			String path
	) {
		return ResponseEntity.status(status).body(baseError(status, code, message, path));
	}

	private Map<String, Object> baseError(HttpStatus status, String code, String message, String path) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("code", code);
		body.put("message", message);
		body.put("path", path);
		return body;
	}
}
