package io.process.analytics.tools.bpmn.generator.internal;

import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.createParents;
import static io.process.analytics.tools.bpmn.generator.internal.FileUtils.fileContent;

import java.io.File;
import java.io.IOException;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;

public class BpmnInOut {

    private final XmlParser xmlParser;

    public static BpmnInOut defaultBpmnInOut() {
        return new BpmnInOut(new XmlParser());
    }

    public BpmnInOut(XmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public TDefinitions readFromBpmn(String xml) {
        return xmlParser.unmarshall(xml);
    }

    public TDefinitions readFromBpmn(File bpmn) {
        try {
            String xml = fileContent(bpmn);
            return readFromBpmn(xml);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file " + bpmn.getName(), e);
        }
    }

    public String writeToBpmn(TDefinitions definitions) {
        return xmlParser.marshal(definitions);
    }

}
