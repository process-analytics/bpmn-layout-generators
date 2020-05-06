package io.process.analytics.tools.bpmn.generator.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static Grid of(Position... positions) {
        Grid grid = new Grid();
        for (Position position : positions) {
            grid.add(position);
        }
        return grid;
    }

    /**
     * @return the width of the grid
     */
    public int width() {
        return getLastColumnIndex() + 1;
    }

    /**
     * @return the height of the grid
     */
    public int height() {
        return getLastRowIndex() + 1;
    }

    public Integer getLastRowIndex() {
        return positions.stream().map(Position::getY).reduce(-1, Math::max);
    }

    public Integer getLastColumnIndex() {
        return positions.stream().map(Position::getX).reduce(-1, Math::max);
    }

    public List<Position> getPositions() {
        return positions;
    }

    public Position getPosition(String node) {
        return positions.stream().filter(position -> position.getShape().equals(node)).findFirst().orElseThrow(() -> new IllegalStateException("Node not yet positionned in grid:" + node));
    }

    public List<Position> getRow(int index) {
        return positions.stream().filter(p -> p.getY() == index).collect(Collectors.toList());
    }

    public void remove(Position position) {
        if (!positions.contains(position)) {
            throw new IllegalArgumentException("Position " + position + " is not in the grid");
        }
        positions.remove(position);
    }

    /**
     * Add a new position the grid
     *
     * @param position to add
     */
    public void add(Position position) {
        positions.add(position);
    }

    /**
     * Add a new row after a given row
     * This will update positions of all elements already in this grid
     *
     * @param y add a row after that row
     */
    public void addRowAfter(int y) {
        for (Position cellToMove : getAllPositionBelow(y)) {
            positions.remove(cellToMove);
            positions.add(cellToMove.toBuilder().y(cellToMove.getY() + 1).build());
        }
    }

    private List<Position> getAllPositionBelow(int y) {
        return positions.stream().filter(p -> p.getY() > y).collect(Collectors.toList());
    }

    public void addRowBefore(int y) {
        addRowAfter(y - 1);
    }

    public void removeEmptyRow(int y) {
        List<Position> row = getRow(y);
        if (!row.isEmpty()) {
            throw new IllegalArgumentException("Row " + y + " is not empty");
        }
        for (Position cellToMove : getAllPositionBelow(y)) {
            positions.remove(cellToMove);
            positions.add(cellToMove.toBuilder().y(cellToMove.getY() - 1).build());
        }
    }

    public boolean isFilled(Position position) {
        return positions.stream().anyMatch(p -> p.getX() == position.getX() && p.getY() == position.getY());
    }
}
