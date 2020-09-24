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

import static io.process.analytics.tools.bpmn.generator.App.runApp;
import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void main_fail_on_unexisting_input_file() throws Exception {
        int returnCode = runApp("input");

        assertThat(returnCode).isEqualTo(2);
    }

    @Test
    public void main_fail_on_wrong_export_type() throws Exception {

        int returnCode = runApp("input", "--output-type", "unknown_export_type");

        assertThat(returnCode).isEqualTo(2);
    }

    @Test
    public void main_generates_xml_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.xml");
        runApp(input("bpmn/A.2.0.bpmn.xml"), outputPath);

        File bpmnFile = new File(outputPath);
        assertThat(bpmnFile).exists().isFile();
        assertThat(fileContent(bpmnFile)).contains("<semantic:definitions").contains("xmlns:semantic=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"");
    }

    @Test
    public void main_generates_svg_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.svg");
        runApp(input("bpmn/A.2.0.bpmn.xml"), outputPath, "svg");

        File svgFile = new File(outputPath);
        assertThat(svgFile).exists().isFile();
        assertThat(fileContent(svgFile)).contains("<svg xmlns=\"http://www.w3.org/2000/svg\"");
    }

    @Test
    public void main_generates_ascii_output_file() throws Exception {
        String outputPath = outputPath("02-startEvent_task_endEvent-without-collaboration.bpmn.txt");
        runApp(input("csv/02-startEvent_task_endEvent-without-collaboration.bpmn.xml"), outputPath, "ascii");

        File asciiFile = new File(outputPath);
        assertThat(asciiFile).exists().isFile();
        assertThat(fileContent(asciiFile)).contains("+---");
    }

    @Test
    public void main_generates_bpmn_with_simple_discovery_data() throws Exception {
        String outputPath = outputPath("from_simple_csv_diagram.bpmn.xml");
        runApp("--input-type=CSV", "--output", outputPath, input("csv/PatientsProcess/nodeSimple.csv"), input("csv/PatientsProcess/edgeSimple.csv"));

        assertOutFile(outputPath);
    }

    @Test
    public void main_generates_bpmn_with_patients_process_discovery_data() throws Exception {
        String outputPath = outputPath("from_patients_csv_diagram.bpmn.xml");
        runApp("--input-type=CSV", "--output", outputPath, input("csv/PatientsProcess/node.csv"), input("csv/PatientsProcess/edge.csv"));

        assertOutFile(outputPath);
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static void assertOutFile(String outputPath) throws IOException {
        File bpmnFile = new File(outputPath);
        assertThat(bpmnFile).exists().isFile();
        assertThat(fileContent(bpmnFile)).contains("<semantic:definitions").contains("xmlns:semantic=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"");
    }


    private static String input(String fileName) {
        return "src/test/resources/" + fileName;
    }

    private static String outputPath(String fileName) {
        return "target/test/output/AppTest/" + fileName;
    }

}
