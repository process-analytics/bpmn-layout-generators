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

import java.util.List;
import java.util.stream.Collectors;

import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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


    public BendConfiguration searchEmptyRow(Position positionFrom, Position positionTo, BendDirection searchDirection) {
        int positionFromY = positionFrom.getY();
        int offset = -1;
        boolean isElementBetween = true;

        log.debug("Search empty rows in direction {}", searchDirection);
        while (isElementBetween) {
            offset++;
            isElementBetween = this.hasElementsBetweenPositionsHorizontally(
                    positionFromY + searchDirection.numericFactor() * offset,
                    positionFrom.getX(), positionTo.getX());
        }
        log.debug("Found empty row at offset {}", offset);
        return BendConfiguration.builder().direction(searchDirection).offset(offset).build();
    }

    private boolean hasElementsBetweenPositionsHorizontally(final int y, final int x1, final int x2) {
        log.debug("Searching for elements horizontally between positions. y={} x1={} x2={}", y, x1, x2);
        return grid.getPositions()
                .stream()
                .filter(p -> p.getY() == y)
                .filter(p -> p.getX() > Math.min(x1, x2))
                .anyMatch(p -> p.getX() < Math.max(x1, x2));
    }

}
