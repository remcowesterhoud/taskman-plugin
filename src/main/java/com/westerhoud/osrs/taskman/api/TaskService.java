package com.westerhoud.osrs.taskman.api;

import com.westerhoud.osrs.taskman.domain.AccountCredentials;
import com.westerhoud.osrs.taskman.domain.AccountProgress;
import com.westerhoud.osrs.taskman.domain.Task;
import com.westerhoud.osrs.taskman.domain.TaskmanCommandData;
import java.io.IOException;

public interface TaskService {

  Task getCurrentTask(final AccountCredentials credentials, final String name) throws IOException;

  Task generateTask(final AccountCredentials credentials, final String name) throws IOException;

  Task completeTask(final AccountCredentials credentials, final String name) throws IOException;

  AccountProgress getAccountProgress(final AccountCredentials credentials, final String name)
      throws IOException;

  TaskmanCommandData getChatCommandData(String rsn) throws IOException;
}
