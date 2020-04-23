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

import io.process.analytics.tools.bpmn.generator.internal.BPMNDiagramRichBuilder;
import io.process.analytics.tools.bpmn.generator.internal.BpmnInOut;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;

public class App {

    public static void main(String[] args) throws Exception {
        validate(args);
        String inputBpmnFilePath = args[0];
        log("Loading bpmn file: " + inputBpmnFilePath);
        BpmnInOut bpmnInOut = defaultBpmnInOut();
        TDefinitions tDefinitions = bpmnInOut.readFromBpmn(new File(inputBpmnFilePath));
        log("Loaded " + tDefinitions);

        log("Building bpmn diagram elements");
        TDefinitions builtDefinitions = new BPMNDiagramRichBuilder(tDefinitions).build();
        log("Bpmn diagram elements have been built");
        File outputBpmnFile = new File(args[1]);
        bpmnInOut.writeToBpmnFile(builtDefinitions, outputBpmnFile);
        log("New file with bpmn diagram generated to " + outputBpmnFile.getAbsolutePath());




//        // This is an example usage of sort/layout algorithm + export to svg for testing purpose
//        Shape start = shape("start");
//        Shape step1 = shape("step1");
//        Shape step2 = shape("step2");
//        Shape step3 = shape("step3");
//        Shape step4 = shape("step4");
//        Shape step5 = shape("step5");
//        Shape end = shape("end");
//        Diagram diagram = Diagram.builder()
//                .shape(step1)
//                .shape(step3)
//                .shape(step2)
//                .shape(end)
//                .shape(step4)
//                .shape(step5)
//                .shape(start)
//                .edge(edge(start, step1))
//                .edge(edge(step1, step2))
//                .edge(edge(step3, step4))
//                .edge(edge(step5, step4))
//                .edge(edge(step2, step4))
//                .edge(edge(step4, end))
//                .build();
//
//
//
//        ShapeSorter shapeSorter = new ShapeSorter();
//        ShapeLayouter shapeLayouter = new ShapeLayouter();
//
//        SortedDiagram sortedDiagram = shapeSorter.sort(diagram);
//        Grid grid = shapeLayouter.layout(sortedDiagram);
//
//        SVGExporter svgExporter = new SVGExporter();
//
//        byte[] exportedFile = svgExporter.export(grid, sortedDiagram);
//
//        Files.write(Path.of("target", "test.svg"), exportedFile);

    }

    private static void validate(String[] args) {
        if (args.length <= 1) {
            throw new IllegalArgumentException("You must pass at least 2 arguments.");
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }

}
