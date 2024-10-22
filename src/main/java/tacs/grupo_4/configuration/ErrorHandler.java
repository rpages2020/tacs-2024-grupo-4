package tacs.grupo_4.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tacs.grupo_4.exceptions.ErrorResponseDTO;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Profile("api")
@RestControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, org.springframework.http.HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error("BindException: ", ex);
    var errorHTTP = ErrorResponseDTO.builder()
        .title("One or more fields are invalid")
        .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .detail(ex.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining("; ")))
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(errorHTTP);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDTO> handleApiException(MethodArgumentTypeMismatchException ex) {

    log.error("MethodArgumentTypeMismatchException", ex);

    var errorResponseDTO = ErrorResponseDTO.builder()
        .title("Parameter type mismatch")
        .statusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .detail(String.format("The value '%s' mismatch the type '%s' of parameter '%s'", ex.getValue(), ex.getParameter().getParameterType(), ex.getParameter().getParameterName()))
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(errorResponseDTO);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponseDTO> handleNoSuchElementException(NoSuchElementException ex) {

    log.info("The requested resource was not found", ex);

    var errorResponseDTO = ErrorResponseDTO.builder()
        .title("Resource not found")
        .statusCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
        .detail("The requested resource was not found")
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(errorResponseDTO);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, org.springframework.http.HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
    var errorResponseDTO = ErrorResponseDTO.builder()
        .title("Generic exception handling")
        .statusCode(String.valueOf(statusCode.value()))
        .detail(ex.getLocalizedMessage())
        .build();

    return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(errorResponseDTO);
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {

    log.error(ex.getMessage(), ex);

    var errorResponseDTO = ErrorResponseDTO.builder()
        .title("Unexpected Error")
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
        .detail("Check logs for more details")
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(errorResponseDTO);
  }



}
