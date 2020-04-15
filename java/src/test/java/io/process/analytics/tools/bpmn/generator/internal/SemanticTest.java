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

import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TProcess;

class SemanticTest {

    @Test
    public void detect_collaboration_when_exist_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile("src/test/resources/bpmn/01-startEvent.bpmn.xml");
        assertThat(semantic.getCollaboration())
                .get()
                .hasFieldOrPropertyWithValue("id", "Collaboration_1cajy2f");
    }

    @Test
    public void detect_collaboration_when_not_exist_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile("src/test/resources/bpmn/02-startEvent_task_endEvent-without-collaboration.bpmn.xml");
        assertThat(semantic.getCollaboration()).isEmpty();
    }

    @Test
    public void detect_processes_in_bpmn_file() {
        Semantic semantic = semanticFromBpmnFile("src/test/resources/bpmn/01-startEvent.bpmn.xml");
        assertThat(semantic.getProcesses())
                .hasSize(1)
                .extracting(TProcess::getId).containsExactly("Process_1duwsyj");
    }

    private static Semantic semanticFromBpmnFile(String bpmnFilePath) {
        TDefinitions tDefinitions = defaultBpmnInOut().readFromBpmn(new File(bpmnFilePath));
        return new Semantic(tDefinitions);
    }

}