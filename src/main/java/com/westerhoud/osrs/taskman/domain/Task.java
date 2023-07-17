package com.westerhoud.osrs.taskman.domain;

import java.awt.Image;
import java.awt.image.BufferedImage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.ImageUtil;

@Data
@NoArgsConstructor
@Slf4j
public class Task {

  private static final BufferedImage ERROR_IMAGE =
      ImageUtil.loadImageResource(Task.class, "error.png");

  private String name;
  private String imageUrl;
  private transient BufferedImage image;

  public String getImageUrl() {
    return imageUrl.startsWith("http://") ? imageUrl.replace("http://", "https://") : imageUrl;
  }

  public Image getResizedImage(final int width, final int height) {
    if (image != null) {
      return image.getScaledInstance(width, height, Image.SCALE_FAST);
    } else {
      return ERROR_IMAGE.getScaledInstance(width, height, Image.SCALE_FAST);
    }
  }
}
