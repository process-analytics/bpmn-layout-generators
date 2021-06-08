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

import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Direction.*;
import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Orientation.*;

import io.process.analytics.tools.bpmn.generator.converter.waypoint.WayPointDescriptor.WayPointDescriptorBuilder;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.ShapeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class WayPointsPositioner {

    private final GridSearcher gridSearcher;

    public WayPointDescriptor computeWaypointDescriptor(Position positionFrom, Position positionTo) {
        WayPointDescriptorBuilder builder = WayPointDescriptor.builder();

        if (positionFrom.getX() == positionTo.getX()) {
            builder.orientation(Vertical);
            if (positionFrom.getY() < positionTo.getY()) {
                builder.direction(TopToBottom);
            } else {
                builder.direction(BottomToTop);
            }
        } else if (positionFrom.getY() == positionTo.getY()) {
            builder.orientation(Horizontal);
            int positionFromX = positionFrom.getX();
            int positionToX = positionTo.getX();

            if (positionFromX < positionToX) {
                builder.direction(LeftToRight);
                if (positionFromX + 1 != positionToX) {
                    updateWhenExtraBendPointsRequired(builder, positionFrom, positionTo);
                }
            } else {
                builder.direction(RightToLeft);
                if (positionFromX - 1 != positionToX) {
                    updateWhenExtraBendPointsRequired(builder, positionFrom, positionTo);
                }
            }
        } else if (positionFrom.getX() < positionTo.getX()) {
            if (positionFrom.getY() < positionTo.getY()) {
                builder.direction(TopLeftToBottomRight);
                boolean shapeExistAtRightPositionFrom = gridSearcher.isShapeExistAtRight(positionFrom);
                Orientation orientation = shapeExistAtRightPositionFrom
                        || (isGatewayAt(positionFrom) && isGatewaySplitAt(positionFrom) && (!isGatewayAt(positionTo) || isGatewaySplitAt(positionTo)))
                        ? VerticalHorizontal
                        : HorizontalVertical;
                builder.orientation(orientation);
            } else {
                builder.direction(BottomLeftToTopRight);
                if (isGatewayAt(positionFrom)) {
                    Orientation orientation = !isGatewaySplitAt(positionFrom)
                            || isGatewayAt(positionTo) && !isGatewaySplitAt(positionTo)
                            ? HorizontalVertical
                            : VerticalHorizontal;
                    builder.orientation(orientation);
                } else {
                    boolean shapeExistAbovePositionFrom = gridSearcher.isShapeExistAbove(positionFrom);
                    Orientation orientation = shapeExistAbovePositionFrom || isGatewayAt(positionTo)
                            ? HorizontalVertical
                            : VerticalHorizontal;
                    builder.orientation(orientation);
                }
            }
        } else {
            if (positionFrom.getY() < positionTo.getY()) {
                builder.direction(TopRightToBottomLeft);
                boolean shapeExistAtLeftPositionFrom = gridSearcher.isShapeExistAtLeft(positionFrom);
                Orientation orientation = shapeExistAtLeftPositionFrom || isGatewaySplitAt(positionFrom)
                        ? VerticalHorizontal
                        : HorizontalVertical;
                builder.orientation(orientation);
            } else {
                builder.direction(BottomRightToTopLeft);
                boolean shapeExistAbovePositionFrom = gridSearcher.isShapeExistAbove(positionFrom);
                Orientation orientation = shapeExistAbovePositionFrom || isGatewayAt(positionTo)
                        ? HorizontalVertical
                        : VerticalHorizontal;
                builder.orientation(orientation);
            }
        }

        WayPointDescriptor wayPointDescriptor = builder.build();
        log.debug("Computed {}", wayPointDescriptor);
        return wayPointDescriptor;
    }

    private void updateWhenExtraBendPointsRequired(WayPointDescriptorBuilder builder, Position positionFrom,
            Position positionTo) {
        log.debug("Detected potential overlapping of horizontal edge on shapes. Shape: {}", positionFrom.getShape());

        BendConfiguration bendConfigurationBottom = gridSearcher.computeConfigurationToPassByEmptyRow(positionFrom, positionTo,
                BendDirection.BOTTOM);
        if (bendConfigurationBottom.offset == 0) {
            log.debug("Keep the horizontal orientation");
            return;
        }
        log.debug("Not possible to keep horizontal orientation");
        builder.orientation(VerticalHorizontalVertical);
        builder.bendConfiguration(bendConfigurationBottom);

        if (bendConfigurationBottom.offset == 1) {
            return;
        }

        BendConfiguration bendConfigurationTop = gridSearcher.computeConfigurationToPassByEmptyRow(positionFrom, positionTo,
                BendDirection.TOP);
        if (bendConfigurationTop.offset < bendConfigurationBottom.offset) {
            builder.bendConfiguration(bendConfigurationTop);
        }
    }

    private static boolean isGatewayAt(Position position) {
        return ShapeType.GATEWAY.equals(position.getShapeType());
    }

    private static boolean isGatewaySplitAt(Position position) {
        return isGatewayAt(position) && position.isSplitGateway();
    }

}
