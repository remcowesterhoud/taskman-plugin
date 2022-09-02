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
@Setter
public class CurrentTaskComponent implements LayoutableRenderableEntity {

  public static final int BORDER_SIZE = 15;
  private final Task task;
  private final BufferedImage background =
      ImageUtil.loadImageResource(getClass(), "empty_dark.png");

  @Getter private final Rectangle bounds = new Rectangle();
  private Point preferredLocation = new Point();

  @Override
  public void setPreferredSize(final Dimension dimension) {}

  @Override
  public Dimension render(final Graphics2D graphics) {
    graphics.drawImage(background, preferredLocation.x, preferredLocation.y, null);

    final int taskIconSize = background.getHeight() - (BORDER_SIZE * 2);
    final int taskIconX = background.getWidth() - BORDER_SIZE - taskIconSize;

    final Image resizedTaskIcon = resizeImage(task.getImage(), taskIconSize, taskIconSize);
    graphics.drawImage(resizedTaskIcon, taskIconX, BORDER_SIZE, null);

    final Dimension dimension = new Dimension(background.getWidth(), background.getHeight());
    bounds.setLocation(preferredLocation);
    bounds.setSize(dimension);
    return dimension;
  }

  private Image resizeImage(final BufferedImage image, final int width, final int height) {
    return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
  }
}
