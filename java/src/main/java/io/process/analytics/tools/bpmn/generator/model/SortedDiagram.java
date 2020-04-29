package io.process.analytics.tools.bpmn.generator.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class SortedDiagram {

    @Singular
    private List<Shape> shapes;
    @Singular
    private Set<Edge> edges;


    public List<Edge> getIncomingEdges(String node) {
        return edges.stream().filter(e -> e.getTo().equals(node)).collect(Collectors.toList());
    }
    public List<Edge> getOutgoingEdges(String node) {
        return edges.stream().filter(e -> e.getFrom().equals(node)).collect(Collectors.toList());
    }
}
