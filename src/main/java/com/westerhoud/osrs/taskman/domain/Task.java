package com.westerhoud.osrs.taskman.domain;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.ImageUtil;

@Data
@NoArgsConstructor
@Slf4j
public class Task {
  private String name;
  private String imageUrl;

  public String getImageUrl() {
    return imageUrl.startsWith("http://") ? imageUrl.replace("http://", "https://") : imageUrl;
  }

  public BufferedImage getImage() {
    BufferedImage image = null;
    try {
      final URL imageUrl = new URL(getImageUrl());
      image = ImageIO.read(imageUrl);
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }

    if (image == null) {
      log.info(getImageUrl());
      image = ImageUtil.loadImageResource(getClass(), "error.png");
    }

    return image;
  }
}
