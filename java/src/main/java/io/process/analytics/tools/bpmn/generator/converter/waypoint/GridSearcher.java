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

import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class GridSearcher {

    private final Grid grid;

    public Position getPositionFrom(Edge edge) {
        return getPosition(edge, true);
    }

    public Position getPositionTo(Edge edge) {
        return getPosition(edge, false);
    }

    private Position getPosition(Edge edge, boolean isFrom) {
        String shapeId = isFrom && !edge.isReverted() || (!isFrom && edge.isReverted()) ? edge.getFrom() : edge.getTo();
        return getPositionOfShape(shapeId);
    }

    private Position getPositionOfShape(String shapeId) {
        return grid.getPositions()
                .stream()
                .filter(p -> shapeId.equals(p.getShape()))
                .findFirst()
                .get(); // always exist, otherwise error occur on flow node generation
    }

    public boolean isShapeExistAtLeft(Position position) {
        return grid.getPositions()
                .stream()
                .filter(p -> p.getY() == position.getY())
                .anyMatch(p -> p.getX() == position.getX() - 1);
    }

    public boolean isShapeExistAtRight(final Position position) {
        return grid.getPositions()
                .stream()
                .filter(p -> p.getY() == position.getY())
                .anyMatch(p -> p.getX() == position.getX() + 1);
    }

    public boolean isShapeExistAbove(final Position positionFrom) {
        return grid.getPositions()
                .stream()
                .filter(p -> p.getX() == positionFrom.getX())
                .anyMatch(p -> p.getY() == positionFrom.getY() - 1);
    }

}
