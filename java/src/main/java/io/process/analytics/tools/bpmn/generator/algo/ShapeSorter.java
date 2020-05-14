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

import static io.process.analytics.tools.bpmn.generator.model.Edge.revertedEdge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
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
    public Diagram sort(Diagram diagram) {
        Set<Shape> nodesToSort = new HashSet<>(diagram.getShapes());
        Set<Edge> remainingEdges = new HashSet<>(diagram.getEdges());
        Set<Edge> finalEdges = new HashSet<>(diagram.getEdges());
        List<Join> joins = findAllJoins(nodesToSort, remainingEdges);

        Diagram.DiagramBuilder sortedDiagram = Diagram.builder();

        while (!nodesToSort.isEmpty()) {
            Set<Shape> startShapes = getStartNodes(nodesToSort, remainingEdges);
            if (!startShapes.isEmpty()) {
                for (Shape startShape : startShapes) {
                    nodesToSort.remove(startShape);
                    sortedDiagram.shape(startShape);
                    remainingEdges = removeEdgesStartingWithNode(remainingEdges, startShape);
                    joins.stream().filter(j -> j.isJoining(startShape)).forEach(j -> j.markProcessed(startShape));
                }
            } else {
                //retrieve a join that we "entered", i.e. we processed an element that has an edge going to this join.
                Join join = getAJoinThatWasProcessed(joins);
                //revert all edges from this join to remove the cycle
                remainingEdges = revertNonProcessedEdgeOfTheJoin(remainingEdges, join);
                finalEdges = revertNonProcessedEdgeOfTheJoin(finalEdges, join);
            }
        }
        return sortedDiagram.edges(finalEdges).build();
    }

    private Set<Edge> revertNonProcessedEdgeOfTheJoin(Set<Edge> remainingEdges, Join join) {
        return remainingEdges.stream().map(edge -> {
            if (join.contains(edge) && !join.wasProcessed(edge.getFrom())) {
                //revert the edge
                return revertedEdge(edge);
            } else {
                return edge;
            }
        }).collect(Collectors.toSet());
    }

    private Join getAJoinThatWasProcessed(List<Join> joins) {
        // we should always have a join that was processed at least once when we don't have a start node
        return joins.stream().filter(j -> !j.getProcessedEdges().isEmpty()).findFirst().get();
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
        @Builder.Default
        private Set<String> processedEdges = new HashSet<>();

        boolean isJoining(Shape shape) {
            return incomings.contains(shape.getId());
        }

        void markProcessed(Shape shape) {
            processedEdges.add(shape.getId());
        }

        boolean wasProcessed(String shapeId) {
            return processedEdges.contains(shapeId);
        }

        boolean contains(Edge edge) {
            return to.getId().equals(edge.getTo()) && incomings.contains(edge.getFrom());
        }

    }


}
