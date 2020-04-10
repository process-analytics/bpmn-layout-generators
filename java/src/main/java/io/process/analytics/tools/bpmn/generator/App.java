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
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;

import java.io.File;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        String filePath = args[0];

        BpmnInOut bpmnInOut = new BpmnInOut(new XmlParser());

        BpmnModelInstance modelInstance = bpmnInOut.readWithCamunda(new File(filePath));
        Definitions definitions = modelInstance.getDefinitions();

        // Initialize
        BpmnDiagram bpmnDiagram = modelInstance.newInstance(BpmnDiagram.class);
        BpmnPlane plane = modelInstance.newInstance(BpmnPlane.class);
        bpmnDiagram.setBpmnPlane(plane);
        definitions.addChildElement(bpmnDiagram);


    }
}
