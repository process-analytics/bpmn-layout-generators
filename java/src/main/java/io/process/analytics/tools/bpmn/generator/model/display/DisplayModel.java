/*
 * Copyright 2021 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.process.analytics.tools.bpmn.generator.model.display;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

@RequiredArgsConstructor
@Builder
public class DisplayModel {
    public final int width;
    public final int height;

    @Singular
    public final List<DisplayFlowNode> flowNodes;
    @Singular
    public final List<DisplayEdge> edges;

}