package ch.uzh.ifi.hase.soprafs24.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

  // @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
  // protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
  //   String bodyOfResponse = "This should be application specific";
  //   return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
  // }

  // Example handler for IllegalArgumentException
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
      // Directly return a string body with the error message
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  // You can add more handlers here for different types of exceptions
  // For example, handling IllegalStateException
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
      // Here we can choose an appropriate HTTP status based on the exception
      return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(TransactionSystemException.class)
  public ResponseStatusException handleTransactionSystemException(Exception ex, HttpServletRequest request) {
    log.error("Request: {} raised {}", request.getRequestURL(), ex);
    return new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
  }

  // Keep this one disable for all testing purposes -> it shows more detail with
  // this one disabled
  @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
  public ResponseStatusException handleException(Exception ex) {
    log.error("Default Exception Handler -> caught:", ex);
    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
  }
}