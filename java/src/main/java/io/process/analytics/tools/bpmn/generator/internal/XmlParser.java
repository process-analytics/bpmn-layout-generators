/*
 * Copyright 2020 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.process.analytics.tools.bpmn.generator.internal;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

import io.process.analytics.tools.bpmn.generator.internal.generated.model.ObjectFactory;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;

public class XmlParser {

    private static final JAXBContext context = initContext();

    private static JAXBContext initContext() {
        try {
            return JAXBContext.newInstance(TDefinitions.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to initialize the JAXBContext", e);
        }
    }

    public void marshal(TDefinitions definitions, File outputFile) {
        try {
            JAXBElement<TDefinitions> root = new ObjectFactory().createDefinitions(definitions);
            createMarshaller().marshal(root, outputFile);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal", e);
        }
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try {
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BpmnNamespacePrefixMapper());
        } catch(PropertyException e) {
            // In case another JAXB implementation is used
            e.printStackTrace();
        }
        return marshaller;
    }

    public TDefinitions unmarshall(String xml) {
        try {
            StreamSource source = new StreamSource(new StringReader(xml));
            JAXBElement<TDefinitions> root = context.createUnmarshaller().unmarshal(source, TDefinitions.class);
            return root.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal", e);
        }
    }

}
