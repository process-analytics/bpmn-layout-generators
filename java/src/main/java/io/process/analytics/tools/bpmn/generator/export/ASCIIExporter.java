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

import static io.process.analytics.tools.bpmn.generator.internal.StringUtils.defaultIfNull;

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
            char[] name = defaultIfNull(position.getShapeName()).toCharArray();
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
}
