package com.westerhoud.osrs.taskman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westerhoud.osrs.taskman.TaskmanConfig;
import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.LoginRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthenticationService {

  public static final String AUTHORIZATION_HEADER = "authorization";
  private final TaskmanConfig config;
  private final HttpClient client;
  private final URI uri;

  public AuthenticationService(final TaskmanConfig config) throws URISyntaxException {
    this.config = config;
    client = HttpClient.newHttpClient();
    uri = new URI(config.url() + "/login");
  }

  public Account login() throws IOException, InterruptedException {
    final LoginRequest loginRequest = new LoginRequest(config.username(), config.password());
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(loginRequest.toJson()))
            .build();
    final HttpResponse<String> response =
        client.send(request, HttpResponse.BodyHandlers.ofString());
    final Account account = new ObjectMapper().readValue(response.body(), Account.class);
    account.setJwt(response.headers().map().get(AUTHORIZATION_HEADER).get(0));
    return account;
  }
}
