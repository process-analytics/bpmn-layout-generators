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

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    @Test
    public void main_generates_xml_output_file() {
        String output = "target/test/output/AppTest/A.2.0_with_diagram.bpmn.xml";
        generate("src/test/resources/bpmn/A.2.0.bpmn.xml", output);

        assertThat(new File(output)).exists();
        // TODO add xml assertions (assertj and/or xmlunit)
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static void generate(String input, String output) {
        App.main(new String[] { input, output });
    }

}
