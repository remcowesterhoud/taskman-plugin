package com.westerhoud.osrs.taskman.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  private String username;
  private String password;

  @SneakyThrows
  public String toJson() {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(this);
  }
}
