package io.process.analytics.tools.bpmn.generator.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Position {
    private final String shape;
    private final String shapeName;
    private final ShapeType shapeType;
    private final boolean isSplitGateway;

    private final int x;
    private final int y;

    public static Position position(Shape shape, int x, int y) {
        return new Position(shape.getId(), shape.getName(), shape.getType(), shape.isSplitGateway(), x, y);
    }
}
