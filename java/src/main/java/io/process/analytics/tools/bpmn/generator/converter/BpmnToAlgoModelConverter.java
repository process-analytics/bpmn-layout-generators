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

import static io.process.analytics.tools.bpmn.generator.internal.Semantic.getId;
import static io.process.analytics.tools.bpmn.generator.model.ShapeType.*;

import java.util.List;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.ShapeType;

public class BpmnToAlgoModelConverter {

    public Diagram toAlgoModel(TDefinitions definitions) {
        Semantic semantic = new Semantic(definitions);
        Diagram.DiagramBuilder diagram = Diagram.builder();

        List<TProcess> processes = semantic.getProcesses();
        for (TProcess process : processes) {
            Semantic.BpmnElements bpmnElements = semantic.getBpmnElements(process);

            bpmnElements.getFlowNodes().stream()
                    .map(BpmnToAlgoModelConverter::toShape)
                    .forEach(diagram::shape);

            bpmnElements.getSequenceFlows().stream()
                    .map(seqFlow -> Edge.edge(seqFlow.getId(), getId(seqFlow.getSourceRef()), getId(seqFlow.getTargetRef())))
                    .forEach(diagram::edge);
        }

        return diagram.build();
    }

    // visible for testing
    static Shape toShape(TFlowElement flowNode) {
        ShapeType shapeType = ACTIVITY;
        if (flowNode instanceof TGateway) {
            shapeType = GATEWAY;
        } else if (flowNode instanceof TEvent) {
            shapeType = EVENT;
        }
        return new Shape(flowNode.getId(), flowNode.getName(), shapeType);
    }

}
