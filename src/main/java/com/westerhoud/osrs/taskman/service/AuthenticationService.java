package com.westerhoud.osrs.taskman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.LoginRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
public class AuthenticationService {

  public static final String AUTHORIZATION_HEADER = "authorization";
  private final HttpClient client;
  private final URI baseUri;

  public AuthenticationService(final URI baseUri) {
    client = HttpClient.newHttpClient();
    this.baseUri = baseUri;
  }

  public Optional<Account> login(final String username, final String password) {
    log.info(String.format("Logging in user %s", username));
    final LoginRequest loginRequest = new LoginRequest(username, password);

    final HttpRequest request;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(String.format("%s/login", baseUri.toString())))
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(loginRequest.toJson()))
              .build();
    } catch (URISyntaxException e) {
      log.error("Invalid URI", e);
      return Optional.empty();
    }

    final HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      log.error(String.format("Could not send login request to server for user %s", username), e);
      return Optional.empty();
    }

    switch (response.statusCode()) {
      case 200:
        return mapResponseToAccount(response);
      case 401:
        log.info(String.format("Invalid credentials entered for user %s", username));
        return Optional.empty();
      default:
        log.error(
            String.format(
                "Something went wrong logging in user %s. Please inspect server logs", username));
        return Optional.empty();
    }
  }

  private Optional<Account> mapResponseToAccount(final HttpResponse<String> response) {
    try {
      final Account account = new ObjectMapper().readValue(response.body(), Account.class);
      account.setJwt(response.headers().map().get(AUTHORIZATION_HEADER).get(0));
      return Optional.of(account);
    } catch (IOException e) {
      log.error(String.format("Could not map response %s to Account", response.body()), e);
      return Optional.empty();
    }
  }
}
