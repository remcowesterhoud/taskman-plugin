package com.westerhoud.osrs.taskman.domain;

import java.awt.image.BufferedImage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class Task {
  private String name;
  private String imageUrl;
  private transient BufferedImage image;

  public String getImageUrl() {
    return imageUrl.startsWith("http://") ? imageUrl.replace("http://", "https://") : imageUrl;
  }

  public BufferedImage getImage() {
    return image;
  }
}
