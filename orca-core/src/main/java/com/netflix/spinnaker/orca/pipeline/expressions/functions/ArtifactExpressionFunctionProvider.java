/*
 * Copyright 2019 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.netflix.spinnaker.orca.pipeline.expressions.functions;

import static java.lang.String.format;

import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import com.netflix.spinnaker.kork.artifacts.model.ExpectedArtifact;
import com.netflix.spinnaker.kork.expressions.ExpressionFunctionProvider;
import com.netflix.spinnaker.kork.expressions.SpelHelperFunctionException;
import com.netflix.spinnaker.orca.api.pipeline.models.PipelineExecution;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ArtifactExpressionFunctionProvider implements ExpressionFunctionProvider {
  @Nullable
  @Override
  public String getNamespace() {
    return null;
  }

  @NotNull
  @Override
  public Functions getFunctions() {
    return new Functions(
        new FunctionDefinition(
            "triggerResolvedArtifact",
            "Looks up a resolved artifact in the current execution trigger given its name. If multiple artifacts are found, only 1 will be returned.",
            new FunctionParameter(
                PipelineExecution.class, "execution", "The execution to search for artifacts"),
            new FunctionParameter(String.class, "name", "The name of the resolved artifact")),
        new FunctionDefinition(
            "triggerResolvedArtifactByType",
            "Looks up a resolved artifact in the current execution trigger given its type. If multiple artifacts are found, only 1 will be returned.",
            new FunctionParameter(
                PipelineExecution.class, "execution", "The execution to search for artifacts"),
            new FunctionParameter(String.class, "type", "The type of the resolved artifact")),
        new FunctionDefinition(
            "resolvedArtifacts",
            "Looks up resolved artifacts in the current execution.",
            new FunctionParameter(
                PipelineExecution.class, "execution", "The execution to search for artifacts")));
  }

  public static Artifact triggerResolvedArtifact(PipelineExecution execution, String name) {
    return triggerResolvedArtifactBy(execution, name, artifact -> name.equals(artifact.getName()));
  }

  public static Artifact triggerResolvedArtifactByType(PipelineExecution execution, String type) {
    return triggerResolvedArtifactBy(execution, type, artifact -> type.equals(artifact.getType()));
  }

  private static Artifact triggerResolvedArtifactBy(
      PipelineExecution execution, String nameOrType, Predicate<Artifact> predicate) {
    return execution.getTrigger().getResolvedExpectedArtifacts().stream()
        .filter(
            expectedArtifact ->
                expectedArtifact.getBoundArtifact() != null
                    && predicate.test(expectedArtifact.getBoundArtifact()))
        .findFirst()
        .map(ExpectedArtifact::getBoundArtifact)
        .orElseThrow(
            () ->
                new SpelHelperFunctionException(
                    format(
                        "Unable to locate resolved artifact %s in trigger execution %s.",
                        nameOrType, execution.getId())));
  }

  private static List<Artifact> resolvedArtifacts(PipelineExecution execution) {
    return execution.getStages().stream()
        .flatMap(
            stageExecution ->
                ((List<Artifact>)
                        stageExecution
                            .getOutputs()
                            .getOrDefault("artifacts", Collections.emptyList()))
                    .stream())
        .collect(Collectors.toList());
  }
}
