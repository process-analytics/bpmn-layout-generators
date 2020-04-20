package io.process.analytics.tools.bpmn.generator.algo;

import static org.assertj.core.api.Assertions.assertThat;

import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Node;
import org.junit.jupiter.api.Test;

class NodeSorterTest {

    private NodeSorter nodeSorter = new NodeSorter();

    Node start = new Node("start");
    Node step1 = new Node("step1");
    Node step2 = new Node("step2");
    Node step3 = new Node("step3");
    Node step4 = new Node("step4");
    Node step5 = new Node("step5");

    @Test
    void should_sort_2_nodes() {
        Diagram diagram = Diagram.builder()
                .node(step1)
                .node(start)
                .edge(edge(start, step1))
                .build();

        Diagram sorted = nodeSorter.sort(diagram);

        assertThat(sorted.getNodes()).containsExactly(start, step1);
    }
    @Test
    void should_sort_3_nodes() {
        Diagram diagram = Diagram.builder()
                .node(step1)
                .node(step2)
                .node(start)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .build();

        Diagram sorted = nodeSorter.sort(diagram);

        assertThat(sorted.getNodes()).containsExactly(start, step1, step2);
    }

    private Edge edge(Node step1, Node step2) {
        return new Edge(step1.getUuid(), step2.getUuid());
    }

    @Test
    void should_sort_nodes_with_split_and_join() {
        // there is 2 branches: step2 and step3
        //
        // start --> step1 ---------> step 2
        //             \               \
        //              --> step 3 -->  step 4 -->  step 5

        Diagram diagram = Diagram.builder()
                .node(step1)
                .node(step3)
                .node(step2)
                .node(step5)
                .node(step4)
                .node(start)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .edge(edge(step1, step3))
                .edge(edge(step3, step4))
                .edge(edge(step2, step4))
                .edge(edge(step4, step5))
                .build();

        Diagram sorted = nodeSorter.sort(diagram);

        assertThat(sorted.getNodes()).startsWith(start, step1);
        assertThat(sorted.getNodes()).contains(step2, step3);
        assertThat(sorted.getNodes()).endsWith(step4, step5);
    }

    @Test
    void should_sort_nodes_with_loop() {
        // loop between step2, step3, step4
        //
        //             ---------------------------------
        //             |                                |
        //             V                                |
        // start --> step1 --> step 2 --> step 3 -->  step 4 -->  step 5

        Diagram diagram = Diagram.builder()
                .node(step1)
                .node(step3)
                .node(step2)
                .node(step5)
                .node(step4)
                .node(start)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .edge(edge(step2, step3))
                .edge(edge(step3, step4))
                .edge(edge(step4, step2))
                .edge(edge(step4, step5))
                .build();

        Diagram sorted = nodeSorter.sort(diagram);

        assertThat(sorted.getNodes()).containsExactly(start, step1, step2, step3, step4, step5);
    }

}