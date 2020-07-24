/*
 * Copyright 2020 Bonitasoft S.A.
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

import static io.process.analytics.tools.bpmn.generator.BPMNLayoutGenerator.ExportType.BPMN;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.BPMNLayoutGenerator.ExportType;
import io.process.analytics.tools.bpmn.generator.internal.FileUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class App {

    public static void main(String[] args) throws Exception {
        new App(Arrays.asList(args)).run();
    }

    private List<String> args;
    BPMNLayoutGenerator bpmnLayoutGenerator = new BPMNLayoutGenerator();

    public App(List<String> args) {
        this.args = args;
    }

    private void run() throws IOException {
        validate(args);
        File bpmnInputFile = new File(args.get(0));
        File outputFile = new File(args.get(1));
        ExportType exportType = exportType(args);
        String bpmn = bpmnLayoutGenerator.generateLayoutFromBPMNSemantic(FileUtils.fileContent(bpmnInputFile), exportType);
        FileUtils.createParents(outputFile);
        Files.write(outputFile.toPath(), bpmn.getBytes());

    }

    private void validate(List<String> args) {
        if (args.size() <= 1) {
            throw new IllegalArgumentException("You must pass at least 2 arguments");
        }
    }
    private ExportType exportType(List<String> args) {
        ExportType type = BPMN;
        if (args.size() > 2) {
            try {
                type = ExportType.valueOf(args.get(2).toUpperCase());
            } catch (IllegalArgumentException e) { throw new IllegalArgumentException(
                    format("Invalid export type: %s. Must be one of [%s]", args.get(2),
                            stream(ExportType.values())
                            .map(Enum::toString)
                            .map(String::toLowerCase)
                            .collect(joining(", "))));
            }
        }
        return type;
    }

}
