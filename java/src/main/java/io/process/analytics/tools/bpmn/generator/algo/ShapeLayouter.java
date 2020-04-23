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
                addJoin(grid, shape, incomingEdges);
            }
        }
        return grid;
    }

    private void addJoin(Grid grid, Shape shape, List<Edge> incomingEdges) {
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
        grid.add(position(shape, xElement, yElement));
    }

    private void addDirectlyNextTo(Grid grid, Shape shapeToAdd, UUID rightTo) {
        Position previous = grid.getPosition(rightTo);
        grid.add(position(shapeToAdd, previous.getX() + 1, previous.getY()));
    }


}
