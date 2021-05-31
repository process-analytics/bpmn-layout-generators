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

import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static io.process.analytics.tools.bpmn.generator.model.Edge.revertedEdge;
import static io.process.analytics.tools.bpmn.generator.model.Shape.shape;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Shape;

class ShapeSorterTest {

    private final ShapeSorter shapeSorter = new ShapeSorter();

    private final Shape start = shape("start");
    private final Shape step1 = shape("step1");
    private final Shape step2 = shape("step2");
    private final Shape step3 = shape("step3");
    private final Shape step4 = shape("step4");
    private final Shape step5 = shape("step5");
    private final Shape step6 = shape("step6");
    private final Shape end = shape("end");

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
    void should_sort_3_shapes_other_initial_ordering() {
        Diagram diagram = Diagram.builder()
                .shape(end)
                .shape(start)
                .shape(step1)
                .edge(edge(step1, end))
                .edge(edge(start, step1))
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getShapes()).containsExactly(start, step1, end);
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
    public void should_revert_all_edges_of_a_cycle() {
        // start -> step1 -> step2 -> end
        //           <- step3 <-
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
                revertedEdge(t3),
                revertedEdge(t4),
                t5);
    }
    @Test
    public void should_be_able_to_sort_multiple_cycles() {

        // start -> step1 -> step2 -> end
        //           <- step3 <-
        Edge t1 = Edge.builder().id("t1").from(start.getId()).to(step1.getId()).build();
        //cycle 1
        Edge t2 = Edge.builder().id("t2").from(step1.getId()).to(step2.getId()).build();
        Edge t3 = Edge.builder().id("t3").from(step2.getId()).to(step3.getId()).build();
        Edge t4 = Edge.builder().id("t4").from(step3.getId()).to(step1.getId()).build();
        // cycle 2
        Edge t5 = Edge.builder().id("t5").from(step2.getId()).to(step4.getId()).build();
        Edge t6 = Edge.builder().id("t6").from(step4.getId()).to(step5.getId()).build();
        Edge t7 = Edge.builder().id("t7").from(step5.getId()).to(step6.getId()).build();
        Edge t8 = Edge.builder().id("t8").from(step6.getId()).to(step4.getId()).build();

        Edge t9 = Edge.builder().id("t9").from(step5.getId()).to(end.getId()).build();
        Diagram diagram = Diagram.builder()
                .shape(start)
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .shape(step5)
                .shape(step6)
                .shape(end)
                .edge(t1)
                .edge(t2)
                .edge(t3)
                .edge(t4)
                .edge(t5)
                .edge(t6)
                .edge(t7)
                .edge(t8)
                .edge(t9)
                .build();

        Diagram sorted = shapeSorter.sort(diagram);

        assertThat(sorted.getEdges()).containsOnly(
                t1,
                t2,
                revertedEdge(t3),
                revertedEdge(t4),
                t5,
                t6,
                revertedEdge(t7),
                revertedEdge(t8),
                t9);
    }

}