/*
 * Copyright 2021 Bonitasoft S.A.
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

import org.junit.jupiter.api.Test;

import static io.process.analytics.tools.bpmn.generator.App.runApp;
import static io.process.analytics.tools.bpmn.generator.AppTest.assertBpmnOutFile;
import static io.process.analytics.tools.bpmn.generator.AppTest.input;

public class AppFromCsvTest {

    @Test
    public void main_generates_bpmn_with_simple_discovery_data() throws Exception {
        String outputPath = outputPath("from_simple_csv_diagram.bpmn.xml");
        runApp("--input-type=CSV", "--output", outputPath, input("csv/PatientsProcess/nodeSimple.csv"), input("csv/PatientsProcess/edgeSimple.csv"));

        assertBpmnOutFile(outputPath);
    }

    @Test
    public void main_generates_bpmn_with_gateways() throws Exception {
        String outputPath = outputPath("from_csv_with_gateways.bpmn");
        runApp("--input-type=CSV", "--output", outputPath, input("csv/PatientsProcess/gateway_node_simple.csv"), input("csv/PatientsProcess/gateway_edge_simple.csv"));

        assertBpmnOutFile(outputPath);
    }

    @Test
    public void main_generates_bpmn_with_patients_process_discovery_data() throws Exception {
        String outputPath = outputPath("from_patients_csv_diagram.bpmn.xml");
        runApp("--input-type=CSV", "--output", outputPath, input("csv/PatientsProcess/node.csv"), input("csv/PatientsProcess/edge.csv"));

        assertBpmnOutFile(outputPath);
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static String outputPath(String fileName) {
        return "target/test/output/AppFromCsvTest/" + fileName;
    }

}
