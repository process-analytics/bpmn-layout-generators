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

import static io.process.analytics.tools.bpmn.generator.converter.Configuration.CELL_HEIGHT;
import static io.process.analytics.tools.bpmn.generator.converter.Configuration.CELL_WIDTH;

import java.util.List;

import io.process.analytics.tools.bpmn.generator.converter.waypoint.WayPointsComputer;
import io.process.analytics.tools.bpmn.generator.model.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;

public class AlgoToDisplayModelConverter {

    public DisplayModel convert(Grid grid, Diagram diagram) {
        DisplayModel.DisplayModelBuilder model = DisplayModel.builder();
        // dimensions must be increased when generating alternate path to avoid edge overlapping on shapes
        // mainly impact on svg exporter. If the dimensions are not updated, the alternate paths are not fully displayed
        // as they are out of the viewport of the svg
        // increase to display edges with extra paths to avoid shape overlapping
        model.width(grid.width() * CELL_WIDTH).height((grid.height() + 1) * CELL_HEIGHT);

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

        WayPointsComputer wayPointsComputer = new WayPointsComputer(grid, model.flowNodes);
        diagram.getEdges()
                .stream()
                .map(edge -> new DisplayEdge(edge.getId(), wayPointsComputer.inferWayPoints(edge)))
                .forEach(model::edge);

        return model.build();
    }

    private static int x(int percentage) {
        return CELL_WIDTH * percentage / 100;
    }

    private static int y(int percentage) {
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
