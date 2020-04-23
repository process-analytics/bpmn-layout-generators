package io.process.analytics.tools.bpmn.generator.algo;

import static io.process.analytics.tools.bpmn.generator.model.Position.position;

import java.util.List;
import java.util.UUID;
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
            List<Edge> incomingEdges = diagram.getIncomingEdges(shape.getId());
            if (incomingEdges.isEmpty()) {
                //This is a start node, insert it in a new column
                grid.add(position(shape, 0, grid.getLastColumnIndex() + 1));
            } else if (incomingEdges.size() == 1) {
                //find the previous node position
                UUID previousShapeID = incomingEdges.get(0).getFrom();
                List<Edge> outgoingEdgesOfPreviousShape = diagram.getOutgoingEdges(previousShapeID);
                if (outgoingEdgesOfPreviousShape.size() == 1) {
                    addDirectlyNextTo(grid, shape, previousShapeID);
                } else {
                    //next to a split
                    throw new UnsupportedOperationException("Positioning a Node that is splitting is not yet supported");
                }
            } else {
                throw new UnsupportedOperationException("Positioning a Node that is splitting is not yet supported");
            }
        }
        return grid;
    }

    // TODO this is the next thing to implement: adding shapes that have more than one incomming edge
    private void addJoin(Grid grid, Shape shape, List<Edge> incomingEdges) {
        //first implementation: middle of elements it joins
        // later we should also try yo find the split to align it to that if possible

        int xMax = 0;
        int ySum = 0;
        for (Position position : incomingEdges.stream().map(e -> grid.getPosition(e.getFrom())).collect(Collectors.toList())) {
            xMax = Math.max(xMax, position.getX());
            ySum += position.getY();
        }
        int y = ySum / incomingEdges.size();
        if (ySum % incomingEdges.size() != 0) {
            // division is not and integer, add a row to add this elements
            grid.addRowAfter(y);// might not be useful, rows are added when handlit the 'split'
        }
        grid.add(position(shape, xMax + 1, y));
    }

    private void addDirectlyNextTo(Grid grid, Shape shapeToAdd, UUID rightTo) {
        Position previous = grid.getPosition(rightTo);
        grid.add(position(shapeToAdd, previous.getX() + 1, previous.getY()));
    }


}
