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

import io.process.analytics.tools.bpmn.generator.internal.model.BPMNDiagram;
import io.process.analytics.tools.bpmn.generator.internal.model.BPMNPlane;
import io.process.analytics.tools.bpmn.generator.internal.model.TDefinitions;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.List;
import java.util.Map;

import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;
import static org.assertj.core.api.Assertions.assertThat;

class BPMNDiagramRichBuilderTest {

    @Test
    public void initializeBPMNDiagram_when_collaboration_exist_in_bpmn_file() {
        BPMNDiagramRichBuilder builder = newBuildFromBpmnFile("src/test/resources/bpmn/01-startEvent.bpmn.xml");

        BPMNDiagram bpmnDiagram = builder.initializeBPMNDiagram();
        BPMNPlane bpmnPlane = check(bpmnDiagram);

        Map<QName, String> attributes = bpmnPlane.getOtherAttributes();
        assertThat(attributes).containsValues("Collaboration_1cajy2f");
    }

    @Test
    public void initializeBPMNDiagram_when_collaboration_not_exist_in_bpmn_file() {
        BPMNDiagramRichBuilder builder = newBuildFromBpmnFile("src/test/resources/bpmn/02-startEvent_task_endEvent-without-collaboration.bpmn.xml");

        BPMNDiagram bpmnDiagram = builder.initializeBPMNDiagram();
        BPMNPlane bpmnPlane = check(bpmnDiagram);

        Map<QName, String> attributes = bpmnPlane.getOtherAttributes();
        assertThat(attributes).containsValues("process_1");
    }

    @Test
    public void build_should_initialize_BPMNDiagram() {
        BPMNDiagramRichBuilder builder = newBuildFromBpmnFile("src/test/resources/bpmn/A.2.0.bpmn.xml");

        TDefinitions definitions = builder.build();

        // TODO duplication with XmlParserTest + check method for id
        // Shapes
        List<BPMNDiagram> diagram = definitions.getBPMNDiagram();
        assertThat(diagram)
                .hasSize(1)
                .extracting(BPMNDiagram::getId).containsOnly("BPMNDiagram_1");
        BPMNPlane plane = diagram.get(0).getBPMNPlane();
        assertThat(plane.getId()).isEqualTo("BPMNPlane_1");
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    // always set like this
    private static BPMNPlane check(BPMNDiagram bpmnDiagram) {
        assertThat(bpmnDiagram.getId()).isEqualTo("BPMNDiagram_1");
        BPMNPlane bpmnPlane = bpmnDiagram.getBPMNPlane();
        assertThat(bpmnPlane.getId()).isEqualTo("BPMNPlane_1");
        return bpmnPlane;
    }

    private static BPMNDiagramRichBuilder newBuildFromBpmnFile(String bpmnFilePath) {
        return new BPMNDiagramRichBuilder(definitionsFromBpmnFile(bpmnFilePath));
    }

    private static TDefinitions definitionsFromBpmnFile(String bpmnFilePath) {
        return defaultBpmnInOut().readFromBpmn(new File(bpmnFilePath));
    }

}
