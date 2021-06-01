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

import static io.process.analytics.tools.bpmn.generator.model.ShapeType.ACTIVITY;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Shape {

    private final String id; // the bpmnElement id
    private final String name;
    private final ShapeType type;
    private final boolean isSplitGateway;

    public static Shape shape(String name) {
        return new Shape(name, name, ACTIVITY, false);
    }

    public static Shape shape(String id, String name) {
        return new Shape(id, name, ACTIVITY, false);
    }

    public static Shape shape(String id, String name, ShapeType type) {
        return new Shape(id, name, type, false);
    }
}
