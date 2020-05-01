package io.process.analytics.tools.bpmn.generator.export;

import java.util.Arrays;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;

public class ASCIIExporter {

    private static final int CELL_WIDTH = 7;

    public String export(Grid grid) {
        int width = grid.width();
        int height = grid.height();
        List<Position> positions = grid.getPositions();
        return export(width, height, positions);
    }

    public String export(int width, int height, List<Position> positions) {
        char[][] charGrid = new char[height][width * CELL_WIDTH];
        for (char[] chars : charGrid) {
            Arrays.fill(chars, ' ');
        }
        for (Position position : positions) {
            char[] charRow = charGrid[position.getY()];
            char[] name = position.getShapeName().toCharArray();
            System.arraycopy(name, 0, charRow, 7 * position.getX(), Math.min(name.length, CELL_WIDTH));
        }
        StringBuilder content = new StringBuilder("Diagram:\n");
        content.append('+').append(horizontalBar(CELL_WIDTH * width)).append('+').append('\n');
        for (char[] chars : charGrid) {
            content.append('|').append(chars).append('|').append('\n');
        }
        content.append('+').append(horizontalBar(CELL_WIDTH * width)).append('+').append('\n');
        return content.toString();
    }


    private char[] horizontalBar(int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, '-');
        return chars;
    }

    public static String toAscii(Grid grid) {
        return new ASCIIExporter().export(grid);
    }

    public static String toAscii(int width, int height, List<Position> positions) {
        return new ASCIIExporter().export(width, height, positions);
    }
    public static String toAscii(int width, int height, Position... positions) {
        return toAscii(width, height, Arrays.asList(positions));
    }
}
