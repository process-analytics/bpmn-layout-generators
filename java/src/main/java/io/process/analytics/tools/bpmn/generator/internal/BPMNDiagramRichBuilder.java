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
package io.process.analytics.tools.bpmn.generator.internal;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Optional;

/**
 * Helper to build a BPMNDiagram based on existing TDefinitions semantic part
 */
@RequiredArgsConstructor
public class BPMNDiagramRichBuilder {

    @NonNull
    private final TDefinitions definitions;

    // TODO make this internal and set the diagram to a field of this class
    public BPMNDiagram initializeBPMNDiagram() {
        BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId("BPMNDiagram_1");

        BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnDiagram.setBPMNPlane(bpmnPlane);
        bpmnPlane.setId("BPMNPlane_1");
        putBpmnElement(bpmnPlane, computeBPMNPlaneBpmnElementId());

        return bpmnDiagram;
    }

    public void addNode() {
        // TODO add node as bpmn shape to the initialized bpmn diagram
    }

    public void addEdge() {
        // TODO add edge as bpmn edge to the initialized bpmn diagram
    }

    public TDefinitions build() {
        // TODO it should be better to clone the initial definitions
        List<BPMNDiagram> diagrams = definitions.getBPMNDiagram();
        diagrams.clear();
        diagrams.add(initializeBPMNDiagram());
        return definitions;
    }

    // TODO we need to know if this is a process or a collaboration to set the actual qname
    private void putBpmnElement(BPMNPlane bpmnPlane, String bpmnElementRef) {
        QName qName = new QName(XMLConstants.NULL_NS_URI, bpmnElementRef);
        bpmnPlane.setBpmnElement(qName);
    }

    // TODO move to the Semantic class?
    // bpmn element value depends on semantic collaboration existence
    // if no collaboration, there is a single process, so use its id
    private String computeBPMNPlaneBpmnElementId() {
        final Semantic semantic = new Semantic(definitions);
        Optional<TCollaboration> collaboration = semantic.getCollaboration();
        // TODO empty processes is supposed to be managed by Semantic
        return collaboration.map(TBaseElement::getId)
                .orElseGet(() -> semantic.getProcesses().get(0).getId());
    }

}
