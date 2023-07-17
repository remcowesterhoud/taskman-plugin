package com.westerhoud.osrs.taskman.ui;

import com.westerhoud.osrs.taskman.domain.Task;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.util.ImageUtil;

@RequiredArgsConstructor
public class CurrentTaskComponent implements LayoutableRenderableEntity {

  public static final int BORDER_SIZE = 15;
  private Task task;
  private final boolean taskChanged = false;
  private final BufferedImage background =
      ImageUtil.loadImageResource(getClass(), "empty_dark.png");

  @Getter private final Rectangle bounds = new Rectangle();
  @Setter private Point preferredLocation = new Point();

  private Image resizedTaskIcon;
  private int taskIconX;

  public void setTask(final Task task) {
    this.task = task;

    final int taskIconSize = background.getHeight() - (BORDER_SIZE * 2);
    taskIconX = background.getWidth() - BORDER_SIZE - taskIconSize;
    resizedTaskIcon = task.getResizedImage(taskIconSize, taskIconSize);
  }

  @Override
  public void setPreferredSize(final Dimension dimension) {}

  @Override
  public Dimension render(final Graphics2D graphics) {
    graphics.drawImage(background, preferredLocation.x, preferredLocation.y, null);
    graphics.drawImage(resizedTaskIcon, taskIconX, BORDER_SIZE, null);

    final Dimension dimension = new Dimension(background.getWidth(), background.getHeight());
    bounds.setLocation(preferredLocation);
    bounds.setSize(dimension);
    return dimension;
  }
}
