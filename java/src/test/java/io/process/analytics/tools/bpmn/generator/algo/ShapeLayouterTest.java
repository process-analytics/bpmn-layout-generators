package io.process.analytics.tools.bpmn.generator.algo;

import static io.process.analytics.tools.bpmn.generator.export.ASCIIExporter.toAscii;
import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static io.process.analytics.tools.bpmn.generator.model.Position.position;
import static io.process.analytics.tools.bpmn.generator.model.Shape.shape;
import static org.assertj.core.api.Assertions.assertThat;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import org.junit.jupiter.api.Test;

class ShapeLayouterTest {

    private final ShapeLayouter shapeLayouter = new ShapeLayouter();

    Shape start = shape("start");
    Shape step1 = shape("step1");
    Shape step2 = shape("step2");
    Shape step3 = shape("step3");
    Shape step4 = shape("step4");
    Shape step5 = shape("step5");
    Shape end = shape("end");

    @Test
    public void should_layout_a_diagram_with_one_node_on_a_grid() {
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1).build();

        Grid grid = shapeLayouter.layout(diagram);

        assertThat(grid.width()).isEqualTo(1);
        assertThat(grid.height()).isEqualTo(1);
        assertThat(grid.getPositions()).containsExactly(position(step1, 0, 0));
    }

    @Test
    public void should_layout_a_diagram_with_2_linked_node_on_a_grid() {
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .edge(edge(step1, step2))
                .build();

        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(position(step1, 0, 0), position(step2, 1, 0))));
    }

    @Test
    public void should_layout_a_diagram_with_2_unlinked_node_on_a_grid() {
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .build();

        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(position(step1, 0, 0), position(step2, 0, 1))));
    }

    @Test
    public void should_layout_a_diagram_that_have_2_branches() {
        //  +---------------------------------+
        //  |              step2              |
        //  |start  step1         step4  end  |
        //  |              step3              |
        //  +---------------------------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(start)
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .shape(end)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .edge(edge(step1, step3))
                .edge(edge(step3, step4))
                .edge(edge(step2, step4))
                .edge(edge(step4, end))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(start, 0, 1),
                position(step1, 1, 1),
                position(step2, 2, 0),
                position(step3, 2, 2),
                position(step4, 3, 1),
                position(end, 4, 1)
        )));
    }

    @Test
    public void should_layout_a_diagram_that_have_join() {
        //  +--------------+
        //  |step1         |
        //  |       step3  |
        //  |step2         |
        //  +--------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .edge(edge(step1, step3))
                .edge(edge(step2, step3))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(step1, 0, 0),
                position(step2, 0, 2),
                position(step3, 1, 1))));
    }

    @Test
    public void should_layout_a_diagram_with_a_split_of_2_elements() {
        //  +--------------+
        //  |       step2  |
        //  |step1         |
        //  |       step3  |
        //  +--------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .edge(edge(step1, step2))
                .edge(edge(step1, step3))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(grid.getPositions()).as(toAscii(grid)).containsOnly(
                position(step1, 0, 1),
                position(step2, 1, 0),
                position(step3, 1, 2));
    }

    @Test
    public void should_layout_a_diagram_with_a_split_of_3_elements() {
        //  +--------------+
        //  |       step2  |
        //  |step1  step3  |
        //  |       step4  |
        //  +--------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .edge(edge(step1, step2))
                .edge(edge(step1, step3))
                .edge(edge(step1, step4))
                .build();


        Grid grid = shapeLayouter.layout(diagram);


        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(step1, 0, 1),
                position(step2, 1, 0),
                position(step3, 1, 1),
                position(step4, 1, 2))));
    }

    @Test
    public void should_layout_a_diagram_that_have_join_with_element_not_in_the_same_column() {
        //+----------------------------+
        //|       step1  step2         |
        //|start                step3  |
        //|       step4                |
        //+----------------------------+
        //
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(start)
                .shape(step1)
                .shape(step2)
                .shape(step4)
                .shape(step3)
                .edge(edge(start, step1))
                .edge(edge(start, step4))
                .edge(edge(step1, step2))
                .edge(edge(step2, step3))
                .edge(edge(step4, step3))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(start, 0, 1),
                position(step1, 1, 0),
                position(step2, 2, 0),
                position(step4, 1, 2),
                position(step3, 3, 1))));
    }

    @Test
    public void should_layout_a_diagram_that_have_join_three_element() {
        // +--------------+
        // |step1         |
        // |step2  step4  |
        // |step3         |
        // +--------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .edge(edge(step1, step4))
                .edge(edge(step2, step4))
                .edge(edge(step3, step4))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(step1, 0, 0),
                position(step2, 0, 1),
                position(step3, 0, 2),
                position(step4, 1, 1))));
    }

    @Test
    public void should_layout_a_diagram_that_have_join_only_two_elements_of_three() {
        //  +--------------+
        //  |step1         |
        //  |step2         |
        //  |       step4  |
        //  |step3         |
        //  +--------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .edge(edge(step2, step4))
                .edge(edge(step3, step4))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(step1, 0, 0),
                position(step2, 0, 1),
                position(step3, 0, 3),
                position(step4, 1, 2))));
    }

    @Test
    public void toto() {
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .edge(edge(step2, step4))
                .edge(edge(step3, step4))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(step1, 0, 0))));
    }


    @Test
    public void should_compact_grid_in_order_to_remove_extra_space() {
        // without compacting
        //+-----------------------------------+
        //|       step1                       |
        //|                     step4         |
        //|start         step3         end    |
        //|                     step5         |
        //|       step2                       |
        //+-----------------------------------+
        //
        // with compacting
        //+-----------------------------------+
        //|       step1         step4         |
        //|start         step3         end    |
        //|       step2         step5         |
        //+-----------------------------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(start)
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .shape(step5)
                .shape(end)
                .edge(edge(start, step1))
                .edge(edge(start, step2))
                .edge(edge(step1, step3))
                .edge(edge(step2, step3))
                .edge(edge(step3, step4))
                .edge(edge(step3, step5))
                .edge(edge(step4, end))
                .edge(edge(step5, end))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(start, 0, 1),
                position(step1, 1, 0),
                position(step2, 1, 2),
                position(step3, 2, 1),
                position(step4, 3, 0),
                position(step5, 3, 2),
                position(end, 4, 1))));
    }


    @Test
    public void should_not_overlap_joins() {
        //+---------------------+
        //|step1                |
        //|step2  step4         |
        //|              end    |
        //|       step5         |
        //|step3                |
        //+---------------------+
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(step4)
                .shape(step5)
                .shape(end)
                .edge(edge(step1, step4))
                .edge(edge(step2, step5))
                .edge(edge(step3, step4))
                .edge(edge(step4, end))
                .edge(edge(step5, end))
                .build();


        Grid grid = shapeLayouter.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo(toAscii(Grid.of(
                position(step1, 0, 0),
                position(step2, 0, 1),
                position(step3, 0, 4),
                position(step4, 1, 1),
                position(step5, 1, 3),
                position(end, 2, 2))));
    }

}