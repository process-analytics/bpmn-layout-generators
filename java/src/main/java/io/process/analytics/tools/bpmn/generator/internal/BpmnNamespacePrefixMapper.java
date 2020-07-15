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

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BpmnNamespacePrefixMapper extends NamespacePrefixMapper {

    private static final Map<String, String> namespaces = new HashMap<>();
    static {
        namespaces.put("http://www.omg.org/spec/BPMN/20100524/DI", "bpmndi");
        namespaces.put("http://www.omg.org/spec/BPMN/20100524/MODEL", "semantic");
        namespaces.put("http://www.omg.org/spec/DD/20100524/DC", "dc");
        namespaces.put("http://www.omg.org/spec/DD/20100524/DI", "di");
    }

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return namespaces.getOrDefault(namespaceUri, suggestion);
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        Set<String> uris = namespaces.keySet();
        return uris.toArray(new String[0]);
    }

}
