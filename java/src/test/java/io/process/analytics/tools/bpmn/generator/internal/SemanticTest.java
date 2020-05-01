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
package io.process.analytics.tools.bpmn.generator.internal;

import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import org.junit.jupiter.api.Test;

public class SemanticTest {

    @Test
    public void detect_collaboration_when_exist_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile("src/test/resources/bpmn/01-startEvent.bpmn.xml");
        assertThat(semantic.getCollaboration())
                .get()
                .hasFieldOrPropertyWithValue("id", "Collaboration_1cajy2f");
    }

    @Test
    public void detect_collaboration_when_not_exist_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile(
                "src/test/resources/bpmn/02-startEvent_task_endEvent-without-collaboration.bpmn.xml");
        assertThat(semantic.getCollaboration()).isEmpty();
    }

    @Test
    public void detect_processes_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile("src/test/resources/bpmn/01-startEvent.bpmn.xml");
        assertThat(semantic.getProcesses())
                .hasSize(1)
                .extracting(TProcess::getId).containsExactly("Process_1duwsyj");
    }

    @Test
    public void detect_bpmn_elements_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile(
                "src/test/resources/bpmn/02-startEvent_task_endEvent-without-collaboration.bpmn.xml");
        // we know that there is a process (see other tests)
        TProcess process = semantic.getProcesses().get(0);

        Semantic.BpmnElements bpmnElements = semantic.getBpmnElements(process);

        assertThat(bpmnElements.getFlowNodes())
                .extracting(Object::getClass, TFlowElement::getId)
                .containsOnly(tuple(TStartEvent.class, "startEvent_1"),
                        tuple(TTask.class, "task_1"),
                        tuple(TEndEvent.class, "endEvent_1"));
        assertThat(bpmnElements.getSequenceFlows())
                .extracting(Object::getClass, TFlowElement::getId)
                .containsOnly(tuple(TSequenceFlow.class, "sequenceFlow_1"),
                        tuple(TSequenceFlow.class, "sequenceFlow_2"));
    }

    public static TDefinitions definitionsFromBpmnFile(String bpmnFilePath) {
        return defaultBpmnInOut().readFromBpmn(new File(bpmnFilePath));
    }

    private static Semantic semanticFromBpmnFile(String bpmnFilePath) {
        return new Semantic(definitionsFromBpmnFile(bpmnFilePath));
    }

}
