package io.process.analytics.tools.bpmn.generator;

import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;
import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;

import java.io.File;
import java.io.IOException;

import io.process.analytics.tools.bpmn.generator.input.CSVtoBPMN;
import io.process.analytics.tools.bpmn.generator.internal.BpmnInOut;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class App2 extends App {

    public App2(BpmnInOut bpmnInOut) {
        super(bpmnInOut);
    }

    public static void main(String[] args) throws IOException {
        File nodeDiscovery = new File(args[0]);
        File edgeDiscovery = new File(args[1]);
        File outputFile = new File(args[2]);

        new App2(defaultBpmnInOut()).process(nodeDiscovery, edgeDiscovery, outputFile);
    }

    private void process(File nodeDiscovery, File edgeDiscovery, File outputFile) throws IOException {
        log.info("Converting CSV into internal model");
        TDefinitions definitions = new CSVtoBPMN().readFromCSV(fileContent(nodeDiscovery), fileContent(edgeDiscovery));
        log.info("Conversion done");

        LayoutSortedDiagram layoutSortedDiagram = computeLayout(definitions);
        exportToBpmn(layoutSortedDiagram, outputFile);
    }

}
