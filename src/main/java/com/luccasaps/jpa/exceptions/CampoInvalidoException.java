package com.luccasaps.jpa.exceptions;

import lombok.Getter;

public class CampoInvalidoException extends RuntimeException {

    @Getter
    private String campo;

  public CampoInvalidoException(String campo, String msg) {
    super(msg);
    this.campo = campo;
  }
}
