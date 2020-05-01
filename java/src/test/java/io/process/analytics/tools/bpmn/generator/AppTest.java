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

import io.process.analytics.tools.bpmn.generator.internal.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    @Test
    public void main_generates_xml_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.xml");
        generate("src/test/resources/bpmn/A.2.0.bpmn.xml", outputPath);

        assertThat(new File(outputPath)).exists().isFile();
        // TODO add xml assertions (assertj and/or xmlunit)
    }

    @Test
    public void main_generates_svg_output_file() throws Exception {
        String outputPath = outputPath("A.2.0_with_diagram.bpmn.svg");
        generate("src/test/resources/bpmn/A.2.0.bpmn.xml", outputPath);

        File svgFile = new File(outputPath);
        assertThat(svgFile).exists().isFile();
        assertThat(fileContent(svgFile)).contains("<svg xmlns=\"http://www.w3.org/2000/svg\"");
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static String outputPath(String fileName) {
        return "target/test/output/AppTest/" + fileName;
    }

    private static void generate(String input, String output) throws Exception {
        App.main(new String[] { input, output });
    }

}
