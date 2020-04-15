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
import java.util.UUID;
import java.util.stream.Collectors;

import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Node;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * Sort nodes of a diagram in topological order.
 *
 * The algorithm also handles cycle.
 *
 */
public class NodeSorter {


    /**
     * sort nodes of a diagram in topological order
     *
     * @param diagram to sort
     * @return a diagram with same nodes but sorted
     */
    Diagram sort(Diagram diagram) {
        Set<Node> nodesToSort = new HashSet<>(diagram.getNodes());
        Set<Edge> edges = new HashSet<>(diagram.getEdges());
        List<Join> joins = findAllJoins(nodesToSort, edges);

        Diagram.DiagramBuilder sortedDiagram = Diagram.builder();

        while (!nodesToSort.isEmpty()) {
            Set<Node> startNodes = getStartNodes(nodesToSort, edges);
            if (!startNodes.isEmpty()) {
                for (Node startNode : startNodes) {
                    nodesToSort.remove(startNode);
                    sortedDiagram.node(startNode);
                    edges = removeEdgesStartingWithNode(edges, startNode);
                    joins.stream().filter(j -> j.isJoining(startNode)).forEach(Join::markProcessed);
                }
            } else {
                //retrieve a join that we "entered", i.e. we processed an element that has an edge going to this join.
                Join join = getAJoinThatWasProcessed(joins);
                //revert all edges from this join to remove the cycle
                edges = edges.stream().map(edge -> {
                    if (join.contains(edge)) {
                        //revert the edge
                        return new Edge(edge.getTo(), edge.getFrom());
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

    private Set<Edge> removeEdgesStartingWithNode(Set<Edge> edges, Node startNode) {
        return edges.stream().filter(e -> e.getFrom() != startNode.getUuid()).collect(Collectors.toSet());
    }

    private Set<Node> getStartNodes(Set<Node> nodesToSort, Set<Edge> edges) {
        Set<Node> startNodes = new HashSet<>();
        for (Node m : nodesToSort) {
            if (hasNoIncomingLink(m, edges)) {
                startNodes.add(m);
            }
        }
        return startNodes;
    }

    private List<Join> findAllJoins(Set<Node> nodes, Set<Edge> edges) {
        //get the nodes that are "join"
        return nodes.stream().map(n -> Join.builder().to(n))
                .map(j -> j.incomings(edges.stream().filter(e -> e.getTo() == j.to.getUuid()).map(Edge::getFrom).collect(Collectors.toList())))
                .map(Join.JoinBuilder::build)
                //keep only join if there is more than 1 edge incoming
                .filter(j -> j.incomings.size() > 1)
                .collect(Collectors.toList());

    }


    private boolean hasNoIncomingLink(Node m, Set<Edge> edges) {
        return edges.stream().noneMatch(e -> e.getTo() == m.getUuid());
    }

    @Data
    @Builder
    private static class Join {
        @Singular
        private Set<UUID> incomings;
        private final Node to;
        private boolean wasProcessed;

        boolean isJoining(Node node) {
            return incomings.contains(node.getUuid());
        }

        void markProcessed() {
            setWasProcessed(true);
        }

        boolean contains(Edge edge) {
            return to.getUuid().equals(edge.getTo()) && incomings.contains(edge.getFrom());
        }

    }


}
