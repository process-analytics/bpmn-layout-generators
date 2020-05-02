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
package io.process.analytics.tools.bpmn.generator.converter;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Diagram.DiagramBuilder;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Shape;

import java.util.List;

public class BpmnToAlgoModelConverter {

    public Diagram toAlgoModel(TDefinitions definitions) {
        Semantic semantic = new Semantic(definitions);
        DiagramBuilder diagram = Diagram.builder();

        List<TProcess> processes = semantic.getProcesses();
        for (TProcess process : processes) {
            Semantic.BpmnElements bpmnElements = semantic.getBpmnElements(process);

            bpmnElements.getFlowNodes().stream()
                    .map(flowNode -> new Shape(flowNode.getId(), flowNode.getName()))
                    .forEach(diagram::shape);

            bpmnElements.getSequenceFlows().stream()
                    .map(seqFlow -> Edge.edge(getId(seqFlow.getSourceRef()), getId(seqFlow.getTargetRef())))
                    .forEach(diagram::edge);
        }

        return diagram.build();
    }

    // assuming this is a TBaseElement
    private static String getId(Object object) {
        return ((TBaseElement) object).getId();
    }

}
