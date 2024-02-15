package com.damianurbaniak.recruitmentapp.common.controller;

import com.damianurbaniak.recruitmentapp.common.exception.CustomErrorResponse;
import com.damianurbaniak.recruitmentapp.common.exception.DataReceiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class ControllerAdvice {

  @ExceptionHandler(DataReceiveException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  CustomErrorResponse alreadyExistsException(final DataReceiveException exception) {
    log.warn(exception.getMessage(), exception);
    return new CustomErrorResponse(exception.getStatus(), exception.getMessage());
  }
}
