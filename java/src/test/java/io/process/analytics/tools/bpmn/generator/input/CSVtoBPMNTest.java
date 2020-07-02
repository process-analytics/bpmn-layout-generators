package io.process.analytics.tools.bpmn.generator.input;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.Semantic.BpmnElements;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
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
        TProcess process = processes.get(0);
        BpmnElements bpmnElements = semantic.getBpmnElements(process);
        List<? extends TFlowElement> flowNodes = bpmnElements.getFlowNodes();
        assertThat(flowNodes).hasSize(9);
        TFlowElement flowElement0 = flowNodes.get(0);
        assertThat(flowElement0.getId()).isEqualTo("1");
        // TODO double quote should be removed
        assertThat(flowElement0.getName()).isEqualTo("\"End\"");
        // TODO this should probably be TTask (BPMN abstract task)
        assertThat(flowElement0).isExactlyInstanceOf(TUserTask.class);

        List<? extends TSequenceFlow> sequenceFlows = bpmnElements.getSequenceFlows();
        assertThat(sequenceFlows).hasSize(13);
        TSequenceFlow sequenceFlow12 = sequenceFlows.get(12);
        assertThat(sequenceFlow12.getId()).isEqualTo("13");
        assertThat(sequenceFlow12.getSourceRef()).isEqualTo("9");
        assertThat(sequenceFlow12.getTargetRef()).isEqualTo("5");
    }

    // TODO the underlying method read lines then join. The CSVtoBPMN spit the string into lines! Useless work!
    private static String readCsvFile(String csvFilePath) throws IOException {
        return fileContent(new File(csvFilePath));
    }
}