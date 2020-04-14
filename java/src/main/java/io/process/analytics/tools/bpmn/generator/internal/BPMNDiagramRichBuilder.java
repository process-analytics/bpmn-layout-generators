package io.process.analytics.tools.bpmn.generator.internal;

import io.process.analytics.tools.bpmn.generator.internal.model.*;
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

    public BPMNDiagram initializeBPMNDiagram() {
        BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId("BPMNDiagram_1");

        BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnDiagram.setBPMNPlane(bpmnPlane);
        bpmnPlane.setId("BPMNPlane_1");
        putBpmnElement(bpmnPlane, computeBPMNPlaneBpmnElementId());

        return bpmnDiagram;
    }

    // copied from the generated ObjectFactory
    private final static QName _Process_QNAME = new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "process");
    private final static QName _Collaboration_QNAME = new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "collaboration");

    // TODO we need to know if this is a process or a collaboration to set the actual qname
    private void putBpmnElement(BPMNPlane bpmnPlane, BpmnElementRef bpmnElementRef) {
        QName qName = bpmnElementRef.qName;
        bpmnPlane.setBpmnElement(qName);
        bpmnPlane.getOtherAttributes().put(qName, bpmnElementRef.ref);
    }

    // TODO move to the Semantic class?
    // bpmn element value depends on semantic collaboration existence
    // if no collaboration, there is a single process, so use its id
    private BpmnElementRef computeBPMNPlaneBpmnElementId() {
        final Semantic semantic = new Semantic(definitions);
        Optional<TCollaboration> collaboration = semantic.getCollaboration();
        // TODO empty processes is supposed to be managed by Semantic
        return collaboration.map(TBaseElement::getId)
                .map(id -> new BpmnElementRef(_Collaboration_QNAME, id))
                .orElseGet(() -> new BpmnElementRef(_Process_QNAME, semantic.getProcesses().get(0).getId()));
    }

    @RequiredArgsConstructor
    private static class BpmnElementRef {

        @NonNull
        private final QName qName;
        @NonNull
        private final String ref;
    }

}
