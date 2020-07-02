package io.process.analytics.tools.bpmn.generator.input;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;

import javax.xml.bind.JAXBElement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static io.process.analytics.tools.bpmn.generator.internal.BPMNDiagramRichBuilder.bpmnElementQName;

class CSVtoBPMN {

    public TDefinitions readFromCSV(String nodes, String edges) {
        TProcess process = new TProcess();
        TDefinitions definitions = new TDefinitions();
        definitions.getRootElement()
                .add(new JAXBElement<>(bpmnElementQName("TProcess"), TProcess.class, null, process));

        getFlowElements(nodes).stream()
                .map(f -> new JAXBElement<>(bpmnElementQName("TFlowElement"), TFlowElement.class, null, f))
                .forEach(f -> process.getFlowElement().add(f));

        getEdgeElements(edges).stream()
                .map(e -> new JAXBElement<>(bpmnElementQName("TSequenceFlow"), TSequenceFlow.class, null, e))
                .forEach(e -> process.getFlowElement().add(e));

        return definitions;
    }

    private List<TFlowElement> getFlowElements(String nodes) {
        String[] lines = toLinesWithoutHeader(nodes);
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
        return flowElements;
    }

    private String[] toLinesWithoutHeader(String fileContent) {
        String[] lines = fileContent.split("\n");
        lines[0] = null;
        return lines;
    }

    private List<TSequenceFlow> getEdgeElements(String edges) {
        String[] lines = toLinesWithoutHeader(edges);
        List<TSequenceFlow> flowElements = new ArrayList<>();
        for (String line : lines) {
            if(line == null){
                continue;
            }
            String[] edge = line.split(",");
            TSequenceFlow tSequenceFlow = new TSequenceFlow();
            tSequenceFlow.setSourceRef(edge[2]);
            tSequenceFlow.setTargetRef(edge[3]);
            tSequenceFlow.setId(edge[1]);
            flowElements.add(tSequenceFlow);
        }
        return flowElements;
    }

}