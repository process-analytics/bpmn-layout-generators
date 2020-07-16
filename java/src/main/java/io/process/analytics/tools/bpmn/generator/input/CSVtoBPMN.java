package io.process.analytics.tools.bpmn.generator.input;

import static io.process.analytics.tools.bpmn.generator.internal.Semantic.addFlowNodes;
import static io.process.analytics.tools.bpmn.generator.internal.Semantic.addSequenceFlows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;

public class CSVtoBPMN {

    // map original flow element id with the values we are using
    // id cannot be numeric, in that case we map the id with a generated one, letting edge reference element ids with the values we are using for BPMN
    private final Map<String, String> mappingShapeId = new HashMap<>();

    public TDefinitions readFromCSV(String nodes, String edges) {
        TProcess process = new TProcess();
        process.setId("process_1");
        TDefinitions definitions = new TDefinitions();
        definitions.setId("definitions_1");
        definitions.setTargetNamespace(XMLConstants.NULL_NS_URI);
        Semantic semantic = new Semantic(definitions);
        semantic.add(process);

        addFlowNodes(process, getFlowNodeElements(nodes));
        addSequenceFlows(process, getEdgeElements(edges));

        return definitions;
    }

    private List<TFlowNode> getFlowNodeElements(String nodes) {
        String[] lines = toLinesWithoutHeader(nodes);
        List<TFlowNode> flowElements = new ArrayList<>();
        for (String line : lines) {
            if(line == null){
                continue;
            }
            String[] node = line.split(",");
            TFlowNode userTask = new TUserTask();
            userTask.setName(node[2]);

            String originalId = node[1];
            String bpmnId = originalId;
            if (isNumeric(originalId)) {
                bpmnId = "bpmnElement_" + originalId;
            }
            this.mappingShapeId.put(originalId, bpmnId);

            userTask.setId(bpmnId);
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

            TUserTask sourceRef = new TUserTask();
            tSequenceFlow.setSourceRef(sourceRef);
            sourceRef.setId(this.mappingShapeId.getOrDefault(edge[2], edge[2]));

            TUserTask targetRef = new TUserTask();
            tSequenceFlow.setTargetRef(targetRef);
            targetRef.setId(this.mappingShapeId.getOrDefault(edge[3], edge[3]));

            String id = edge[1];
            if (isNumeric(id)) {
                id = "sequenceFlow_" + id;
            }
            tSequenceFlow.setId(id);
            flowElements.add(tSequenceFlow);
        }
        return flowElements;
    }

    // TODO not optimal for performance, see https://www.baeldung.com/java-check-string-number
    // bpmn element id cannot be numeric
    private static boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}