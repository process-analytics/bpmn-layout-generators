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

import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.process.analytics.tools.bpmn.generator.algo.ShapeLayouter;
import io.process.analytics.tools.bpmn.generator.algo.ShapeSorter;
import io.process.analytics.tools.bpmn.generator.converter.BpmnToAlgoModelConverter;
import io.process.analytics.tools.bpmn.generator.export.ASCIIExporter;
import io.process.analytics.tools.bpmn.generator.export.SVGExporter;
import io.process.analytics.tools.bpmn.generator.internal.BpmnInOut;
import io.process.analytics.tools.bpmn.generator.internal.FileUtils;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class App {

    public static void main(String[] args) throws Exception {
        validate(args);
        File bpmnInputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        App app = new App();
        LayoutSortedDiagram diagram = app.loadAndLayout(bpmnInputFile);

        FileUtils.touch(outputFile);
        String exportType = exportType(args);
        log("Requested Export Type: " + exportType);
        switch (exportType) {
            case "ascii":
                app.exportToAscii(diagram, outputFile);
                break;
            case "bpmn":
                app.exportToBpmn(null);
                break;
            case "svg":
                app.exportToSvg(diagram, outputFile);
                break;
            default:
                throw new IllegalStateException("Unexpected Export Type: " + exportType);
        }
    }

    private static void validate(String[] args) {
        if (args.length <= 1) {
            throw new IllegalArgumentException("You must pass at least 2 arguments.");
        }
    }

    private static String exportType(String[] args) {
        String type = "bpmn";
        if (args.length > 2) {
            type = args[2];
        }
        return type;
    }

    private static void log(String message) {
        System.out.println(message);
    }

    @RequiredArgsConstructor
    @Getter
    private static class LayoutSortedDiagram {

        private final Grid grid;
        private final SortedDiagram sortedDiagram;
    }

    public LayoutSortedDiagram loadAndLayout(File bpmnInputFile) {
        log("Loading bpmn file: " + bpmnInputFile);
        BpmnInOut bpmnInOut = defaultBpmnInOut();
        TDefinitions definitions = bpmnInOut.readFromBpmn(bpmnInputFile);
        log("Loaded " + definitions);

        log("Converting BPMN into internal model");
        Diagram diagram = new BpmnToAlgoModelConverter().toAlgoModel(definitions);
        log("Conversion done");

        log("Sorting and generating Layout");
        SortedDiagram sortedDiagram = new ShapeSorter().sort(diagram);
        Grid grid = new ShapeLayouter().layout(sortedDiagram);
        log("Sort and Layout done");

        return new LayoutSortedDiagram(grid, sortedDiagram);
    }

    private void exportToBpmn(TDefinitions tDefinitions) {
        log("export to bpmn NOT IMPLEMENTED");
//        log("Building bpmn diagram elements");
//        TDefinitions builtDefinitions = new BPMNDiagramRichBuilder(tDefinitions).build();
//        log("Bpmn diagram elements have been built");
//        File outputBpmnFile = new File(args[1]);
//        bpmnInOut.writeToBpmnFile(builtDefinitions, outputBpmnFile);
//        log("New file with bpmn diagram generated to " + outputBpmnFile.getAbsolutePath());
    }

    private void exportToSvg(LayoutSortedDiagram diagram, File outputFile) throws IOException {
        log("Exporting to SVG");
        byte[] svgContent = new SVGExporter().export(diagram.getGrid(), diagram.getSortedDiagram());
        Files.write(outputFile.toPath(), svgContent);
        log("SVG exported to " + outputFile);
    }

    private void exportToAscii(LayoutSortedDiagram diagram, File outputFile) throws IOException {
        log("Exporting to ASCII file");
        String asciiContent = new ASCIIExporter().export(diagram.getGrid());
        Files.write(outputFile.toPath(), asciiContent.getBytes(StandardCharsets.UTF_8));
        log("ASCII exported to " + outputFile);
    }

}
