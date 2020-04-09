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

import io.process.analytics.tools.bpmn.generator.internal.model.TDefinitions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class XmlParser {
    private static final JAXBContext context = initContext();

    private static JAXBContext initContext() {
        try {
            return JAXBContext.newInstance(TDefinitions.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to initialize the JAXBContext", e);
        }
    }

    public void marshal() {
        try {
            context.createMarshaller().marshal(null, System.out);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal", e);
        }
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
