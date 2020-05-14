package io.process.analytics.tools.bpmn.generator.algo;

import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static io.process.analytics.tools.bpmn.generator.model.Edge.revertedEdge;
import static org.assertj.core.api.Assertions.assertThat;

import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import org.junit.jupiter.api.Test;

class ShapeSorterTest {

    private ShapeSorter shapeSorter = new ShapeSorter();

    Shape start = new Shape("start");
    Shape step1 = new Shape("step1");
    Shape step2 = new Shape("step2");
    Shape step3 = new Shape("step3");
    Shape step4 = new Shape("step4");
    Shape step5 = new Shape("step5");
    Shape end = new Shape("end");

    @Test
    void should_sort_2_shapes() {
        Diagram diagram = Diagram.builder()
                .shape(step1)
                .shape(start)
                .edge(edge(start, step1))
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getShapes()).containsExactly(start, step1);
    }
    @Test
    void should_sort_3_shapes() {
        Diagram diagram = Diagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(start)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getShapes()).containsExactly(start, step1, step2);
    }

    @Test
    void should_sort_shapes_with_split_and_join() {
        // there is 2 branches: step2 and step3
        //
        // start --> step1 ---------> step 2
        //             \               \
        //              --> step 3 -->  step 4 -->  step 5

        Diagram diagram = Diagram.builder()
                .shape(step1)
                .shape(step3)
                .shape(step2)
                .shape(step5)
                .shape(step4)
                .shape(start)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .edge(edge(step1, step3))
                .edge(edge(step3, step4))
                .edge(edge(step2, step4))
                .edge(edge(step4, step5))
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getShapes()).startsWith(start, step1);
        assertThat(sorted.getShapes()).contains(step2, step3);
        assertThat(sorted.getShapes()).endsWith(step4, step5);
    }

    @Test
    void should_sort_shapes_with_cycle() {
        // loop between step2, step3, step4
        //
        //                      -------------------------
        //                      |                        |
        //                      V                        |
        // start --> step1 --> step 2 --> step 3 -->  step 4 -->  step 5

        Diagram diagram = Diagram.builder()
                .shape(step1)
                .shape(step3)
                .shape(step2)
                .shape(step5)
                .shape(step4)
                .shape(start)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .edge(edge(step2, step3))
                .edge(edge(step3, step4))
                .edge(edge(step4, step2))
                .edge(edge(step4, step5))
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getShapes()).containsExactly(start, step1, step2, step3, step4, step5);
    }

    @Test
    public void should_revert_one_edge_of_a_cycle() {
        Edge t1 = Edge.builder().id("t1").from(start.getId()).to(step1.getId()).build();
        Edge t2 = Edge.builder().id("t2").from(step1.getId()).to(step2.getId()).build();
        Edge t3 = Edge.builder().id("t3").from(step2.getId()).to(step3.getId()).build();
        Edge t4 = Edge.builder().id("t4").from(step3.getId()).to(step1.getId()).build();
        Edge t5 = Edge.builder().id("t5").from(step2.getId()).to(end.getId()).build();
        Diagram diagram = Diagram.builder()
                .shape(start)
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(end)
                .edge(t1)
                .edge(t2)
                .edge(t3)
                .edge(t4)
                .edge(t5)
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getEdges()).containsOnly(
                t1,
                t2,
                t3,
                revertedEdge(t4),
                t5);
    }

}