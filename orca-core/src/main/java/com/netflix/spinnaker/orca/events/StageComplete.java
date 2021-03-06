/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.events;

import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import javax.annotation.Nonnull;
import lombok.Getter;

public final class StageComplete extends ExecutionEvent {
  @Getter private final StageExecution stage;

  public StageComplete(@Nonnull Object source, @Nonnull StageExecution stage) {
    super(source, stage.getExecution().getType(), stage.getExecution().getId());

    this.stage = stage;
  }

  public @Nonnull String getStageId() {
    return stage.getId();
  }

  public @Nonnull String getStageType() {
    return stage.getType();
  }

  public @Nonnull ExecutionStatus getStatus() {
    return stage.getStatus();
  }
}
