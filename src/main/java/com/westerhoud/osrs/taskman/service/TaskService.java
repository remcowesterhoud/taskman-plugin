package com.westerhoud.osrs.taskman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westerhoud.osrs.taskman.TaskmanConfig;
import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.AccountTask;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
public class TaskService {

  private final HttpClient client;
  private URI baseUri;

  public TaskService(final URI baseUri) {
    client = HttpClient.newHttpClient();
    this.baseUri = baseUri;
  }

  public Optional<AccountTask> getCurrentTask(final Account account) {
    final HttpRequest request;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(
                  new URI(String.format("%s/account/%d/task", baseUri.toString(), account.getId())))
              .header("Authorization", account.getAuthorizationHeader())
              .GET()
              .build();
    } catch (URISyntaxException e) {
      log.error("Invalid URI", e);
      return Optional.empty();
    }

    final HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      log.error(
          String.format(
              "Could not send current task request to server for user %s", account.getUsername()),
          e);
      return Optional.empty();
    }

    switch (response.statusCode()) {
      case 200:
        return mapResponseToTask(response);
      case 403:
        log.error(
            String.format(
                "User %s not authorized to get current task for id %d",
                account.getUsername(), account.getId()));
        return Optional.empty();
      case 404:
        log.info("User has no current task");
        return Optional.empty();
      default:
        return Optional.empty();
    }
  }

  private Optional<AccountTask> mapResponseToTask(HttpResponse<String> response) {
    try {
      return Optional.of(new ObjectMapper().readValue(response.body(), AccountTask.class));
    } catch (IOException e) {
      log.error(String.format("Could not map response %s to AccountTask", response.body()), e);
      return Optional.empty();
    }
  }
}
