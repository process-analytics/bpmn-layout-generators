/*
 * Copyright 2020 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.process.analytics.tools.bpmn.generator.converter;



import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import org.junit.jupiter.api.Test;

import static io.process.analytics.tools.bpmn.generator.internal.SemanticTest.definitionsFromBpmnFile;
import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static org.assertj.core.api.Assertions.assertThat;

class BpmnToAlgoModelConverterTest {

    @Test
    public void convertBpmnFlowNodesIntoDiagram() {
        //        <semantic:startEvent name="Start Event" id="startEvent_1">
        //            <semantic:outgoing>task_1</semantic:outgoing>
        //        </semantic:startEvent>
        //        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="Task 1" id="task_1">
        //            <semantic:incoming>startEvent_1</semantic:incoming>
        //            <semantic:outgoing>endEvent_1</semantic:outgoing>
        //        </semantic:task>
        //        <semantic:endEvent name="End Event" id="endEvent_1">
        //            <semantic:incoming>task_1</semantic:incoming>
        //        </semantic:endEvent>
        //        <semantic:sequenceFlow sourceRef="startEvent_1" targetRef="task_1" name="" id="sequenceFlow_1"/>
        //        <semantic:sequenceFlow sourceRef="task_1" targetRef="endEvent_1" name="" id="sequenceFlow_2"/>
        TDefinitions definitions = definitionsFromBpmnFile(
                "src/test/resources/bpmn/02-startEvent_task_endEvent-without-collaboration.bpmn.xml");

        Diagram diagram = new BpmnToAlgoModelConverter().toAlgoModel(definitions);

        assertThat(diagram.getShapes()).containsOnly(
                new Shape("startEvent_1", "Start Event"),
                new Shape("task_1", "Task 1"),
                new Shape("endEvent_1", "End Event"));
        assertThat(diagram.getEdges()).containsOnly(
                edge("startEvent_1", "task_1"),
                edge("task_1", "endEvent_1"));
    }

}
