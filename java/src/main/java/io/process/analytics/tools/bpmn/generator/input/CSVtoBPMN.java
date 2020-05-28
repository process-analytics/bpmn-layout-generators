package io.process.analytics.tools.bpmn.generator.input;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.BPMNShape;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TFlowElement;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TUserTask;

import javax.xml.bind.JAXBElement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static io.process.analytics.tools.bpmn.generator.internal.BPMNDiagramRichBuilder.bpmnElementQName;

class CSVtoBPMN {
    public TDefinitions readFromCSV(String nodes, String edges){

        String[] lines = nodes.split("\n");
        List<TFlowElement> flowElements = new ArrayList<>();
        for (String line : lines) {
            String[] node = line.split(",");
            TFlowElement userTask = new TUserTask();
            userTask.setName(node[2]);
            userTask.setId(node[1]);
            flowElements.add(userTask);
        }
        for (TFlowElement flowElement : flowElements) {
            new JAXBElement<>(bpmnElementQName("TFlowElement"), TFlowElement.class, null, flowElement);
        }


        return new TDefinitions();
    }
}