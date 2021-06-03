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

import static io.process.analytics.tools.bpmn.generator.internal.SemanticTest.definitionsFromBpmnFile;
import static io.process.analytics.tools.bpmn.generator.model.Edge.edge;
import static io.process.analytics.tools.bpmn.generator.model.Shape.shape;
import static io.process.analytics.tools.bpmn.generator.model.ShapeType.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Shape;

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
                shape("startEvent_1", "Start Event", EVENT),
                shape("task_1", "Task 1", ACTIVITY),
                shape("endEvent_1", "End Event", EVENT));
        assertThat(diagram.getEdges()).containsOnly(
                edge("sequenceFlow_1", "startEvent_1", "task_1"),
                edge("sequenceFlow_2", "task_1", "endEvent_1"));
    }

    @Test
    void should_convert_activities_to_shape() {
        assertThat(toShape(new TUserTask(), new TSendTask(), new TSubProcess()))
                .containsOnly(shape("bpmn_id", "bpmn_name", ACTIVITY));
    }

    private static Stream<Shape> toShape(TFlowNode... flowNodes) {
        return Stream.of(flowNodes).map(BpmnToAlgoModelConverterTest::setBaseFields)
                .map(BpmnToAlgoModelConverter::toShape);
    }

    private static TFlowNode setBaseFields(TFlowNode flowNode) {
        flowNode.setId("bpmn_id");
        flowNode.setName("bpmn_name");
        return flowNode;
    }

    @Test
    void should_convert_events_to_shape() {
        assertThat(toShape(new TStartEvent(), new TIntermediateCatchEvent(), new TEndEvent()))
                .containsOnly(shape("bpmn_id", "bpmn_name", EVENT));
    }

    @Test
    void should_convert_gateways_to_shape() {
        assertThat(toShape(new TParallelGateway(), new TComplexGateway()))
                .containsOnly(shape("bpmn_id", "bpmn_name", GATEWAY));
    }

}
