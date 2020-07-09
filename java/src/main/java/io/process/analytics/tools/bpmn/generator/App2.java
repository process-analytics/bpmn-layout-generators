package io.process.analytics.tools.bpmn.generator;

import io.process.analytics.tools.bpmn.generator.algo.ShapeLayouter;
import io.process.analytics.tools.bpmn.generator.algo.ShapeSorter;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter;
import io.process.analytics.tools.bpmn.generator.converter.BpmnToAlgoModelConverter;
import io.process.analytics.tools.bpmn.generator.export.BPMNExporter;
import io.process.analytics.tools.bpmn.generator.input.CSVtoBPMN;
import io.process.analytics.tools.bpmn.generator.internal.BpmnInOut;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Grid;

import java.io.File;
import java.io.IOException;

import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;

public class App2 {
    public static void main(String[] args) throws IOException {

        File nodeDiscovery = new File(args[0]);
        File edgeDiscovery = new File(args[1]);
        File outputFile = new File(args[2]);

        TDefinitions tDefinitions = new CSVtoBPMN().readFromCSV(fileContent(nodeDiscovery), fileContent(edgeDiscovery));

        Diagram diagram = new BpmnToAlgoModelConverter().toAlgoModel(tDefinitions);
        Diagram sortedDiagram = new ShapeSorter().sort(diagram);
        Grid grid = new ShapeLayouter().layout(sortedDiagram);

        BPMNExporter bpmnExporter = new BPMNExporter(new AlgoToDisplayModelConverter());
        TDefinitions newDefinitions = bpmnExporter.export(tDefinitions, grid, diagram);
        BpmnInOut.defaultBpmnInOut().writeToBpmnFile(newDefinitions, outputFile);
    }
}
