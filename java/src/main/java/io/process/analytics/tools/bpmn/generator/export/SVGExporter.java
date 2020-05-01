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
package io.process.analytics.tools.bpmn.generator.export;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class SVGExporter {

    private static final int CELL_WIDTH = 200;
    private static final int CELL_HEIGHT = 100;

    public byte[] export(Grid grid, SortedDiagram diagram) {
        StringBuilder content = new StringBuilder();
        content.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"")
                .append(grid.width() * CELL_WIDTH)
                .append("\" height=\"")
                .append(grid.height() * CELL_HEIGHT).append("\">\n");
        for (Position position : grid.getPositions()) {
            int xOffset = position.getX() * CELL_WIDTH;
            int yOffset = position.getY() * CELL_HEIGHT;
            int nodeWidth = x(60);
            int nodeHeight = y(60);

            int x = xOffset + (CELL_WIDTH - nodeWidth) / 2;
            int y = yOffset + (CELL_HEIGHT - nodeHeight) / 2;
            DisplayDimension flowNodeDimension = new DisplayDimension(x, y, nodeWidth, nodeHeight);

            // TODO manage empty name
            String name = diagram.getShapes().stream().filter(n -> n.getId().equals(position.getShape())).findFirst().get().getName();
            int labelX = xOffset + x(50);
            int labelY = yOffset + y(50);

            DisplayDimension labelDimension = new DisplayDimension(labelX, labelY, -1, -1);
            DisplayLabel label = new DisplayLabel(name, y(16), labelDimension);

            DisplayFlowNode flowNode = new DisplayFlowNode(flowNodeDimension, label, y(10), y(5));


            content.append("<rect ");
            content.append("x=\"").append(flowNodeDimension.x).append("\" ");
            content.append("y=\"").append(flowNodeDimension.y).append("\" ");
            content.append("width=\"").append(flowNodeDimension.width).append("\" ");
            content.append("height=\"").append(flowNodeDimension.height).append("\" ");
            content.append("rx=\"").append(flowNode.rx).append("\" ");
            content.append("fill=\"#E3E3E3\" stroke=\"#92ADC8\" ");
            content.append("stroke-width=\"").append(flowNode.strokeWidth).append("\"/>\n");
            content.append("<text x=\"").append(labelDimension.x);
            content.append("\" y=\"").append(labelDimension.y);
            content.append("\" text-anchor=\"middle\" font-size=\"").append(label.fontSize);
            content.append("\" fill=\"#374962\">");
            content.append(label.text).append("</text>\n");
        }
        content.append("</svg>");
        return content.toString().getBytes();
    }

    private int x(int percentage) {
        return CELL_WIDTH * percentage / 100;
    }

    private int y(int percentage) {
        return CELL_HEIGHT * percentage / 100;
    }





    private static class DisplayModelConverter {

        // TODO this should be fields of the class and configured by the client code
        private static final int CELL_WIDTH = 200;
        private static final int CELL_HEIGHT = 100;

        public void compute(Grid grid, SortedDiagram diagram) {

        }


    }

    @RequiredArgsConstructor
    private static class DisplayFlowNode {

        public final DisplayDimension dimension;
        public final DisplayLabel label;
        // for non BPMN exporters only
        public final int rx;
        public final int strokeWidth;

    }

    @RequiredArgsConstructor
    private static class DisplayDimension {

        public final int x;
        public final int y;
        public final int width;
        public final int height;
    }

    @RequiredArgsConstructor
    private static class DisplayLabel {

        public final String text; // for non BPMN exporters only
        public final int fontSize;
        public final DisplayDimension dimension;
    }

}
