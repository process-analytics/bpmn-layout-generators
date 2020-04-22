package io.process.analytics.tools.bpmn.generator.algo;

import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static io.process.analytics.tools.bpmn.generator.model.Position.position;
import static io.process.analytics.tools.bpmn.generator.model.Shape.shape;
import static org.assertj.core.api.Assertions.assertThat;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ShapeLayouterTest {


    private ShapeLayouter shapeLayouter = new ShapeLayouter();

    Shape start = shape("start");
    Shape step1 = shape("step1");
    Shape step2 = shape("step2");
    Shape step3 = shape("step3");
    Shape step4 = shape("step4");
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

        assertThat(grid.width()).isEqualTo(2);
        assertThat(grid.height()).isEqualTo(1);
        assertThat(grid.getPositions()).containsExactly(position(step1, 0, 0), position(step2, 1, 0));
    }

    @Test
    public void should_layout_a_diagram_with_2_unlinked_node_on_a_grid() {
        SortedDiagram diagram = SortedDiagram.builder()
                .shape(step1)
                .shape(step2)
                .build();

        Grid grid = shapeLayouter.layout(diagram);

        assertThat(grid.width()).isEqualTo(1);
        assertThat(grid.height()).isEqualTo(2);
        assertThat(grid.getPositions()).containsExactly(position(step1, 0, 0), position(step2, 0, 1));
    }

    @Test
    @Disabled("Not yet implemented")
    public void should_layout_a_diagram_that_have_2_branches() {
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

        assertThat(grid.getPositions()).isNull();

    }
}