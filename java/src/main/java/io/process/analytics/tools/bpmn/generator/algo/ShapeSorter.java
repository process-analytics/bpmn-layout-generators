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
package io.process.analytics.tools.bpmn.generator.algo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * Sort nodes of a diagram in topological order.
 *
 * The algorithm also handles cycle.
 *
 */
public class ShapeSorter {


    /**
     * sort nodes of a diagram in topological order
     *
     * @param diagram to sort
     * @return a diagram with same nodes but sorted
     */
    public SortedDiagram sort(Diagram diagram) {
        Set<Shape> nodesToSort = new HashSet<>(diagram.getShapes());
        Set<Edge> edges = new HashSet<>(diagram.getEdges());
        List<Join> joins = findAllJoins(nodesToSort, edges);

        SortedDiagram.SortedDiagramBuilder sortedDiagram = SortedDiagram.builder();

        while (!nodesToSort.isEmpty()) {
            Set<Shape> startShapes = getStartNodes(nodesToSort, edges);
            if (!startShapes.isEmpty()) {
                for (Shape startShape : startShapes) {
                    nodesToSort.remove(startShape);
                    sortedDiagram.shape(startShape);
                    edges = removeEdgesStartingWithNode(edges, startShape);
                    joins.stream().filter(j -> j.isJoining(startShape)).forEach(Join::markProcessed);
                }
            } else {
                //retrieve a join that we "entered", i.e. we processed an element that has an edge going to this join.
                Join join = getAJoinThatWasProcessed(joins);
                //revert all edges from this join to remove the cycle
                edges = edges.stream().map(edge -> {
                    if (join.contains(edge)) {
                        //revert the edge
                        return new Edge(edge.getId(), edge.getTo(), edge.getFrom());
                    } else {
                        return edge;
                    }
                }).collect(Collectors.toSet());
            }
        }
        return sortedDiagram.edges(diagram.getEdges()).build();
    }

    private Join getAJoinThatWasProcessed(List<Join> joins) {
        // we should always have a join that was processed when we don't have a start node
        return joins.stream().filter(j -> j.wasProcessed).findFirst().get();
    }

    private Set<Edge> removeEdgesStartingWithNode(Set<Edge> edges, Shape startShape) {
        return edges.stream().filter(e -> e.getFrom() != startShape.getId()).collect(Collectors.toSet());
    }

    private Set<Shape> getStartNodes(Set<Shape> nodesToSort, Set<Edge> edges) {
        return nodesToSort.stream()
                .filter(n -> hasNoIncomingLink(n, edges))
                .collect(Collectors.toSet());
    }

    private List<Join> findAllJoins(Set<Shape> shapes, Set<Edge> edges) {
        //get the nodes that are "join"
        return shapes.stream().map(n -> Join.builder().to(n))
                .map(j -> j.incomings(edges.stream().filter(e -> e.getTo() == j.to.getId()).map(Edge::getFrom).collect(Collectors.toList())))
                .map(Join.JoinBuilder::build)
                //keep only join if there is more than 1 edge incoming
                .filter(j -> j.incomings.size() > 1)
                .collect(Collectors.toList());

    }


    private boolean hasNoIncomingLink(Shape m, Set<Edge> edges) {
        return edges.stream().noneMatch(e -> e.getTo() == m.getId());
    }

    @Data
    @Builder
    private static class Join {
        @Singular
        private Set<String> incomings;
        private final Shape to;
        private boolean wasProcessed;

        boolean isJoining(Shape shape) {
            return incomings.contains(shape.getId());
        }

        void markProcessed() {
            setWasProcessed(true);
        }

        boolean contains(Edge edge) {
            return to.getId().equals(edge.getTo()) && incomings.contains(edge.getFrom());
        }

    }


}
