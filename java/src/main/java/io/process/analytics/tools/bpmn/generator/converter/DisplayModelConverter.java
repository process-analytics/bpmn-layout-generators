package io.process.analytics.tools.bpmn.generator.converter;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

public class DisplayModelConverter {

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

            // TODO manage empty name
            String name = diagram.getShapes().stream().filter(n -> n.getId().equals(position.getShape())).findFirst().get().getName();
            int labelX = xOffset + x(50);
            int labelY = yOffset + y(50);

            DisplayDimension labelDimension = new DisplayDimension(labelX, labelY, -1, -1);
            DisplayLabel label = new DisplayLabel(name, y(16), labelDimension);

            model.flowNode(new DisplayFlowNode(flowNodeDimension, label, y(10), y(5)));
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
    public static class DisplayFlowNode {

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
