package com.westerhoud.osrs.taskman.service;

import com.google.gson.Gson;
import com.westerhoud.osrs.taskman.api.TaskService;
import com.westerhoud.osrs.taskman.domain.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class SheetService implements TaskService {

  private static final String BASE_URL = "https://osrs-taskman.herokuapp.com/sheet";
  private final OkHttpClient client;
  private final Gson gson;
  private final String currentUrl;
  private final String generateUrl;
  private final String completeUrl;
  private final String progressUrl;

  public SheetService(OkHttpClient okHttpClient) {
    this.client = okHttpClient;
    this.gson = new Gson();
    this.currentUrl = BASE_URL + "/current";
    this.generateUrl = BASE_URL + "/generate";
    this.completeUrl = BASE_URL + "/complete";
    this.progressUrl = BASE_URL + "/progress";
  }

  public Task getCurrentTask(final String key) throws IOException {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException(
          "Please set your username / spreadsheet key in the plugin configurations");
    }

    final Request request =
        new Request.Builder().url(String.format("%s?key=%s", currentUrl, key)).get().build();

    return executeRequest(request);
  }

  public Task generateTask(final AccountCredentials credentials) throws IOException {
    final Request request =
        new Request.Builder()
            .url(generateUrl)
            .header("Content-Type", "application/json")
            .post(getRequestBody(credentials.getIdentifier(), credentials.getPassword()))
            .build();
    return executeRequest(request);
  }

  public Task completeTask(final AccountCredentials credentials) throws IOException {
    final Request request =
        new Request.Builder()
            .url(completeUrl)
            .header("Content-Type", "application/json")
            .post(getRequestBody(credentials.getIdentifier(), credentials.getPassword()))
            .build();
    return executeRequest(request);
  }

  public AccountProgress getAccountProgress(final String key) throws IOException {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException(
          "Please set your username / spreadsheet key in the plugin configurations");
    }

    final Request request =
        new Request.Builder().url(String.format("%s?key=%s", progressUrl, key)).get().build();

    final Response response = client.newCall(request).execute();

    if (response.code() == 200) {
      return gson.fromJson(response.body().string(), AccountProgress.class);
    }

    ErrorResponse error = mapResponseToErrorResponse(response);
    throw new IllegalArgumentException(error.getMessage());
  }

  private RequestBody getRequestBody(final String key, final String passphrase) {
    final SheetRequestBody body = new SheetRequestBody(key, passphrase);
    return RequestBody.create(MediaType.parse("application/json"), gson.toJson(body));
  }

  private Task executeRequest(final Request request) throws IOException {
    final Response response = client.newCall(request).execute();

    if (response.code() == 200) {
      return mapResponseToTask(response);
    }

    ErrorResponse error = mapResponseToErrorResponse(response);
    throw new IllegalArgumentException(error.getMessage());
  }

  private Task mapResponseToTask(final Response response) throws IOException {
    return gson.fromJson(response.body().string(), Task.class);
  }

  private ErrorResponse mapResponseToErrorResponse(final Response response) throws IOException {
    final String responseString = response.body().string();
    log.error(responseString);
    return gson.fromJson(responseString, ErrorResponse.class);
  }
}
