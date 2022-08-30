package com.westerhoud.osrs.taskman.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
  private Map<String, TierProgress> progressByTier;
  private String currentTier;
}
