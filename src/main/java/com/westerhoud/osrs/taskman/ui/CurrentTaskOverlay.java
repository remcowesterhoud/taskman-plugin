package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.TaskmanConfig;
import com.westerhoud.osrs.taskman.domain.Task;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;

public class CurrentTaskOverlay extends OverlayPanel {

  private Task task;
  private final TaskmanConfig config;
  private final CurrentTaskComponent currentTaskComponent = new CurrentTaskComponent();

  @Inject
  public CurrentTaskOverlay(final TaskmanConfig config) {
    this.config = config;
  }

  public void setTask(final Task task) {
    this.task = task;
    currentTaskComponent.setTask(task);
  }

  @Override
  public Dimension render(final Graphics2D graphics) {
    if (!config.showOverlay() || task == null) {
      return null;
    }

    panelComponent.getChildren().add(currentTaskComponent);
    return super.render(graphics);
  }
}
