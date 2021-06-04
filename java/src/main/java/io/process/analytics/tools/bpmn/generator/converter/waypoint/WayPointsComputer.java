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

import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayDimension;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayFlowNode;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayPoint;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.ShapeType;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class WayPointsComputer {

    private final List<DisplayFlowNode> flowNodes;
    private final GridSearcher gridSearcher;

    public WayPointsComputer(final Grid grid, final List<DisplayFlowNode> flowNodes) {
        this.flowNodes = flowNodes;
        gridSearcher = new GridSearcher(grid);
    }

    public List<DisplayPoint> inferWayPoints(Edge edge) {
        Position positionFrom = gridSearcher.getPositionOfShape(edge.getFrom());
        Position positionTo = gridSearcher.getPositionOfShape(edge.getTo());

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
        switch (edgeDirection) {
            case HorizontalLeftToRight:
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x, dimensionTo.y + dimensionTo.height / 2));
                break;
            case HorizontalRightToLeft:
                wayPoints.add(new DisplayPoint(dimensionFrom.x, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(
                        new DisplayPoint(dimensionTo.x + dimensionTo.width, dimensionTo.y + dimensionTo.height / 2));
                break;
            case BottomLeftToTopRight_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(
                        new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height));
                break;
            case BottomLeftToTopRight_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x, dimensionTo.y + dimensionTo.height / 2));
                break;
            case BottomRightToTopLeft_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(
                        new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height));
                break;
            case BottomRightToTopLeft_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionTo.y + dimensionTo.height / 2));
                break;
            case TopLeftToBottomRight_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y));
                break;
            case TopLeftToBottomRight_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionFrom.y + dimensionTo.height));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x, dimensionTo.y + dimensionTo.height / 2));
                break;
            case TopRightToBottomLeft_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y));
                break;
            case TopRightToBottomLeft_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionFrom.y + dimensionTo.height));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionTo.y + dimensionTo.height / 2));
                break;
            case VerticalBottomToTop:
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y));
                wayPoints.add(
                        new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height));
                break;
            case VerticalTopToBottom:
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2,
                        dimensionFrom.y + dimensionFrom.height));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y));
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
        EdgeDirection edgeDirection = null;
        if (positionFrom.getX() == positionTo.getX()) {
            if (positionFrom.getY() < positionTo.getY()) {
                edgeDirection = EdgeDirection.VerticalTopToBottom;
            } else {
                edgeDirection = EdgeDirection.VerticalBottomToTop;
            }
        } else if (positionFrom.getY() == positionTo.getY()) {
            if (positionFrom.getX() < positionTo.getX()) {
                edgeDirection = EdgeDirection.HorizontalLeftToRight;
            } else {
                edgeDirection = EdgeDirection.HorizontalRightToLeft;
            }
        } else if (positionFrom.getX() < positionTo.getX()) {
            if (positionFrom.getY() < positionTo.getY()) {
                boolean shapeExistAtRightPositionFrom = gridSearcher.isShapeExistAtRight(positionFrom);
                edgeDirection = shapeExistAtRightPositionFrom
                        || (isGatewayAt(positionFrom) && (!isGatewayAt(positionTo) || isGatewaySplitAt(positionTo)))
                                ? EdgeDirection.TopLeftToBottomRight_FirstVertical
                                : EdgeDirection.TopLeftToBottomRight_FirstHorizontal;
            } else {
                if (isGatewayAt(positionFrom)) {
                    edgeDirection = !isGatewaySplitAt(positionFrom)
                            || isGatewayAt(positionTo) && !isGatewaySplitAt(positionTo)
                                    ? EdgeDirection.BottomLeftToTopRight_FirstHorizontal
                                    : EdgeDirection.BottomLeftToTopRight_FirstVertical;
                } else {
                    boolean shapeExistAbovePositionFrom = gridSearcher.isShapeExistAbove(positionFrom);
                    edgeDirection = shapeExistAbovePositionFrom || isGatewayAt(positionTo)
                            ? EdgeDirection.BottomLeftToTopRight_FirstHorizontal
                            : EdgeDirection.BottomLeftToTopRight_FirstVertical;
                }
            }
        } else {
            if (positionFrom.getY() < positionTo.getY()) {
                boolean shapeExistAtLeftPositionFrom = gridSearcher.isShapeExistAtLeft(positionFrom);
                edgeDirection = shapeExistAtLeftPositionFrom ? EdgeDirection.TopRightToBottomLeft_FirstVertical
                        : EdgeDirection.TopRightToBottomLeft_FirstHorizontal;
            } else {
                boolean shapeExistAbovePositionFrom = gridSearcher.isShapeExistAbove(positionFrom);
                edgeDirection = shapeExistAbovePositionFrom ? EdgeDirection.BottomRightToTopLeft_FirstHorizontal
                        : EdgeDirection.BottomRightToTopLeft_FirstVertical;
            }
        }

        return edgeDirection;
    }

    private static boolean isGatewayAt(Position position) {
        return ShapeType.GATEWAY.equals(position.getShapeType());
    }

    private static boolean isGatewaySplitAt(Position position) {
        return isGatewayAt(position) && position.isSplitGateway();
    }

    // visible for testing
    static enum EdgeDirection {
        HorizontalLeftToRight, HorizontalRightToLeft, VerticalBottomToTop, VerticalTopToBottom, BottomLeftToTopRight_FirstHorizontal, BottomLeftToTopRight_FirstVertical, BottomRightToTopLeft_FirstHorizontal, BottomRightToTopLeft_FirstVertical, TopLeftToBottomRight_FirstHorizontal, TopLeftToBottomRight_FirstVertical, TopRightToBottomLeft_FirstHorizontal, TopRightToBottomLeft_FirstVertical,
    }

}
