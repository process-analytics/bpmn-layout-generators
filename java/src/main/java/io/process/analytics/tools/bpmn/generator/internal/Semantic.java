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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

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

    public List<TParticipant> getParticipants() {
        return getCollaboration()
                .map(TCollaboration::getParticipant)
                .orElseGet(Collections::emptyList);
    }

    public Optional<TCollaboration> getCollaboration() {
        List<TCollaboration> collaborations = definitions.getRootElement().stream()
                .map(JAXBElement::getValue)
                .filter(TCollaboration.class::isInstance)
                .map(o -> (TCollaboration) o)
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
                .map(o -> (TProcess) o)
                .collect(Collectors.toList());
    }

    public List<String> getBpmnElements(TProcess process) {
        // TODO manage TLaneSet
//        List<TLaneSet> laneSet = process.getLaneSet();
//        // there is always a TLaneSet (see BPMN spec)
//        List<TLane> lanes = laneSet.get(0).getLane();
//        // TODO do this for all lanes
//        // TODO have a look at childLaneSet
//        List<JAXBElement<Object>> flowNodeRef = lanes.get(0).getFlowNodeRef();


        List<JAXBElement<? extends TFlowElement>> flowElements = process.getFlowElement();
        // TODO filter nodes and edges



        return null;
    }
}
