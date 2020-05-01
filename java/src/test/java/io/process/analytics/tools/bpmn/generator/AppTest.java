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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    @Test
    @Disabled("not implemented yet")
    public void main_generates_xml_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.xml");
        generateToBpmn(inputPath("A.2.0.bpmn.xml"), outputPath);

        File bpmnFile = new File(outputPath);
        assertThat(bpmnFile).exists().isFile();
        // TODO add xml assertions (assertj and/or xmlunit)
        assertThat(fileContent(bpmnFile)).contains("BPMN Baby!!");
    }

    @Test
    public void main_generates_svg_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.svg");
        generateToSvg(inputPath("A.2.0.bpmn.xml"), outputPath);

        File svgFile = new File(outputPath);
        assertThat(svgFile).exists().isFile();
        assertThat(fileContent(svgFile)).contains("<svg xmlns=\"http://www.w3.org/2000/svg\"");
    }

    @Test
    public void main_generates_ascii_output_file() throws Exception {
        String outputPath = outputPath("02-startEvent_task_endEvent-without-collaboration.bpmn.txt");
        generateToAscii(inputPath("02-startEvent_task_endEvent-without-collaboration.bpmn.xml"), outputPath);

        File asciiFile = new File(outputPath);
        assertThat(asciiFile).exists().isFile();
        assertThat(fileContent(asciiFile)).contains("+---");
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static String inputPath(String fileName) {
        return "src/test/resources/bpmn/" + fileName;
    }

    private static String outputPath(String fileName) {
        return "target/test/output/AppTest/" + fileName;
    }

    private static void generateToBpmn(String input, String output) throws Exception {
        App.main(new String[] { input, output });
    }

    private static void generateToSvg(String input, String output) throws Exception {
        App.main(new String[] { input, output, "svg" });
    }

    private static void generateToAscii(String input, String output) throws Exception {
        App.main(new String[] { input, output, "ascii" });
    }

}
