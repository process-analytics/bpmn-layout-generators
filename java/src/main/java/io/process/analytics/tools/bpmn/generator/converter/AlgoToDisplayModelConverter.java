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

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

public class AlgoToDisplayModelConverter {

    // TODO this should be fields of the class and configured by the client code
    private static final int CELL_WIDTH = 200;
    private static final int CELL_HEIGHT = 100;

    public DisplayModel convert(Grid grid, SortedDiagram diagram) {
        DisplayModel.DisplayModelBuilder model = DisplayModel.builder();
        model.width(grid.width() * CELL_WIDTH).height(grid.height() * CELL_HEIGHT);
        
        for (Position position : grid.getPositions()) {
            int xOffset = position.getX() * CELL_WIDTH;
            int yOffset = position.getY() * CELL_HEIGHT;
            int nodeWidth = x(60);
            int nodeHeight = y(60);

            int x = xOffset + (CELL_WIDTH - nodeWidth) / 2;
            int y = yOffset + (CELL_HEIGHT - nodeHeight) / 2;
            DisplayDimension flowNodeDimension = new DisplayDimension(x, y, nodeWidth, nodeHeight);

            // TODO manage when not found (should not occur)
            Shape shape = diagram.getShapes().stream().filter(s -> s.getId().equals(position.getShape())).findFirst().get();
            String name = shape.getName();
            int labelX = xOffset + x(50);
            int labelY = yOffset + y(50);

            DisplayDimension labelDimension = new DisplayDimension(labelX, labelY, -1, -1);
            DisplayLabel label = new DisplayLabel(name, y(16), labelDimension);

            model.flowNode(DisplayFlowNode.builder().bpmnElementId(shape.getId())
                    .dimension(flowNodeDimension)
                    .label(label)
                    .rx(y(10)).strokeWidth(y(5)).build());
        }
        return model.build();
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

    }

    @RequiredArgsConstructor
    @Builder
    public static class DisplayFlowNode {

        public final String bpmnElementId;
        public final DisplayDimension dimension;
        public final DisplayLabel label;
        // for non BPMN exporters only
        public final int rx;
        public final int strokeWidth;

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

}
