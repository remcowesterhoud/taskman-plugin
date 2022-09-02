package com.westerhoud.osrs.taskman.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountProgress {
  private Map<String, TierProgress> progressByTier;
  private String currentTier;
}
