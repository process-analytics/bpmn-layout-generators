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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.junit.jupiter.api.Test;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.*;

public class XmlParserTest {

    private final XmlParser xmlParser = new XmlParser();

    @Test
    public void unmarshall() throws IOException {
        String bpmnAsXml = fileContent("src/test/resources/bpmn/01-startEvent.bpmn.xml");

        TDefinitions definitions = xmlParser.unmarshall(bpmnAsXml);

        // Semantic
        List<JAXBElement<? extends TRootElement>> rootElement = definitions.getRootElement();
        //rootElement.forEach(jaxbElement -> System.out.println(jaxbElement.getValue().getClass()));
        assertThat(rootElement)
                .hasSize(2)
                .extracting(JAXBElement::getValue)
                .extracting(TRootElement::getClass)
                .extracting(Class::getName)
                .containsExactly(TCollaboration.class.getName(),TProcess.class.getName());

        // Shapes
        List<BPMNDiagram> diagram = definitions.getBPMNDiagram();
        assertThat(diagram)
                .hasSize(1)
                .extracting(BPMNDiagram::getId).containsOnly("BPMNDiagram_1");
        BPMNPlane plane = diagram.get(0).getBPMNPlane();
        assertThat(plane.getId()).isEqualTo("BPMNPlane_1");
    }

    private static String fileContent(String filePath) throws IOException {
        String bpmnAsXml = Files.readString(new File(filePath).toPath());
        //System.out.println("xml: " + bpmnAsXml);
        return bpmnAsXml;
    }

}