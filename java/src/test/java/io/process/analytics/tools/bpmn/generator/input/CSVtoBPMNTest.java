package io.process.analytics.tools.bpmn.generator.input;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

class CSVtoBPMNTest {
    @Test
    public void should_convert_csv_to_bpmn() throws IOException {
        String edge = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "csv", "PatientsProcess", "edge.csv")));
        String node = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "csv", "PatientsProcess", "node.csv")));

        TDefinitions bpmn = new CSVtoBPMN().readFromCSV(node, edge);
        Semantic semantic = new Semantic(bpmn);

        assertThat(semantic.getProcesses()).hasSize(1);


    }
}