/*
 * Copyright 2021 Bonitasoft S.A.
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
package io.process.analytics.tools.bpmn.generator.converter.waypoint;

import static io.process.analytics.tools.bpmn.generator.converter.Configuration.CELL_HEIGHT;
import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Orientation.*;

import java.util.ArrayList;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.model.display.DisplayDimension;
import io.process.analytics.tools.bpmn.generator.model.display.DisplayPoint;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WayPointsConverter {

    private final EdgeTerminalPoints edgeTerminalPoints = new EdgeTerminalPoints();

    public List<DisplayPoint> toDisplayPoints(WayPointDescriptor wayPointDescriptor, DisplayDimension dimensionFrom,
                                              DisplayDimension dimensionTo) {
        List<DisplayPoint> wayPoints = new ArrayList<>();

        Orientation orientation = wayPointDescriptor.orientation;
        switch (wayPointDescriptor.direction) {
            case LeftToRight:
                if (orientation == Horizontal) {
                    wayPoints.add(edgeTerminalPoints.rightMiddle(dimensionFrom));
                    wayPoints.add(edgeTerminalPoints.leftMiddle(dimensionTo));
                }
                else if (orientation == VerticalHorizontalVertical) {
                    log.debug("Special case LeftToRight VerticalHorizontalVertical");
                    wayPoints = computeWaypointsWithBendPoint(wayPointDescriptor.bendConfiguration, dimensionFrom, dimensionTo);
                }
                break;
            case RightToLeft:
                if (orientation == Horizontal) {
                    wayPoints.add(edgeTerminalPoints.leftMiddle(dimensionFrom));
                    wayPoints.add(edgeTerminalPoints.rightMiddle(dimensionTo));
                }
                else if (orientation == VerticalHorizontalVertical) {
                    log.debug("Special case RightToLeft VerticalHorizontalVertical");
                    wayPoints = computeWaypointsWithBendPoint(wayPointDescriptor.bendConfiguration, dimensionFrom, dimensionTo);
                }
                break;
            case BottomLeftToTopRight:
                if (orientation == HorizontalVertical) {
                    DisplayPoint from = edgeTerminalPoints.rightMiddle(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.centerBottom(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(to.x, from.y));
                    wayPoints.add(to);
                } else if (orientation == VerticalHorizontal) {
                    DisplayPoint from = edgeTerminalPoints.centerTop(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.leftMiddle(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(from.x, to.y));
                    wayPoints.add(to);
                }
                break;
            case BottomRightToTopLeft:
                if (orientation == HorizontalVertical) {
                    DisplayPoint from = edgeTerminalPoints.leftMiddle(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.centerBottom(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(to.x, from.y));
                    wayPoints.add(to);
                } else if (orientation == VerticalHorizontal) {
                    DisplayPoint from = edgeTerminalPoints.centerTop(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.rightMiddle(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(from.x, to.y));
                    wayPoints.add(to);
                }
                break;
            case TopLeftToBottomRight:
                if (orientation == HorizontalVertical) {
                    DisplayPoint from = edgeTerminalPoints.rightMiddle(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.centerTop(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(to.x, from.y));
                    wayPoints.add(to);
                } else if (orientation == VerticalHorizontal) {
                    DisplayPoint from = edgeTerminalPoints.centerBottom(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.leftMiddle(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(from.x, to.y));
                    wayPoints.add(to);
                }
                break;
            case TopRightToBottomLeft:
                if (orientation == HorizontalVertical) {
                    DisplayPoint from = edgeTerminalPoints.leftMiddle(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.centerTop(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(to.x, from.y));
                    wayPoints.add(to);
                } else if (orientation == VerticalHorizontal) {
                    DisplayPoint from = edgeTerminalPoints.centerBottom(dimensionFrom);
                    DisplayPoint to = edgeTerminalPoints.rightMiddle(dimensionTo);
                    wayPoints.add(from);
                    wayPoints.add(new DisplayPoint(from.x, to.y));
                    wayPoints.add(to);
                }
                break;
            case BottomToTop:
                if (orientation == Vertical) {
                    wayPoints.add(edgeTerminalPoints.centerTop(dimensionFrom));
                    wayPoints.add(edgeTerminalPoints.centerBottom(dimensionTo));
                }
                break;
            case TopToBottom:
                if (orientation == Vertical) {
                    wayPoints.add(edgeTerminalPoints.centerBottom(dimensionFrom));
                    wayPoints.add(edgeTerminalPoints.centerTop(dimensionTo));
                }
                break;
            default:
                // do nothing
        }
        return wayPoints;
    }

    // Special case add bend points to avoid edge overlapping on shape
    private List<DisplayPoint> computeWaypointsWithBendPoint(BendConfiguration bendConfiguration,
                                                                    DisplayDimension dimensionFrom, DisplayDimension dimensionTo) {
        log.debug("Bend configuration: {}", bendConfiguration);

        DisplayPoint from = bendConfiguration.direction == BendDirection.BOTTOM ? edgeTerminalPoints.centerBottom(dimensionFrom): edgeTerminalPoints.centerTop(dimensionFrom);
        DisplayPoint to = bendConfiguration.direction == BendDirection.BOTTOM ? edgeTerminalPoints.centerBottom(dimensionTo): edgeTerminalPoints.centerTop(dimensionTo);
        int bendPointY = from.y + bendConfiguration.direction.numericFactor() * bendConfiguration.offset * CELL_HEIGHT;

        List<DisplayPoint> wayPoints = new ArrayList<>();
        wayPoints.add(from);
        wayPoints.add(new DisplayPoint(from.x, bendPointY));
        wayPoints.add(new DisplayPoint(to.x, bendPointY));
        wayPoints.add(to);
        return wayPoints;
    }

}
