package com.damianurbaniak.recruitmentapp.common.exception;

public record CustomErrorResponse(int status,
                                  String message) {
}
