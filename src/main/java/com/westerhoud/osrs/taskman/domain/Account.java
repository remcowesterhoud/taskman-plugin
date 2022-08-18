package com.westerhoud.osrs.taskman.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    private Long id;
    private String username;
    private String role;
    private String tier;
    @JsonIgnore
    private String jwt;

    public String getAuthorizationHeader() {
        return String.format("Bearer %s", jwt);
    }
}
