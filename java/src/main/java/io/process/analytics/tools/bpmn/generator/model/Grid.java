package io.process.analytics.tools.bpmn.generator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * Represent a grid with coordinate as follow
 *
 * <pre>
 *  + ➞ x
 *  ↓
 *  y
 * </pre>
 */
public class Grid {

    private final List<Position> positions = new ArrayList<>();

    /**
     * @return the width of the grid
     */
    public int width() {
        return getLastRowIndex() + 1;
    }

    /**
     * @return the height of the grid
     */
    public int height() {
        return getLastColumnIndex() + 1;
    }

    public Integer getLastRowIndex() {
        return positions.stream().map(Position::getX).reduce(-1, Math::max);
    }

    public Integer getLastColumnIndex() {
        return positions.stream().map(Position::getY).reduce(-1, Math::max);
    }

    public List<Position> getPositions() {
        return positions;
    }

    public Position getPosition(UUID node) {
        return positions.stream().filter(position -> position.getShape().equals(node)).findFirst().orElseThrow(() -> new IllegalStateException("Node not yet positionned in grid:" + node));
    }

    /**
     * Add a new position the grid
     * @param position to add
     */
    public void add(Position position) {
        positions.add(position);
    }

    /**
     * Add a new row after a given row
     * This will update positions of all elements already in this grid
     * @param y add a row after that row
     */
    public void addRowAfter(int y) {
        List<Position> cellsToMove = positions.stream().filter(p -> p.getY() > y).collect(Collectors.toList());
        for (Position cellToMove : cellsToMove) {
            positions.remove(cellToMove);
            positions.add(cellToMove.toBuilder().y(cellToMove.getY() + 1).build());
        }
    }
}
