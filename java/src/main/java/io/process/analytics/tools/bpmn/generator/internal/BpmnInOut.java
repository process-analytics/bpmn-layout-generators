/*
 * Copyright 2020 Bonitasoft S.A.
 *
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

    public void writeToBpmnFile(TDefinitions definitions, File file) {
        createParents(file);
        xmlParser.marshal(definitions, file);
    }

}
