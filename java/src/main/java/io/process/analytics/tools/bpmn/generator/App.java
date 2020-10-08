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

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import io.process.analytics.tools.bpmn.generator.BPMNLayoutGenerator.ExportType;
import io.process.analytics.tools.bpmn.generator.internal.FileUtils;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Log4j2
@Command(name = "App", mixinStandardHelpOptions = true)
public class App implements Callable<Integer> {


    @Option(names = {"-t", "--input-type"},
            description = "BPMN or CSV.",
            paramLabel = "TYPE")
    String inputType = "BPMN";
    @Option(names = {"-u", "--output-type"},
            description = "BPMN, SVG or ASCII.",
            paramLabel = "TYPE")
    String outputType = "BPMN";

    @Option(names = {"-o", "--output"},
            description = "Output file.",
            paramLabel = "OUTPUT")
    private File outputFile;
    @Parameters(arity = "1..2", paramLabel = "INPUT", description = "Input file(s).")
    private File[] inputFiles;

    public static void main(String[] args) throws Exception {

        int exitCode = runApp(args);
        System.exit(exitCode);
    }

    static int runApp(String... args) {
        return new CommandLine(new App()).execute(args);
    }


    private App() {
    }

    @Override
    public Integer call() {
        try {
            BPMNLayoutGenerator bpmnLayoutGenerator = new BPMNLayoutGenerator();
            String output;
            switch (inputType) {
                case "BPMN":
                    if (inputFiles.length != 1) {
                        System.err.println("Expected only one input file to import from BPMN format, got: " + inputType.length());
                    }
                    output = bpmnLayoutGenerator.generateLayoutFromBPMNSemantic(FileUtils.fileContent(inputFiles[0]), exportType(outputType));
                    break;
                case "CSV":
                    if (inputFiles.length != 2) {
                        System.err.println("Expected 2 input files to import from CSV format, got: " + inputType.length());
                    }
                    output = bpmnLayoutGenerator.generateLayoutFromCSV(FileUtils.fileContent(inputFiles[0]), FileUtils.fileContent(inputFiles[1]), exportType(outputType));
                    break;
                default:
                    System.err.println("Unexpected input type: " + inputType);
                    return 2;
            }
            if (outputFile != null) {
                FileUtils.touch(outputFile);
                Files.write(outputFile.toPath(), output.getBytes());
            } else {
                System.out.println(output);
            }
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + e.getMessage());
            return 2;
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return 1;
        }
        return 0;
    }


    private ExportType exportType(String arg) {
        try {
            return ExportType.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    format("Invalid export type: %s. Must be one of [%s]", arg,
                            stream(ExportType.values())
                                    .map(Enum::toString)
                                    .map(String::toLowerCase)
                                    .collect(joining(", "))));
        }
    }

}
