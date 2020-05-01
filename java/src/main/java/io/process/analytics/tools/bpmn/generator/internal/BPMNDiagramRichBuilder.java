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

import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayDimension;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayEdge;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayFlowNode;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Helper to build a BPMNDiagram based on existing TDefinitions semantic part
 */
@RequiredArgsConstructor
public class BPMNDiagramRichBuilder {

    @NonNull
    private final TDefinitions definitions;

    private final List<BPMNShape> bpmnShapes = new ArrayList<>();
    private final List<BPMNEdge> bpmnEdges = new ArrayList<>();

    // visible for testing
    BPMNDiagram initializeBPMNDiagram() {
        BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId("BPMNDiagram_1");

        BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnDiagram.setBPMNPlane(bpmnPlane);
        bpmnPlane.setId("BPMNPlane_1");
        putBpmnElement(bpmnPlane, computeBPMNPlaneBpmnElementId());

        return bpmnDiagram;
    }

    public void addFlowNode(DisplayFlowNode flowNode) {
        BPMNShape bpmnShape = new BPMNShape();
        bpmnShape.setId("Shape_" + generateRandomId());
        putBpmnElement(bpmnShape, flowNode.bpmnElementId);

        bpmnShape.setBounds(bounds(flowNode.dimension));

        BPMNLabel label = new BPMNLabel();
        label.setBounds(bounds(flowNode.label.dimension));
        // TODO add label style
        bpmnShape.setBPMNLabel(label);

        bpmnShapes.add(bpmnShape);
    }

    public void addEdge(DisplayEdge edge) {
        BPMNEdge bpmnEdge = new BPMNEdge();
        bpmnEdge.setId("Edge_" + generateRandomId());
        putBpmnElement(bpmnEdge, edge.bpmnElementId);

        bpmnEdges.add(bpmnEdge);
    }

    private static Bounds bounds(DisplayDimension dimension) {
        Bounds bounds = new Bounds();
        bounds.setX(dimension.x);
        bounds.setY(dimension.y);
        bounds.setWidth(dimension.width);
        bounds.setHeight(dimension.height);
        return bounds;
    }

// TODO duplicated with Shape
    private static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    private static void putBpmnElement(BPMNShape bpmnShape, String bpmnElementRef) {
        QName qName = new QName(XMLConstants.NULL_NS_URI, bpmnElementRef);
        bpmnShape.setBpmnElement(qName);
    }

    private static void putBpmnElement(BPMNEdge bpmnEdge, String bpmnElementRef) {
        QName qName = new QName(XMLConstants.NULL_NS_URI, bpmnElementRef);
        bpmnEdge.setBpmnElement(qName);
    }

    public TDefinitions build() {
        // TODO it should be better to clone the initial definitions
        List<BPMNDiagram> diagrams = definitions.getBPMNDiagram();
        diagrams.clear();
        BPMNDiagram bpmnDiagram = initializeBPMNDiagram();
        diagrams.add(bpmnDiagram);
        
        BPMNPlane bpmnPlane = bpmnDiagram.getBPMNPlane();
        List<JAXBElement<? extends DiagramElement>> diagramElements = bpmnPlane.getDiagramElement();


        QName bpmnShapeQname = new QName("http://www.omg.org/spec/BPMN/20100524/DI", "BPMNShape", "");
        bpmnShapes.stream()
                .map(s -> new JAXBElement<>(bpmnShapeQname, BPMNShape.class, null, s))
                .forEach(diagramElements::add);

        QName bpmnEdgeQname = new QName("http://www.omg.org/spec/BPMN/20100524/DI", "BPMNEdge", "");
        bpmnEdges.stream()
                .map(s -> new JAXBElement<>(bpmnEdgeQname, BPMNEdge.class, null, s))
                .forEach(diagramElements::add);

        return definitions;
    }

    // TODO we need to know if this is a process or a collaboration to set the actual QName
    private static void putBpmnElement(BPMNPlane bpmnPlane, String bpmnElementRef) {
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
