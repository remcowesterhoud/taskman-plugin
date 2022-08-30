package com.westerhoud.osrs.taskman.service;

import com.google.gson.Gson;
import com.westerhoud.osrs.taskman.domain.ErrorResponse;
import com.westerhoud.osrs.taskman.domain.Progress;
import com.westerhoud.osrs.taskman.domain.SheetRequestBody;
import com.westerhoud.osrs.taskman.domain.Task;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class SheetService {

  private static final String BASE_URL = "https://osrs-taskman.herokuapp.com/sheet";
  final OkHttpClient client;
  final Gson gson;
  private final String currentUrl;
  private final String generateUrl;
  private final String completeUrl;
  private final String progressUrl;

  public SheetService() {
    this.client = new OkHttpClient();
    this.gson = new Gson();
    this.currentUrl = BASE_URL + "/current";
    this.generateUrl = BASE_URL + "/generate";
    this.completeUrl = BASE_URL + "/complete";
    this.progressUrl = BASE_URL + "/progress";
  }

  public Task getCurrentTask(final String key, final String passphrase) throws IOException {
    if (key == null || key.isEmpty() || passphrase == null || passphrase.isEmpty()) {
      throw new IllegalArgumentException(
          "Please set your key and passphrase in the plugin configurations");
    }

    final Request request =
        new Request.Builder()
            .url(String.format("%s?key=%s&passphrase=%s", currentUrl, key, passphrase))
            .get()
            .build();

    return executeRequest(request);
  }

  public Task generateTask(final String key, final String passphrase) throws IOException {
    final Request request =
        new Request.Builder()
            .url(generateUrl)
            .header("Content-Type", "application/json")
            .post(getRequestBody(key, passphrase))
            .build();
    return executeRequest(request);
  }

  public Task completeTask(final String key, final String passphrase) throws IOException {
    final Request request =
        new Request.Builder()
            .url(completeUrl)
            .header("Content-Type", "application/json")
            .post(getRequestBody(key, passphrase))
            .build();
    return executeRequest(request);
  }

  public Progress progress(final String key, final String passphrase) throws IOException {
    final Request request =
        new Request.Builder()
            .url(String.format("%s?key=%s&passphrase=%s", progressUrl, key, passphrase))
            .get()
            .build();

    final Response response = client.newCall(request).execute();

    if (response.code() == 200) {
      return gson.fromJson(response.body().string(), Progress.class);
    }

    log.error(response.body().string());
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

    log.error(response.body().string());
    ErrorResponse error = mapResponseToErrorResponse(response);
    throw new IllegalArgumentException(error.getMessage());
  }

  private Task mapResponseToTask(final Response response) throws IOException {
    return gson.fromJson(response.body().string(), Task.class);
  }

  private ErrorResponse mapResponseToErrorResponse(final Response response) throws IOException {
    return gson.fromJson(response.body().string(), ErrorResponse.class);
  }
}
