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

import static io.process.analytics.tools.bpmn.generator.converter.Configuration.CELL_HEIGHT;
import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Direction.*;
import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Orientation.*;

import java.util.ArrayList;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayDimension;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayFlowNode;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayPoint;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.ShapeType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WayPointsComputer {

    private final List<DisplayFlowNode> flowNodes;
    private final GridSearcher gridSearcher;

    public WayPointsComputer(final Grid grid, final List<DisplayFlowNode> flowNodes) {
        this.flowNodes = flowNodes;
        gridSearcher = new GridSearcher(grid);
    }

    public List<DisplayPoint> inferWayPoints(Edge edge) {
        log.debug("Inferring waypoints of edge {}", edge);
        Position positionFrom = gridSearcher.getPositionFrom(edge);
        Position positionTo = gridSearcher.getPositionTo(edge);

        EdgeDirection edgeDirection = computeEdgeDirection(positionFrom, positionTo);
        DisplayFlowNode flowNodeFrom = getFlowNode(flowNodes, positionFrom.getShape());
        DisplayFlowNode flowNodeTo = getFlowNode(flowNodes, positionTo.getShape());

        return computeWayPoints(edgeDirection, flowNodeFrom, flowNodeTo);
    }

    private static List<DisplayPoint> computeWayPoints(EdgeDirection edgeDirection, DisplayFlowNode flowNodeFrom,
                                                       DisplayFlowNode flowNodeTo) {
        DisplayDimension dimensionFrom = flowNodeFrom.dimension;
        DisplayDimension dimensionTo = flowNodeTo.dimension;

        List<DisplayPoint> wayPoints = new ArrayList<>();

        Orientation orientation = edgeDirection.orientation;
        EdgeTerminalPoints edgeTerminalPoints = new EdgeTerminalPoints();
        switch (edgeDirection.direction) {
            case LeftToRight:
                if (orientation == Horizontal) {
                    wayPoints.add(edgeTerminalPoints.rightMiddle(dimensionFrom));
                    wayPoints.add(edgeTerminalPoints.leftMiddle(dimensionTo));
                }
                break;
            case RightToLeft:
                if (orientation == Horizontal) {
                    wayPoints.add(edgeTerminalPoints.leftMiddle(dimensionFrom));
                    wayPoints.add(edgeTerminalPoints.rightMiddle(dimensionTo));
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
                if (orientation == HorizontalVertical) { // TODO duplication with BottomLeftToTopRight HorizontalVertical
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

    private static DisplayFlowNode getFlowNode(List<DisplayFlowNode> flowNodes, String flowNodeId) {
        return flowNodes.stream()
                .filter(f -> flowNodeId.equals(f.bpmnElementId))
                .findFirst()
                .get(); // always exist, otherwise error occur on flow node generation
    }

    // visible for testing
    EdgeDirection computeEdgeDirection(Position positionFrom, Position positionTo) {
        Direction direction;
        Orientation orientation;

        if (positionFrom.getX() == positionTo.getX()) {
            orientation = Orientation.Vertical;
            if (positionFrom.getY() < positionTo.getY()) {
                direction = TopToBottom;
            } else {
                direction = BottomToTop;
            }
        } else if (positionFrom.getY() == positionTo.getY()) {
            orientation = Horizontal;
            int positionFromX = positionFrom.getX();
            int positionToX = positionTo.getX();
            if (positionFromX < positionToX) {
                direction = LeftToRight;
            } else {
                direction = RightToLeft;
            }
        } else if (positionFrom.getX() < positionTo.getX()) {
            if (positionFrom.getY() < positionTo.getY()) {
                direction = TopLeftToBottomRight;
                boolean shapeExistAtRightPositionFrom = gridSearcher.isShapeExistAtRight(positionFrom);
                orientation = shapeExistAtRightPositionFrom
                        || (isGatewayAt(positionFrom) && (!isGatewayAt(positionTo) || isGatewaySplitAt(positionTo)))
                        ? Orientation.VerticalHorizontal
                        : Orientation.HorizontalVertical;
            } else {
                direction = BottomLeftToTopRight;
                if (isGatewayAt(positionFrom)) {
                    orientation = !isGatewaySplitAt(positionFrom)
                            || isGatewayAt(positionTo) && !isGatewaySplitAt(positionTo)
                            ? Orientation.HorizontalVertical
                            : Orientation.VerticalHorizontal;
                } else {
                    boolean shapeExistAbovePositionFrom = gridSearcher.isShapeExistAbove(positionFrom);
                    orientation = shapeExistAbovePositionFrom || isGatewayAt(positionTo)
                            ? Orientation.HorizontalVertical
                            : Orientation.VerticalHorizontal;
                }
            }
        } else {
            if (positionFrom.getY() < positionTo.getY()) {
                direction = TopRightToBottomLeft;
                boolean shapeExistAtLeftPositionFrom = gridSearcher.isShapeExistAtLeft(positionFrom);
                orientation = shapeExistAtLeftPositionFrom || isGatewaySplitAt(positionFrom)
                        ? Orientation.VerticalHorizontal
                        : Orientation.HorizontalVertical;
            } else {
                direction = BottomRightToTopLeft;
                boolean shapeExistAbovePositionFrom = gridSearcher.isShapeExistAbove(positionFrom);
                orientation = shapeExistAbovePositionFrom || isGatewayAt(positionTo)
                        ? Orientation.HorizontalVertical
                        : Orientation.VerticalHorizontal;
            }
        }

        log.debug("Computed Edge orientation. Direction: {}, Orientation: {}", direction, orientation);
        return EdgeDirection.builder().direction(direction).orientation(orientation).build();
    }

    private static boolean isGatewayAt(Position position) {
        return ShapeType.GATEWAY.equals(position.getShapeType());
    }

    private static boolean isGatewaySplitAt(Position position) {
        return isGatewayAt(position) && position.isSplitGateway();
    }

}
