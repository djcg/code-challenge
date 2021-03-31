package com.hospital.codechallengeapi.controller;

import com.hospital.codechallengeapi.exception.AppointmentCreationException;
import com.hospital.codechallengeapi.exception.UserAlreadyExistsException;
import com.hospital.codechallengeapi.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse onUserAlreadyExistsException(UserAlreadyExistsException exception) {
    return new ErrorResponse(exception.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse onConstraintViolationException(ConstraintViolationException exception) {
    List<ConstraintViolation> fieldErrors = new ArrayList<>(exception.getConstraintViolations());
    String message =
        fieldErrors.stream()
            .map(error -> "'" + error.getPropertyPath() + "' " + error.getMessage())
            .collect(Collectors.joining(", "));
    return new ErrorResponse(message);
  }

  @ExceptionHandler(AppointmentCreationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse onAppointmentCreationException(AppointmentCreationException exception) {
    return new ErrorResponse(exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    String message =
            exception.getBindingResult().getFieldErrors().stream()
                    .map(error -> "'" + error.getField() + "' : " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
    return new ErrorResponse(message);
  }
}
