package com.westerhoud.osrs.taskman.api;

import com.westerhoud.osrs.taskman.domain.AccountCredentials;
import com.westerhoud.osrs.taskman.domain.AccountProgress;
import com.westerhoud.osrs.taskman.domain.Task;

import java.io.IOException;

public interface TaskService {

  Task getCurrentTask(final String identifier) throws IOException;

  Task generateTask(final AccountCredentials credentials) throws IOException;

  Task completeTask(final AccountCredentials credentials) throws IOException;

  AccountProgress getAccountProgress(final String identifier) throws IOException;
}
