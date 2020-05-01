package io.process.analytics.tools.bpmn.generator.algo;

import static io.process.analytics.tools.bpmn.generator.model.Position.position;

import java.util.List;
import java.util.stream.Collectors;

import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;

public class ShapeLayouter {


    public Grid layout(SortedDiagram diagram) {
        Grid grid = new Grid();
        for (Shape shape : diagram.getShapes()) {
            Position positionOfCurrentShape;
            List<Edge> incomingEdges = diagram.getIncomingEdges(shape.getId());
            if (incomingEdges.isEmpty()) {
                //This is a start node, insert it in a new column
                positionOfCurrentShape = addStartShape(grid, shape);
            } else if (incomingEdges.size() == 1) {
                //find the previous node position
                String previousShapeID = incomingEdges.get(0).getFrom();
                List<Edge> outgoingEdgesOfPreviousShape = diagram.getOutgoingEdges(previousShapeID);
                if (outgoingEdgesOfPreviousShape.size() == 1) {
                    positionOfCurrentShape = addDirectlyNextTo(grid, shape, previousShapeID);
                } else {
                    positionOfCurrentShape = addSplit(grid, shape, previousShapeID, outgoingEdgesOfPreviousShape);
                }
            } else {
                positionOfCurrentShape = addJoin(grid, shape, incomingEdges);
            }
            List<Edge> outgoingEdges = diagram.getOutgoingEdges(shape.getId());
            if (outgoingEdges.size() > 1) {
                //add rows to place elements of this split
                int rowsToAddBeforeAndAfter = outgoingEdges.size() / 2;
                for (int i = 0; i < rowsToAddBeforeAndAfter; i++) {
                    grid.addRowAfter(positionOfCurrentShape.getY());
                    grid.addRowBefore(positionOfCurrentShape.getY());
                }
            }
        }
        compactGrid(grid);
        return grid;
    }

    private void compactGrid(Grid grid) {
        int i = 0;
        while (i < grid.getLastRowIndex()) {
            List<Integer> currentRow = grid.getRow(i).stream().map(Position::getX).collect(Collectors.toList());
            List<Integer> nextRow = grid.getRow(i + 1).stream().map(Position::getX).collect(Collectors.toList());

            boolean currentRowCanBeMovedBelow = true;
            for (Integer shapeIndexInCurrentRow : currentRow) {
                int index = shapeIndexInCurrentRow;
                //we can move the current row below if each element have all adjacent cells free in the row below
                if (nextRow.stream().anyMatch(s -> s == index || s == index + 1 || s == index - 1)) {
                    currentRowCanBeMovedBelow = false;
                    break;
                }
            }

            if (currentRowCanBeMovedBelow) {
                final int finalI = i+1;
                for (Position position : grid.getRow(i)) {
                    grid.remove(position);
                    grid.add(position.toBuilder().y(finalI).build());
                }
                grid.removeEmptyRow(i);
            }else{
                i++;
            }
        }
    }

    private Position addStartShape(Grid grid, Shape shape) {
        Position position = position(shape, 0, grid.getLastColumnIndex() + 1);
        grid.add(position);
        return position;
    }

    private Position addSplit(Grid grid, Shape shape, String previousShapeID, List<Edge> outgoingEdgesOfPreviousShape) {
        Position previousShapePosition = grid.getPosition(previousShapeID);
        int numberOfShapesInTheSplit = outgoingEdgesOfPreviousShape.size();
        int indexOfCurrentShape = outgoingEdgesOfPreviousShape.stream().map(Edge::getTo).collect(Collectors.toList()).indexOf(shape.getId());
        //put element right to the split vertically distributed according to the index
        int relativeYPosition;
        if (numberOfShapesInTheSplit % 2 == 0 && indexOfCurrentShape >= numberOfShapesInTheSplit / 2) {
            //if there is an even number of element, there is no "middle" element so we must add 1 to the index of the elements after the "middle"
            relativeYPosition = indexOfCurrentShape + 1 - numberOfShapesInTheSplit / 2;
        } else {
            relativeYPosition = indexOfCurrentShape - numberOfShapesInTheSplit / 2;
        }
        Position position = position(shape, previousShapePosition.getX() + 1, previousShapePosition.getY() + relativeYPosition);
        grid.add(position);
        return position;
    }

    private Position addJoin(Grid grid, Shape shape, List<Edge> incomingEdges) {
        //first implementation: middle of elements it joins
        // later we should also try yo find the split to align it to that if possible
        List<Position> positions = incomingEdges.stream().map(Edge::getFrom).map(grid::getPosition).collect(Collectors.toList());
        int xMax = positions.stream().map(Position::getX).reduce(0, Math::max);
        int yMax = positions.stream().map(Position::getY).reduce(0, Math::max);
        int yMin = positions.stream().map(Position::getY).reduce(Integer.MAX_VALUE, Math::min);

        int xElement = xMax + 1;
        int yElement = (yMin + yMax) / 2;
        if ((yMin + yMax) % 2 != 0) {
            grid.addRowAfter(yElement);
            yElement++;
        }
        Position position = position(shape, xElement, yElement);
        grid.add(position);
        return position;
    }

    private Position addDirectlyNextTo(Grid grid, Shape shapeToAdd, String rightTo) {
        Position previous = grid.getPosition(rightTo);
        Position position = position(shapeToAdd, previous.getX() + 1, previous.getY());
        grid.add(position);
        return position;
    }


}
