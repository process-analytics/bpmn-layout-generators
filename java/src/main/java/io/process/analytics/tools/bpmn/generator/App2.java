package io.process.analytics.tools.bpmn.generator;

import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.createParents;
import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class App2 {

    public static void main(String[] args) throws IOException {
        File nodeDiscovery = new File(args[0]);
        File edgeDiscovery = new File(args[1]);
        File outputFile = new File(args[2]);

        log.info("Converting CSV into internal model");
        String bpmn = new BPMNLayoutGenerator().generateLayoutFromCSV(fileContent(nodeDiscovery), fileContent(edgeDiscovery), BPMNLayoutGenerator.ExportType.BPMN);
        createParents(outputFile);
        Files.write(outputFile.toPath(), bpmn.getBytes());
    }

}
