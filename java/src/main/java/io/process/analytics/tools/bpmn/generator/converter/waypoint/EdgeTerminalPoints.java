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
package io.process.analytics.tools.bpmn.generator.converter.waypoint;

import io.process.analytics.tools.bpmn.generator.model.display.DisplayDimension;
import io.process.analytics.tools.bpmn.generator.model.display.DisplayPoint;

/**
 * Compute coordinates of the terminal points (source or target) of an Edge.
 */
public class EdgeTerminalPoints {

    public DisplayPoint rightMiddle(DisplayDimension dimension) {
        return new DisplayPoint(dimension.x + dimension.width, dimension.y + dimension.height / 2);
    }

    public DisplayPoint leftMiddle(DisplayDimension dimension) {
        return new DisplayPoint(dimension.x, dimension.y + dimension.height / 2);
    }

    public DisplayPoint centerBottom(DisplayDimension dimension) {
        return new DisplayPoint(dimension.x + dimension.width / 2, dimension.y + dimension.height);
    }

    public DisplayPoint centerTop(DisplayDimension dimension) {
        return new DisplayPoint(dimension.x + dimension.width / 2, dimension.y);
    }

}
