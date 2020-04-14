package io.process.analytics.tools.bpmn.generator.internal;

import io.process.analytics.tools.bpmn.generator.internal.model.BPMNDiagram;
import io.process.analytics.tools.bpmn.generator.internal.model.BPMNPlane;
import io.process.analytics.tools.bpmn.generator.internal.model.TCollaboration;
import io.process.analytics.tools.bpmn.generator.internal.model.TDefinitions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * Helper to build a BPMNDiagram based on existing TDefinitions semantic part
 */
@RequiredArgsConstructor
public class BPMNDiagramRichBuilder {

    @NonNull
    private final TDefinitions definitions;

    private void createBPMNDiagram() {

        BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId("BPMNDiagram_1");

        BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnPlane.setId("BPMNPlane_1");
        //        QName bpmnElement = bpmnPlane.getBpmnElement()
        //        bpmnElement.getid

    }

    // bpmn element value depends on semantic collaboration existence
    private String computeBPMNPlaneBpmnElementId() {
        Semantic semantic = new Semantic(definitions);
        Optional<TCollaboration> collaboration = semantic.getCollaboration();
        // TODO refactor
        if (collaboration.isPresent()) {
            String bpmnElementId = collaboration.get().getId();

        } else {
            // get the single process

        }
        return null;
    }

}
