/*
 * Copyright 2024 Bonitasoft S.A.
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

package io.process.analytics.tools.bpmn.generator.export;

import io.process.analytics.tools.bpmn.generator.converter.waypoint.Direction;
import io.process.analytics.tools.bpmn.generator.model.display.DisplayPoint;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SVGExporterTest {

    @ParameterizedTest
    @MethodSource("provideWaypointsForDirectionDetection")
    void detect_last_segment_direction(List<DisplayPoint> waypoints, Direction expectedDirection) {
        var direction = SVGExporter.detectLastSegmentDirection(waypoints);
        assertThat(direction).isEqualTo(expectedDirection);
    }

    private static Stream<Arguments> provideWaypointsForDirectionDetection() {
        return Stream.of(
                Arguments.of(List.of(new DisplayPoint(0, 37), new DisplayPoint(10, 37)), Direction.LeftToRight),
                Arguments.of(List.of(new DisplayPoint(40, 58), new DisplayPoint(10, 58)), Direction.RightToLeft),
                Arguments.of(List.of(new DisplayPoint(13, 73), new DisplayPoint(13, 137)), Direction.TopToBottom),
                Arguments.of(List.of(new DisplayPoint(33, 73), new DisplayPoint(33, 37)), Direction.BottomToTop)
        );
    }

}

