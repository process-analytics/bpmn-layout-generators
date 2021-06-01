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

import java.io.File;
import java.io.IOException;

import static io.process.analytics.tools.bpmn.generator.App.runApp;
import static io.process.analytics.tools.bpmn.generator.AppTest.*;
import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;
import static org.assertj.core.api.Assertions.assertThat;

public class AppFromBpmnTest {

    @Test
    public void main_generates_bpmn_output_file_for_A_2_0() throws Exception {
        runAndCheckBPMNGeneration("bpmn/A.2.0.bpmn.xml", "A.2.0_with_diagram.bpmn.xml");
    }

    @Test
    public void main_generates_bpmn_output_file_for_A_2_1() throws Exception {
        runAndCheckBPMNGeneration("bpmn/A.2.1.bpmn.xml", "A.2.1_with_diagram.bpmn.xml");
    }

    @Test
    public void main_generates_svg_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.svg");
        runApp(input("bpmn/A.2.0.bpmn.xml"), "--output-type=SVG", "-o", outputPath);

        assertSvgOutFile(outputPath);
    }

    @Test
    public void main_generates_ascii_output_file() throws Exception {
        String outputPath = outputPath("02-startEvent_task_endEvent-without-collaboration.bpmn.txt");
        runApp(input("bpmn/02-startEvent_task_endEvent-without-collaboration.bpmn.xml"), "--output-type=ASCII", "-o", outputPath);

        File asciiFile = new File(outputPath);
        assertThat(asciiFile).exists().isFile();
        assertThat(fileContent(asciiFile)).contains("+---");
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static void runAndCheckBPMNGeneration(String inputFileName, String outputFileName) throws IOException {
        String outputPath = outputPath(outputFileName);
        runApp(input(inputFileName), "-o", outputPath);
        assertBpmnOutFile(outputPath);
    }

    private static String outputPath(String fileName) {
        return "target/test/output/AppFromBpmnTest/" + fileName;
    }

}
