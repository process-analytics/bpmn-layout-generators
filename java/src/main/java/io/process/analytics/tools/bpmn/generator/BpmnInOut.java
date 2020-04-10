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
package io.process.analytics.tools.bpmn.generator;

import io.process.analytics.tools.bpmn.generator.internal.XmlParser;
import io.process.analytics.tools.bpmn.generator.internal.model.TDefinitions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BpmnInOut {

    private final XmlParser xmlParser;

    public BpmnInOut(XmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public void readFromBpmn(String xml) {
        TDefinitions unmarshall = xmlParser.unmarshall(xml);
    }

    public void readFromBpmn(File bpmn) {
        try {
            String xml = Files.readString(bpmn.toPath());
            readFromBpmn(xml);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file " + bpmn.getName(), e);
        }
    }

//    public void writeToBpmn() {
//
//    }

}
