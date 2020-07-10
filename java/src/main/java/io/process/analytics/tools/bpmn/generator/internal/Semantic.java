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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Helper to access to the BPMN semantic part
 */
@RequiredArgsConstructor
public class Semantic {

    @NonNull
    @Getter
    private final TDefinitions definitions;

    // assuming this is a TBaseElement
    public static String getId(Object object) {
        return ((TBaseElement) object).getId();
    }

    public List<TParticipant> getParticipants() {
        return getCollaboration()
                .map(TCollaboration::getParticipant)
                .orElseGet(Collections::emptyList);
    }

    public Optional<TCollaboration> getCollaboration() {
        List<TCollaboration> collaborations = definitions.getRootElement().stream()
                .map(JAXBElement::getValue)
                .filter(TCollaboration.class::isInstance)
                .map(TCollaboration.class::cast)
                .collect(Collectors.toList());

        // TODO check at most 1 otherwise error
        // TODO refactor into a more functional way
        if (collaborations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(collaborations.get(0));
    }

    public List<TProcess> getProcesses() {
        // TODO manage no process (should not occurred)
        return definitions.getRootElement().stream()
                .map(JAXBElement::getValue)
                .filter(TProcess.class::isInstance)
                .map(TProcess.class::cast)
                .collect(Collectors.toList());
    }

    // TODO manage lanes
    public BpmnElements getBpmnElements(TProcess process) {
        // TODO manage TLaneSet
//        List<TLaneSet> laneSet = process.getLaneSet();
//        // there is always a TLaneSet (see BPMN spec)
//        List<TLane> lanes = laneSet.get(0).getLane();
//        // TODO do this for all lanes
//        // TODO have a look at childLaneSet
//        List<JAXBElement<Object>> flowNodeRef = lanes.get(0).getFlowNodeRef();


        List<JAXBElement<? extends TFlowElement>> flowElements = process.getFlowElement();
        List<? extends TFlowElement> flowNodes = flowElements.stream()
                .map(JAXBElement::getValue)
                .filter(TFlowNode.class::isInstance)
                .map(TFlowNode.class::cast)
                .collect(Collectors.toList());

        List<? extends TSequenceFlow> sequenceFlows = flowElements.stream()
                .map(JAXBElement::getValue)
                .filter(TSequenceFlow.class::isInstance)
                .map(TSequenceFlow.class::cast)
                .collect(Collectors.toList());

        return new BpmnElements(flowNodes, sequenceFlows);
    }

    public void add(TProcess process) {
        definitions.getRootElement()
                .add(new JAXBElement<>(bpmnElementQName("process"), TProcess.class, null, process));
    }

    public static void addFlowElements(TProcess process, Collection<TFlowElement> flowElements) {
        flowElements.stream()
                // TODO name should be set accordingly to the type of the flow element
                .map(f -> new JAXBElement<>(bpmnElementQName("userTask"), TFlowElement.class, null, f))
                .forEach(f -> process.getFlowElement().add(f));
    }

    public static void addSequenceFlowElements(TProcess process, Collection<TSequenceFlow> sequenceFlowElements) {
        sequenceFlowElements.stream()
                .map(e -> new JAXBElement<>(bpmnElementQName("sequenceFlow"), TSequenceFlow.class, null, e))
                .forEach(e -> process.getFlowElement().add(e));
    }

    private static QName bpmnElementQName(String bpmnElement) {
        return new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", bpmnElement, "");
    }

    @RequiredArgsConstructor
    @Getter
    public static class BpmnElements {

        private final List<? extends TFlowElement> flowNodes;
        private final List<? extends TSequenceFlow> sequenceFlows;
    }

}
