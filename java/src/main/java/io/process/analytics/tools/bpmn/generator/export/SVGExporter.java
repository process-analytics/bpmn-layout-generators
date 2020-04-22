package io.process.analytics.tools.bpmn.generator.export;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;

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
            content.append("<rect ");
            content.append("x=\"").append(xOffset + (CELL_WIDTH - nodeWidth) / 2).append("\" ");
            content.append("y=\"").append(yOffset + (CELL_HEIGHT - nodeHeight) / 2).append("\" ");
            content.append("width=\"").append(nodeWidth).append("\" ");
            content.append("height=\"").append(nodeHeight).append("\" ");
            content.append("rx=\"").append(y(10)).append("\" ");
            content.append("fill=\"#E3E3E3\" stroke=\"#92ADC8\" ");
            content.append("stroke-width=\"").append(y(5)).append("\"/>\n");
            String name = diagram.getShapes().stream().filter(n -> n.getId().equals(position.getShape())).findFirst().get().getName();
            content.append("<text x=\"").append(xOffset + x(50));
            content.append("\" y=\"").append(yOffset + y(50));
            content.append("\" text-anchor=\"middle\" font-size=\"").append(y(16));
            content.append("\" fill=\"#374962\">");
            content.append(name).append("</text>\n");

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
}
