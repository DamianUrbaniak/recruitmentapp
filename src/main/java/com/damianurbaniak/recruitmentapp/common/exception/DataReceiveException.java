package com.damianurbaniak.recruitmentapp.common.exception;

import lombok.Getter;

@Getter
public class DataReceiveException extends RuntimeException {

  private final int status;

  public DataReceiveException(final String message, final int status) {
    super(message);
    this.status = status;
  }
}
