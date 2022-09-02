package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.TaskmanConfig;
import com.westerhoud.osrs.taskman.domain.Task;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.client.ui.overlay.OverlayPanel;

public class CurrentTaskOverlay extends OverlayPanel {

  @Setter private Task task;
  private final TaskmanConfig config;

  @Inject
  public CurrentTaskOverlay(final TaskmanConfig config) {
    this.config = config;
  }

  @Override
  public Dimension render(final Graphics2D graphics) {
    if (!config.showOverlay() || task == null) {
      return null;
    }
    panelComponent.getChildren().add(new CurrentTaskComponent(task));
    return super.render(graphics);
  }
}
