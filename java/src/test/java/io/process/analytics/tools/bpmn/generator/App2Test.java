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
package io.process.analytics.tools.bpmn.generator;

import static io.process.analytics.tools.bpmn.generator.App2.main;
import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class App2Test {

    @Test
    public void main_generates_bpmn_with_simple_discovery_data() throws Exception {
        String outputPath = outputPath("from_simple_csv_diagram.bpmn.xml");
        generateToBpmn(inputPath("PatientsProcess/nodeSimple.csv"), inputPath("PatientsProcess/edgeSimple.csv"), outputPath);

        assertOutFile(outputPath);
    }

    @Test
    public void main_generates_bpmn_with_patients_process_discovery_data() throws Exception {
        String outputPath = outputPath("from_patients_csv_diagram.bpmn.xml");
        generateToBpmn(inputPath("PatientsProcess/node.csv"), inputPath("PatientsProcess/edge.csv"), outputPath);

        assertOutFile(outputPath);
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static void assertOutFile(String outputPath) throws IOException {
        File bpmnFile = new File(outputPath);
        assertThat(bpmnFile).exists().isFile();
        assertThat(fileContent(bpmnFile)).contains("<semantic:definitions xmlns:semantic=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"");
    }

    private static String inputPath(String fileName) {
        return "src/test/resources/csv/" + fileName;
    }

    private static String outputPath(String fileName) {
        return "target/test/output/App2Test/" + fileName;
    }

    private static void generateToBpmn(String nodeDiscovery, String edgeDiscovery, String output) throws Exception {
        main(new String[] { nodeDiscovery, edgeDiscovery, output});
    }

}
