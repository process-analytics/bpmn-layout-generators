package io.process.analytics.tools.bpmn.generator.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Diagram {

    @Singular
    private List<Shape> shapes;
    @Singular
    private Set<Edge> edges;


    public List<Edge> getIncomingEdges(String shapeId) {
        return edges.stream().filter(e -> e.getTo().equals(shapeId)).collect(Collectors.toList());
    }

    public List<Edge> getOutgoingEdges(String shapeId) {
        return edges.stream().filter(e -> e.getFrom().equals(shapeId)).collect(Collectors.toList());
    }

    public List<Edge> getOriginalIncomingEdges(String shapeId) {
        return edges.stream().filter(e -> (e.getTo().equals(shapeId) && !e.isReverted()) || (e.getFrom().equals(shapeId) && e.isReverted())).collect(Collectors.toList());
    }

    public List<Edge> getOriginalOutgoingEdges(String shapeId) {
        return edges.stream().filter(e -> (e.getFrom().equals(shapeId) && !e.isReverted()) || (e.getTo().equals(shapeId) && e.isReverted())).collect(Collectors.toList());
    }

    public Shape getShape(String shapeId) {
        return shapes.stream().filter(s -> s.getId().equals(shapeId)).findFirst().get();
    }

}
