package io.process.analytics.tools.bpmn.generator.input;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;

import javax.xml.bind.JAXBElement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static io.process.analytics.tools.bpmn.generator.internal.BPMNDiagramRichBuilder.bpmnElementQName;

class CSVtoBPMN {
    public TDefinitions readFromCSV(String nodes, String edges){

        String[] lines = nodes.split("\n");
        lines[0] = null;
        List<TFlowElement> flowElements = new ArrayList<>();
        for (String line : lines) {
            if(line == null){
                continue;
            }
            String[] node = line.split(",");
            TFlowElement userTask = new TUserTask();
            userTask.setName(node[2]);
            userTask.setId(node[1]);
            flowElements.add(userTask);
        }
        TProcess tProcess = new TProcess();
        for (TFlowElement flowElement : flowElements) {
            tProcess.getFlowElement().add(new JAXBElement<>(bpmnElementQName("TFlowElement"), TFlowElement.class, null, flowElement));
        }
        TDefinitions tDefinitions = new TDefinitions();
        tDefinitions.getRootElement().add(new JAXBElement<>(bpmnElementQName("TProcess"),TProcess.class,null, tProcess));
        return tDefinitions;
    }
}