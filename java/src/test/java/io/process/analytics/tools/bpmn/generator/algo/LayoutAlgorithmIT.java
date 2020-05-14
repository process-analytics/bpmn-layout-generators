package io.process.analytics.tools.bpmn.generator.algo;

import static io.process.analytics.tools.bpmn.generator.export.ASCIIExporter.toAscii;
import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static io.process.analytics.tools.bpmn.generator.model.Position.position;
import static io.process.analytics.tools.bpmn.generator.model.Shape.shape;
import static org.assertj.core.api.Assertions.assertThat;

import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import org.junit.jupiter.api.Test;

public class LayoutAlgorithmIT {

    Shape start = shape("start");
    Shape step1 = shape("step1");
    Shape step2 = shape("step2");
    Shape step3 = shape("step3");
    Shape step4 = shape("step4");
    Shape step5 = shape("step5");
    Shape end = shape("end");

    private LayoutAlgorithm layoutAlgorithm = new LayoutAlgorithm();

    @Test
    public void should_layout_diagram_with_cycle() {
        //+-----------------------------------+
        //|              step3                |
        //|start  step1         step2  end    |
        //+-----------------------------------+
        Diagram diagram = Diagram.builder()
                .shape(start)
                .shape(step1)
                .shape(step2)
                .shape(step3)
                .shape(end)
                .edge(edge(start, step1))
                .edge(edge(step1, step2))
                .edge(edge(step2, step3))
                .edge(edge(step3, step1))
                .edge(edge(step2, end))
                .build();


        Grid grid = layoutAlgorithm.layout(diagram);

        assertThat(toAscii(grid)).isEqualTo("Diagram:\n" +
                "+-----------------------------------+\n" +
                "|              step3                |\n" +
                "|start  step1         step2  end    |\n" +
                "+-----------------------------------+\n");
    }
}
