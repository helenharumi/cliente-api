package br.com.impacta.customers.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
	
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<DefaultError> objectNotFound(ObjectNotFoundException e, HttpServletRequest request) {
		DefaultError error = new DefaultError(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<DefaultError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
		
		ValidatorErrors error = new ValidatorErrors(HttpStatus.BAD_REQUEST.value(), "Error the validatition", System.currentTimeMillis());
	
		e.getBindingResult()
			.getFieldErrors()
			.forEach( x -> error.addError(x.getField(), x.getDefaultMessage()));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(DataBaseException.class)
	public ResponseEntity<DefaultError> entityNotFound(DataBaseException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		DefaultError err = new DefaultError(status.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(ResourceExistsException.class)
	public ResponseEntity<DefaultError> entityNotFound(ResourceExistsException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		DefaultError err = new DefaultError(status.value(), e.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(status).body(err);
	}

}