package com.westerhoud.osrs.taskman.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountCredentials {

  final String identifier;
  final String password;
  final TaskSource source;

  public boolean isValid() {
    return identifier != null
        && !identifier.isEmpty()
        && password != null
        && !password.isEmpty()
        && source != null;
  }
}
