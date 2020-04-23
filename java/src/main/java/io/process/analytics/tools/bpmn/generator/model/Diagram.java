package io.process.analytics.tools.bpmn.generator.model;

import java.util.Set;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Diagram {

    @Singular
    private Set<Shape> shapes;
    @Singular
    private Set<Edge> edges;
}
