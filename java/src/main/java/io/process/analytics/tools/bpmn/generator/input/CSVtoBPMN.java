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



        TProcess tProcess = new TProcess();
        TDefinitions tDefinitions = new TDefinitions();
        tDefinitions.getRootElement().add(new JAXBElement<>(bpmnElementQName("TProcess"),TProcess.class,null, tProcess));

        List<TFlowElement> flowElements = getFlowElements(nodes);
        for (TFlowElement flowElement : flowElements) {
            tProcess.getFlowElement().add(new JAXBElement<>(bpmnElementQName("TFlowElement"), TFlowElement.class, null, flowElement));
        }

        List<TSequenceFlow> sequenceElements = getEdgeElements(edges);
        for (TSequenceFlow sequenceFlow : sequenceElements) {
            tProcess.getFlowElement().add(new JAXBElement<>(bpmnElementQName("TSequenceFlow"), TSequenceFlow.class, null, sequenceFlow));
        }

        return tDefinitions;


    }

    private List<TFlowElement> getFlowElements(String nodes) {
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
        return flowElements;
    }


    private List<TSequenceFlow> getEdgeElements(String edges) {
        String[] lines = edges.split("\n");
        lines[0] = null;
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