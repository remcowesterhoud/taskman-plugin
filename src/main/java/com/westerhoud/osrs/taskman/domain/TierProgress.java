package com.westerhoud.osrs.taskman.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TierProgress {
  private int maxValue;
  private int value;
}
