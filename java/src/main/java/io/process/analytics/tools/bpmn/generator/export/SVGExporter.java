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
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayDimension;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayFlowNode;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayLabel;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.DisplayModel;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.SortedDiagram;

public class SVGExporter {

    private final AlgoToDisplayModelConverter converter = new AlgoToDisplayModelConverter();

    public byte[] export(Grid grid, SortedDiagram diagram) {
        DisplayModel model = converter.convert(grid, diagram);

        StringBuilder content = new StringBuilder();
        content.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"")
                .append(model.width)
                .append("\" height=\"")
                .append(model.height).append("\">\n");

        for (DisplayFlowNode flowNode : model.flowNodes) {
            DisplayDimension flowNodeDimension = flowNode.dimension;
            DisplayLabel label = flowNode.label;
            DisplayDimension labelDimension = label.dimension;

            content.append("<rect ");
            content.append("x=\"").append(flowNodeDimension.x).append("\" ");
            content.append("y=\"").append(flowNodeDimension.y).append("\" ");
            content.append("width=\"").append(flowNodeDimension.width).append("\" ");
            content.append("height=\"").append(flowNodeDimension.height).append("\" ");
            content.append("rx=\"").append(flowNode.rx).append("\" ");
            content.append("fill=\"#E3E3E3\" stroke=\"#92ADC8\" ");
            content.append("stroke-width=\"").append(flowNode.strokeWidth).append("\"/>\n");
            content.append("<text x=\"").append(labelDimension.x);
            content.append("\" y=\"").append(labelDimension.y);
            content.append("\" text-anchor=\"middle\" font-size=\"").append(label.fontSize);
            content.append("\" fill=\"#374962\">");
            content.append(label.text).append("</text>\n");
        }
        content.append("</svg>");
        return content.toString().getBytes();
    }

}
