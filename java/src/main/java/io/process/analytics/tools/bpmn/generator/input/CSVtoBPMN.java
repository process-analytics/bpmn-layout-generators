/*
 * Copyright 2020-2021 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.process.analytics.tools.bpmn.generator.input;

import io.process.analytics.tools.bpmn.generator.internal.Semantic;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.process.analytics.tools.bpmn.generator.internal.Semantic.addFlowNodes;
import static io.process.analytics.tools.bpmn.generator.internal.Semantic.addSequenceFlows;

public class CSVtoBPMN {

    // map original flow element id with the values we are using
    // id cannot be numeric, in that case we map the id with a generated one, letting edge reference element ids with the values we are using for BPMN
    private final Map<String, String> mappingShapeId = new HashMap<>();

    // map flow element id with its incoming and outgoing edges
    private final Map<String, EdgeRelation> shapeRelations = new HashMap<>();

    public TDefinitions readFromCSV(String nodes, String edges) {
        TProcess process = new TProcess();
        process.setId("process_1");
        TDefinitions definitions = new TDefinitions();
        definitions.setId("definitions_1");
        definitions.setTargetNamespace(XMLConstants.NULL_NS_URI);
        Semantic semantic = new Semantic(definitions);
        semantic.add(process);

        List<TFlowNode> flowNodeElements = getFlowNodeElements(nodes);
        addFlowNodes(process, flowNodeElements);
        addSequenceFlows(process, getEdgeElements(edges));
        assignIncomingAndOutgoingReferences(flowNodeElements);

        return definitions;
    }

    private List<TFlowNode> getFlowNodeElements(String nodes) {
        String[] lines = toLinesWithoutHeader(nodes);
        List<TFlowNode> flowElements = new ArrayList<>();
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            String[] node = line.split(",");
            String type = removeEnclosingDoubleQuote(node[3]);
            TFlowNode flowNode;
            switch (type) {
                case "start_event":
                    flowNode = new TStartEvent();
                    break;
                case "end_event":
                    flowNode = new TEndEvent();
                    break;
                case "gateway":
                case "parallel_gateway":
                    flowNode = new TParallelGateway();
                    break;
                case "exclusive_gateway":
                    flowNode = new TExclusiveGateway();
                    break;
                case "inclusive_gateway":
                    flowNode = new TInclusiveGateway();
                    break;
                case "user_task":
                    flowNode = new TUserTask();
                    break;
                case "service_task":
                    flowNode = new TServiceTask();
                    break;
                case "task":
                default:
                    flowNode = new TTask();
            }
            flowNode.setName(removeEnclosingDoubleQuote(node[2]));

            String originalId = node[1];
            String bpmnId = originalId;
            if (isNumeric(originalId)) {
                bpmnId = "bpmnElement_" + originalId;
            }
            this.mappingShapeId.put(originalId, bpmnId);

            flowNode.setId(bpmnId);
            flowElements.add(flowNode);
        }
        return flowElements;
    }

    private void assignIncomingAndOutgoingReferences(List<TFlowNode> flowNodeElements) {
        for (TFlowNode flowNode : flowNodeElements) {
            EdgeRelation edgeRelation = this.shapeRelations.get(flowNode.getId());
            edgeRelation.incoming.stream().map(CSVtoBPMN::bpmnElementQName).forEach(flowNode.getIncoming()::add);
            edgeRelation.outgoing.stream().map(CSVtoBPMN::bpmnElementQName).forEach(flowNode.getOutgoing()::add);
        }
    }

    // TODO rework to find the right value
    private static QName bpmnElementQName(String bpmnElement) {
        return new QName(XMLConstants.NULL_NS_URI, bpmnElement, XMLConstants.DEFAULT_NS_PREFIX); // <semantic:incoming xmlns="">sequenceFlow_1</semantic:incoming>
        // return new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", bpmnElement, XMLConstants.DEFAULT_NS_PREFIX); // <semantic:incoming>semantic:sequenceFlow_1</semantic:incoming>
    }

    private static String removeEnclosingDoubleQuote(String s) {
        return s.replaceAll("^\"|\"$", "");
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
            String sourceRefId = this.mappingShapeId.getOrDefault(edge[2], edge[2]);
            sourceRef.setId(sourceRefId);

            TUserTask targetRef = new TUserTask();
            tSequenceFlow.setTargetRef(targetRef);
            String targetRefId = this.mappingShapeId.getOrDefault(edge[3], edge[3]);
            targetRef.setId(targetRefId);

            String sequenceFlowId = edge[1];
            if (isNumeric(sequenceFlowId)) {
                sequenceFlowId = "sequenceFlow_" + sequenceFlowId;
            }
            tSequenceFlow.setId(sequenceFlowId);
            flowElements.add(tSequenceFlow);

            // prepare incoming/outgoing management
            getEdgeRelation(sourceRefId).outgoing.add(sequenceFlowId);
            getEdgeRelation(targetRefId).incoming.add(sequenceFlowId);
        }
        return flowElements;
    }

    private EdgeRelation getEdgeRelation(String shapeId) {
        EdgeRelation edgeRelation = this.shapeRelations.get(shapeId);
        if (edgeRelation == null) {
            edgeRelation= new EdgeRelation();
            this.shapeRelations.put(shapeId, edgeRelation);
        }
        return edgeRelation;
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

    private static class EdgeRelation {

        public final List<String> incoming = new ArrayList<>();
        public final List<String> outgoing = new ArrayList<>();

    }

}

