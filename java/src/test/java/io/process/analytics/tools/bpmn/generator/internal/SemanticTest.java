package io.process.analytics.tools.bpmn.generator.internal;

import io.process.analytics.tools.bpmn.generator.internal.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.internal.model.TProcess;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;
import static org.assertj.core.api.Assertions.assertThat;

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