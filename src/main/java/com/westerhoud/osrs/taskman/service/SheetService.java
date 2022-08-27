package com.westerhoud.osrs.taskman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westerhoud.osrs.taskman.domain.ErrorResponse;
import com.westerhoud.osrs.taskman.domain.SheetRequestBody;
import com.westerhoud.osrs.taskman.domain.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class SheetService {

  private static final String BASE_URL = "https://osrs-taskman.herokuapp.com/sheet";
  private final HttpClient client;
  private final String currentUrl;
  private final URI generateUrl;
  private final URI completeUrl;

  public SheetService() throws URISyntaxException {
    client = HttpClient.newHttpClient();
    this.currentUrl = BASE_URL + "/current";
    this.generateUrl = new URI(BASE_URL + "/generate");
    this.completeUrl = new URI(BASE_URL + "/complete");
  }

  public Task getCurrentTask(final String key, final String passphrase)
      throws URISyntaxException, IOException, InterruptedException {
    if (key == null || key.isEmpty() || passphrase == null || passphrase.isEmpty()) {
      throw new IllegalArgumentException("Please set your key and passphrase in the plugin configurations");
    }

    final HttpRequest request;
    request =
        HttpRequest.newBuilder()
            .uri(new URI(String.format("%s?key=%s&passphrase=%s", currentUrl, key, passphrase)))
            .GET()
            .build();

    final HttpResponse<String> response =
        client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      return mapResponseToTask(response);
    }

    log.error(response.body());
    ErrorResponse error = mapResponseToErrorResponse(response);
    throw new IllegalArgumentException(error.getMessage());
  }

  public Task generateTask(final String key, final String passphrase) throws IOException, InterruptedException {
    final HttpRequest request;
    request =
            HttpRequest.newBuilder()
                    .uri(generateUrl)
                    .setHeader("Content-Type", "application/json")
                    .POST(getRequestBody(key, passphrase))
                    .build();

    final HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      return mapResponseToTask(response);
    }

    log.error(response.body());
    ErrorResponse error = mapResponseToErrorResponse(response);
    throw new IllegalArgumentException(error.getMessage());
  }

  private Task mapResponseToTask(final HttpResponse<String> response) throws IOException {
    return new ObjectMapper().readValue(response.body(), Task.class);
  }

  private ErrorResponse mapResponseToErrorResponse(final HttpResponse<String> response)
      throws IOException {
    return new ObjectMapper().readValue(response.body(), ErrorResponse.class);
  }

  private HttpRequest.BodyPublisher getRequestBody(final String key, final String passphrase) throws IOException {
    final SheetRequestBody body = new SheetRequestBody(key, passphrase);
    return HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(body));
  }
}
