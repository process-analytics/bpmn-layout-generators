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
package io.process.analytics.tools.bpmn.generator.converter;

import java.util.ArrayList;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.model.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;

public class AlgoToDisplayModelConverter {

    // TODO this should be fields of the class and configured by the client code
    private static final int CELL_WIDTH = 200;
    private static final int CELL_HEIGHT = 100;

    public DisplayModel convert(Grid grid, Diagram diagram) {
        DisplayModel.DisplayModelBuilder model = DisplayModel.builder();
        model.width(grid.width() * CELL_WIDTH).height(grid.height() * CELL_HEIGHT);

        for (Position position : grid.getPositions()) {
            int xOffset = position.getX() * CELL_WIDTH;
            int yOffset = position.getY() * CELL_HEIGHT;
            int nodeWidth = x(60);
            int nodeHeight = y(60);

            // TODO manage when not found (should not occur)
            Shape shape = diagram.getShapes().stream()
                    .filter(s -> s.getId().equals(position.getShape()))
                    .findFirst().get();
            String name = shape.getName();

            // ensure to have a square shape (i.e. same width and height) for non activity elements
            ShapeType shapeType = shape.getType();
            if (shapeType == ShapeType.EVENT || shapeType == ShapeType.GATEWAY) {
                int nodeDimension = Math.min(nodeWidth, nodeHeight);
                if (shapeType == ShapeType.EVENT) {
                    nodeDimension /= 2;
                }
                nodeWidth = nodeDimension;
                nodeHeight = nodeDimension;
            }

            int x = xOffset + (CELL_WIDTH - nodeWidth) / 2;
            int y = yOffset + (CELL_HEIGHT - nodeHeight) / 2;
            DisplayDimension flowNodeDimension = new DisplayDimension(x, y, nodeWidth, nodeHeight);

            // Labels positions better work with the SVG export
            // BPMN label positions are adjusted in BPMNDiagramRichBuilder
            int labelX = xOffset + x(50);
            int labelY = yOffset + y(50);
            if (shapeType == ShapeType.EVENT) { // put the label under the shape
                labelY = (int) (y + nodeHeight * 1.5);
            } else if (shapeType == ShapeType.GATEWAY) { // put the label on the top left of the shape
                labelX = (int) (x - nodeWidth * 0.5);
                labelY = (int) (y - nodeHeight * 0.5);
            }

            DisplayDimension labelDimension = new DisplayDimension(labelX, labelY, nodeWidth, nodeHeight);
            DisplayLabel label = new DisplayLabel(name, y(16), labelDimension);

            model.flowNode(DisplayFlowNode.builder().bpmnElementId(shape.getId())
                    .dimension(flowNodeDimension)
                    .label(label)
                    .type(shapeType)
                    .rx(y(10)).strokeWidth(y(5)).build());
        }

        diagram.getEdges()
                .stream()
                .map(edge -> new DisplayEdge(edge.getId(), inferWayPoints(edge, grid, model.flowNodes)))
                .forEach(model::edge);

        return model.build();
    }

    private List<DisplayPoint> inferWayPoints(Edge edge, Grid grid, List<DisplayFlowNode> flowNodes) {
        Position positionFrom = getPositionOfShape(grid, edge.getFrom());
        Position positionTo = getPositionOfShape(grid, edge.getTo());

        EdgeDirection edgeDirection = computeEdgeDirection(positionFrom, positionTo, grid);

        DisplayFlowNode flowNodeFrom = getFlowNode(flowNodes, positionFrom.getShape());
        DisplayFlowNode flowNodeTo = getFlowNode(flowNodes, positionTo.getShape());

        return computeWayPoints(edgeDirection, flowNodeFrom, flowNodeTo);
    }

    private List<DisplayPoint> computeWayPoints(EdgeDirection edgeDirection, DisplayFlowNode flowNodeFrom, DisplayFlowNode flowNodeTo) {
        DisplayDimension dimensionFrom = flowNodeFrom.dimension;
        DisplayDimension dimensionTo = flowNodeTo.dimension;

        List<DisplayPoint> wayPoints = new ArrayList<>();
        switch (edgeDirection) {
            case HorizontalLeftToRight:
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x, dimensionTo.y + dimensionTo.height / 2));
                break;
            case HorizontalRightToLeft:
                wayPoints.add(new DisplayPoint(dimensionFrom.x, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width, dimensionTo.y + dimensionTo.height / 2));
                break;
            case BottomLeftToTopRight_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height));
                break;
            case BottomLeftToTopRight_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x, dimensionTo.y + dimensionTo.height / 2));
                break;
            case BottomRightToTopLeft_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2,
                        dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height));
                break;
            case BottomRightToTopLeft_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height / 2));
                break;
            case TopLeftToBottomRight_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionTo.y));
                break;
            case TopLeftToBottomRight_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y + dimensionTo.height));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x, dimensionTo.y + dimensionTo.height / 2));
                break;
            case TopRightToBottomLeft_FirstHorizontal: // horizontal then vertical
                wayPoints.add(new DisplayPoint(dimensionFrom.x, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionFrom.y + dimensionFrom.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionTo.y));
                break;
            case TopRightToBottomLeft_FirstVertical: // vertical then horizontal
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y + dimensionTo.height));
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionTo.y + dimensionTo.height / 2));
                wayPoints.add(new DisplayPoint(dimensionTo.x + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height / 2));
                break;
            case VerticalBottomToTop:
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionTo.y + dimensionTo.height));
                break;
            case VerticalTopToBottom:
                wayPoints.add(new DisplayPoint(dimensionFrom.x + dimensionFrom.width / 2, dimensionFrom.y + dimensionFrom.height));
                wayPoints.add(new DisplayPoint(dimensionTo.x  + dimensionTo.width / 2, dimensionTo.y));
                break;
            default:
                // do nothing
        }
        return wayPoints;
    }

    private DisplayFlowNode getFlowNode(List<DisplayFlowNode> flowNodes, String flowNodeId) {
        return flowNodes.stream()
                .filter(f -> flowNodeId.equals(f.bpmnElementId))
                .findFirst()
                .get(); // always exist, otherwise error occur on flow node generation
    }

    private Position getPositionOfShape(Grid grid, String shapeId) {
        return grid.getPositions()
                .stream()
                .filter(p -> shapeId.equals(p.getShape()))
                .findFirst()
                .get(); // always exist, otherwise error occur on flow node generation
    }

    // visible for testing
    static EdgeDirection computeEdgeDirection(Position positionFrom, Position positionTo, Grid grid) {
        EdgeDirection edgeDirection = null;
        if (positionFrom.getX() == positionTo.getX()) {
            if (positionFrom.getY() < positionTo.getY()) {
                edgeDirection = EdgeDirection.VerticalTopToBottom;
            }
            else {
                edgeDirection = EdgeDirection.VerticalBottomToTop;
            }
        }
        else  if (positionFrom.getY() == positionTo.getY()) {
            if (positionFrom.getX() < positionTo.getX()) {
                edgeDirection = EdgeDirection.HorizontalLeftToRight;
            }
            else {
                edgeDirection = EdgeDirection.HorizontalRightToLeft;
            }
        } else if (positionFrom.getX() < positionTo.getX()) {
            if (positionFrom.getY() < positionTo.getY()) {
                // check if there is a shape at the right of 'positionFrom'
                boolean shapeExistAtRightPositionFrom = grid.getPositions()
                        .stream()
                        .filter(p -> p.getY() == positionFrom.getY())
                        .anyMatch(p -> p.getX() == positionFrom.getX() + 1);

                edgeDirection = shapeExistAtRightPositionFrom ? EdgeDirection.TopLeftToBottomRight_FirstVertical : EdgeDirection.TopLeftToBottomRight_FirstHorizontal;
            }
            else {
                // check if there is a shape above 'positionFrom'
                boolean shapeExistAbovePositionFrom = grid.getPositions()
                        .stream()
                        .filter(p -> p.getX() == positionFrom.getX())
                        .anyMatch(p -> p.getY() == positionFrom.getY() - 1);

                edgeDirection = shapeExistAbovePositionFrom ? EdgeDirection.BottomLeftToTopRight_FirstHorizontal : EdgeDirection.BottomLeftToTopRight_FirstVertical;
            }
        } else {
            if (positionFrom.getY() < positionTo.getY()) {
                // check if there is a shape at the left of 'positionFrom'
                boolean shapeExistAtLeftPositionFrom = grid.getPositions()
                        .stream()
                        .filter(p -> p.getY() == positionFrom.getY())
                        .anyMatch(p -> p.getX() == positionFrom.getX() - 1);

                edgeDirection = shapeExistAtLeftPositionFrom ? EdgeDirection.TopRightToBottomLeft_FirstVertical : EdgeDirection.TopRightToBottomLeft_FirstHorizontal;
            } else {
                edgeDirection = EdgeDirection.BottomRightToTopLeft_FirstHorizontal;
                // check if there is a shape above 'positionFrom'
                boolean shapeExistAbovePositionFrom = grid.getPositions()
                        .stream()
                        .filter(p -> p.getX() == positionFrom.getX())
                        .anyMatch(p -> p.getY() == positionFrom.getY() - 1);

                edgeDirection = shapeExistAbovePositionFrom ? EdgeDirection.BottomRightToTopLeft_FirstHorizontal : EdgeDirection.BottomRightToTopLeft_FirstVertical;
            }
        }

        return edgeDirection;
    }

    // visible for testing
    static enum EdgeDirection {
        HorizontalLeftToRight,
        HorizontalRightToLeft,
        VerticalBottomToTop,
        VerticalTopToBottom,
        BottomLeftToTopRight_FirstHorizontal,
        BottomLeftToTopRight_FirstVertical,
        BottomRightToTopLeft_FirstHorizontal,
        BottomRightToTopLeft_FirstVertical,
        TopLeftToBottomRight_FirstHorizontal,
        TopLeftToBottomRight_FirstVertical,
        TopRightToBottomLeft_FirstHorizontal,
        TopRightToBottomLeft_FirstVertical,
    }

    private int x(int percentage) {
        return CELL_WIDTH * percentage / 100;
    }

    private int y(int percentage) {
        return CELL_HEIGHT * percentage / 100;
    }


    @RequiredArgsConstructor
    @Builder
    public static class DisplayModel {
        public final int width;
        public final int height;

        @Singular
        public final List<DisplayFlowNode> flowNodes;
        @Singular
        public final List<DisplayEdge> edges;

    }

    @RequiredArgsConstructor
    @Builder
    public static class DisplayFlowNode {

        public final String bpmnElementId;
        public final DisplayDimension dimension;
        public final DisplayLabel label;
        // for non BPMN exporters only
        public final ShapeType type;
        public final int rx;
        public final int strokeWidth;

    }

    @RequiredArgsConstructor
    @Builder
    public static class DisplayEdge {

        public final String bpmnElementId;
        public final List<DisplayPoint> wayPoints;

    }

    @RequiredArgsConstructor
    public static class DisplayDimension {

        public final int x;
        public final int y;
        public final int width;
        public final int height;
    }

    @RequiredArgsConstructor
    public static class DisplayLabel {

        public final String text; // for non BPMN exporters only
        public final int fontSize;
        public final DisplayDimension dimension;
    }

    @RequiredArgsConstructor
    @ToString
    public static class DisplayPoint {

        public final int x;
        public final int y;
    }

}
