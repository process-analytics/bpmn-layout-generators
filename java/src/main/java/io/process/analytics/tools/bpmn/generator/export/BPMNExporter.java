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
package io.process.analytics.tools.bpmn.generator.export;

import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayModel;
import io.process.analytics.tools.bpmn.generator.internal.BPMNDiagramRichBuilder;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BPMNExporter {

    private final AlgoToDisplayModelConverter converter;

    public static BPMNExporter defaultBpmnExporter() {
        return new BPMNExporter(new AlgoToDisplayModelConverter());
    }

    public TDefinitions export(TDefinitions originalBpmnDefinitions, Grid grid, Diagram diagram) {
        BPMNDiagramRichBuilder builder = new BPMNDiagramRichBuilder(originalBpmnDefinitions);
        DisplayModel displayModel = converter.convert(grid, diagram);
        displayModel.flowNodes.forEach(builder::addFlowNode);
        displayModel.edges.forEach(builder::addEdge);
        return builder.build();
    }

}
