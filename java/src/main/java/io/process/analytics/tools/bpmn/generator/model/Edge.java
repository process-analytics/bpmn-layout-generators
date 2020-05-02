/*
 * Copyright 2020 Bonitasoft S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.process.analytics.tools.bpmn.generator.model;

import lombok.Data;

@Data
public class Edge {

    private final String from;
    private final String to;
    /**
     * This property helps to break cycles
     *
     * `true` when the original edge is in the opposite direction
     *
     * When drown, it should be drown in the opposite direction.
     * When positioning shapes, it should be kept reverted
     */
    private final boolean reverted;

    public static Edge edge(Shape from, Shape to) {
        return new Edge(from.getId(), to.getId(), false);
    }
    public static Edge edge(String from, String to) {
        return new Edge(from, to, false);
    }
    public static Edge revertedEdge(String from, String to) {
        return new Edge(from, to, true);
    }

}
