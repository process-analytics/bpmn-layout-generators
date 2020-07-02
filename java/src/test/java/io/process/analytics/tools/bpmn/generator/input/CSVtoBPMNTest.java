package io.process.analytics.tools.bpmn.generator.input;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TProcess;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;
import static org.assertj.core.api.Assertions.assertThat;

class CSVtoBPMNTest {
    @Test
    public void should_convert_csv_to_bpmn() throws IOException {
        String edge = readCsvFile("src/test/resources/csv/PatientsProcess/edge.csv");
        String node = readCsvFile("src/test/resources/csv/PatientsProcess/node.csv");

        TDefinitions bpmn = new CSVtoBPMN().readFromCSV(node, edge);
        Semantic semantic = new Semantic(bpmn);

        List<TProcess> processes = semantic.getProcesses();
        assertThat(processes).hasSize(1);
        TProcess tProcess = processes.get(0);
        Semantic.BpmnElements bpmnElements = semantic.getBpmnElements(tProcess);
        assertThat(bpmnElements.getFlowNodes()).hasSize(9);
        assertThat(bpmnElements.getSequenceFlows()).hasSize(13);
    }

    // TODO the underlying method read lines then join. The CSVtoBPMN spit the string into lines! Useless work!
    private static String readCsvFile(String csvFilePath) throws IOException {
        return fileContent(new File(csvFilePath));
    }
}