package com.westerhoud.osrs.taskman.service;

import com.google.gson.Gson;
import com.westerhoud.osrs.taskman.domain.AccountCredentials;
import com.westerhoud.osrs.taskman.domain.AccountProgress;
import com.westerhoud.osrs.taskman.domain.ErrorResponse;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.domain.TaskmanCommandData;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class TaskService implements com.westerhoud.osrs.taskman.api.TaskService {

  public static final String TASKMAN_IDENTIFIER_HEADER = "x-taskman-identifier";
  public static final String TASKMAN_PASSWORD_HEADER = "x-taskman-password";
  public static final String TASKMAN_SOURCE_HEADER = "x-taskman-source";
  public static final String TASKMAN_RSN_HEADER = "x-taskman-rsn";
  private static final String BASE_URL = "https://taskman.up.railway.app/task";
  private final OkHttpClient client;
  private final Gson gson;
  private final String currentUrl;
  private final String generateUrl;
  private final String completeUrl;
  private final String progressUrl;
  private final String commandUrl;

  public TaskService(final OkHttpClient okHttpClient, final Gson gson) {
    client = okHttpClient;
    this.gson = gson;
    currentUrl = BASE_URL + "/current";
    generateUrl = BASE_URL + "/generate";
    completeUrl = BASE_URL + "/complete";
    progressUrl = BASE_URL + "/progress";
    commandUrl = BASE_URL + "/command/%s";
  }

  @Override
  public Task getCurrentTask(final AccountCredentials credentials, final String rsn)
      throws IOException {
    if (!credentials.isValid()) {
      throw new IllegalArgumentException(
          "Please configure your credentials in the plugin configurations");
    }

    final Request request =
        new Request.Builder()
            .url(currentUrl)
            .addHeader(TASKMAN_IDENTIFIER_HEADER, credentials.getIdentifier())
            .addHeader(TASKMAN_PASSWORD_HEADER, credentials.getPassword())
            .addHeader(TASKMAN_SOURCE_HEADER, credentials.getSource().name())
            .addHeader(TASKMAN_RSN_HEADER, rsn)
            .get()
            .build();

    return executeRequest(request);
  }

  @Override
  public Task generateTask(final AccountCredentials credentials, final String rsn)
      throws IOException {
    final Request request =
        new Request.Builder()
            .url(generateUrl)
            .header("Content-Type", "application/json")
            .addHeader(TASKMAN_SOURCE_HEADER, credentials.getSource().name())
            .addHeader(TASKMAN_RSN_HEADER, rsn)
            .post(getRequestBody(credentials))
            .build();
    return executeRequest(request);
  }

  @Override
  public Task completeTask(final AccountCredentials credentials, final String rsn)
      throws IOException {
    final Request request =
        new Request.Builder()
            .url(completeUrl)
            .header("Content-Type", "application/json")
            .addHeader(TASKMAN_SOURCE_HEADER, credentials.getSource().name())
            .addHeader(TASKMAN_RSN_HEADER, rsn)
            .post(getRequestBody(credentials))
            .build();
    return executeRequest(request);
  }

  @Override
  public AccountProgress getAccountProgress(final AccountCredentials credentials, final String rsn)
      throws IOException {
    if (!credentials.isValid()) {
      throw new IllegalArgumentException(
          "Please set your username / spreadsheet key in the plugin configurations");
    }

    final Request request =
        new Request.Builder()
            .url(progressUrl)
            .addHeader(TASKMAN_IDENTIFIER_HEADER, credentials.getIdentifier())
            .addHeader(TASKMAN_PASSWORD_HEADER, credentials.getPassword())
            .addHeader(TASKMAN_SOURCE_HEADER, credentials.getSource().name())
            .addHeader(TASKMAN_RSN_HEADER, rsn)
            .get()
            .build();

    final Response response = client.newCall(request).execute();

    if (response.code() == 200) {
      return gson.fromJson(response.body().string(), AccountProgress.class);
    }

    final ErrorResponse error = mapResponseToErrorResponse(response);
    throw new IllegalArgumentException(error.getMessage());
  }

  @Override
  public TaskmanCommandData getChatCommandData(final String rsn) throws IOException {
    final Request request = new Builder().url(String.format(commandUrl, rsn)).get().build();

    final Response response = client.newCall(request).execute();

    if (response.code() == 200) {
      return gson.fromJson(response.body().string(), TaskmanCommandData.class);
    } else {
      throw new IllegalArgumentException("Could not get task command data for rsn: " + rsn);
    }
  }

  private RequestBody getRequestBody(final AccountCredentials credentials) {
    return RequestBody.create(MediaType.parse("application/json"), gson.toJson(credentials));
  }

  private Task executeRequest(final Request request) throws IOException {
    final Response response = client.newCall(request).execute();

    if (response.code() == 200) {
      return mapResponseToTask(response);
    }

    final ErrorResponse error = mapResponseToErrorResponse(response);
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
