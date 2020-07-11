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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
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
            Marshaller marshaller = context.createMarshaller();
            configure(marshaller);
            marshaller.marshal(root, outputFile);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal", e);
        }
    }

    private static void configure(Marshaller marshaller) throws PropertyException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try {
            // TODO not working, give a try to https://github.com/Siggen/jaxb2-namespace-prefix
            // jaxb binding for the maven plugin: http://www.mojohaus.org/jaxb2-maven-plugin/Documentation/v2.2/example_xjc_basic.html#Example_6:_Using_an_XML_Java_Binding_file_XJB
            //marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new BpmnNamespaceMapper());
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BpmnNamespaceMapper());
        } catch(PropertyException e) {
            // In case another JAXB implementation is used
            e.printStackTrace();
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

    public static class BpmnNamespaceMapper extends NamespacePrefixMapper {

        private static final String URI_BPMN_DI = "http://www.omg.org/spec/BPMN/20100524/DI";
        private static final String URI_DC = "http://www.omg.org/spec/DD/20100524/DC";
        private static final String URI_DI = "http://www.omg.org/spec/DD/20100524/DI";

        private static final Map<String, String> namespaces = new HashMap<>();
        static {
            namespaces.put(URI_BPMN_DI, "bpmndi");
            namespaces.put(URI_DC, "dc");
            namespaces.put(URI_DI, "di");
        }

        @Override
        public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
            return namespaces.getOrDefault(namespaceUri, suggestion);
        }

        @Override
        public String[] getPreDeclaredNamespaceUris() {
            Set<String> uris = namespaces.keySet();
            return uris.toArray(new String[uris.size()]);
        }

    }
}
