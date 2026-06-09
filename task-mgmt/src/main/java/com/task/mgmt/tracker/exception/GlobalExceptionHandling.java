package com.task.mgmt.tracker.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class GlobalExceptionHandling {

	@ExceptionHandler(AppException.class)

	public ResponseEntity<ErrorResponse> userHandlerException(AppException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.toString());
		errorResponse.setMessage(exception.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NotFoundException.class)

	public ResponseEntity<ErrorResponse> userHandlerException(NotFoundException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.toString());
		errorResponse.setMessage(exception.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(AlreadyExistsException.class)

	public ResponseEntity<ErrorResponse> userHandlerException(AlreadyExistsException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.toString());
		errorResponse.setMessage(exception.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
	    List<String> errors = ex.getBindingResult().getFieldErrors()
	            .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
	    return new ResponseEntity<>(getErrorsMap (errors), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LogOutFailedException.class)

	public ResponseEntity<ErrorResponse> userHandlerException(LogOutFailedException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(HttpStatus.EXPECTATION_FAILED.toString());
		errorResponse.setMessage(exception.getMessage());
		errorResponse.setTimeStamp(System.currentTimeMillis());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.EXPECTATION_FAILED);
	}
	
	
	@ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.FORBIDDEN.toString());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
	
	private Map<String, List<String>> getErrorsMap(List<String> errors) {
	    Map<String, List<String>> errorResponse = new HashMap<>();
	    errorResponse.put("errors", errors);
	    return errorResponse;
	}
}
