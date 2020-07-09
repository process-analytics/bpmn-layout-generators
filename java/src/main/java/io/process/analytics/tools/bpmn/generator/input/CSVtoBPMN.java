package io.process.analytics.tools.bpmn.generator.input;

import static io.process.analytics.tools.bpmn.generator.internal.Semantic.addFlowElements;
import static io.process.analytics.tools.bpmn.generator.internal.Semantic.addSequenceFlowElements;

import java.util.ArrayList;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;
import io.process.analytics.tools.bpmn.generator.internal.Semantic;

class CSVtoBPMN {

    public TDefinitions readFromCSV(String nodes, String edges) {
        TProcess process = new TProcess(); // TODO set generated id
        TDefinitions definitions = new TDefinitions();
        Semantic semantic = new Semantic(definitions);
        semantic.add(process);

        addFlowElements(process, getFlowElements(nodes));
        addSequenceFlowElements(process, getEdgeElements(edges));

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