package com.westerhoud.osrs.taskman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westerhoud.osrs.taskman.TaskmanConfig;
import com.westerhoud.osrs.taskman.domain.Account;
import com.westerhoud.osrs.taskman.domain.AccountTask;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TaskService {

  private final TaskmanConfig config;
  private final HttpClient client;
  private final URI baseUri;

  public TaskService(final TaskmanConfig config) throws URISyntaxException {
    this.config = config;
    client = HttpClient.newHttpClient();
    baseUri = new URI(config.url());
  }

  public AccountTask getCurrentTask(final Account account)
      throws URISyntaxException, IOException, InterruptedException {
    final URI uri =
        new URI(String.format("%s/account/%d/task", baseUri.toString(), account.getId()));
    final HttpRequest request =
        HttpRequest.newBuilder().uri(uri).header("Authorization", account.getAuthorizationHeader()).GET().build();
    final HttpResponse<String> response =
        client.send(request, HttpResponse.BodyHandlers.ofString());
    return new ObjectMapper().readValue(response.body(), AccountTask.class);
  }
}
