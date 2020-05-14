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
import io.process.analytics.tools.bpmn.generator.model.ShapeType;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SVGExporter {

    private final AlgoToDisplayModelConverter converter = new AlgoToDisplayModelConverter();

    public byte[] export(Grid grid, Diagram diagram) {
        DisplayModel model = converter.convert(grid, diagram);

        // TODO introduce a method to generate escaped double quote and avoid double quote escaping when writing xml
        StringBuilder content = new StringBuilder();
        content.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"")
                .append(model.width)
                .append("\" height=\"")
                .append(model.height).append("\">\n");

        // TODO extract colors to constant or configurable field
        final String colorActivityFill ="#E3E3E3";
        final String colorActivityStroke ="#92ADC8";
        final String colorEventFill ="LightSalmon";
        final String colorEventStroke ="FireBrick";
        final String colorGatewayFill ="Gold";
        final String colorGatewayStroke ="GoldenRod";

        for (DisplayFlowNode flowNode : model.flowNodes) {
            DisplayDimension flowNodeDimension = flowNode.dimension;
            DisplayLabel label = flowNode.label;
            DisplayDimension labelDimension = label.dimension;

            final int strokeWidth = flowNode.strokeWidth;

            if (flowNode.type == ShapeType.ACTIVITY) {
                log.debug("Exporting activity {}", flowNode.bpmnElementId);
                content.append("<rect")
                        .append(" x=\"").append(flowNodeDimension.x).append("\"")
                        .append(" y=\"").append(flowNodeDimension.y).append("\"")
                        .append(" width=\"").append(flowNodeDimension.width).append("\"")
                        .append(" height=\"").append(flowNodeDimension.height).append("\"")
                        .append(" rx=\"").append(flowNode.rx).append("\"")
                        .append(" fill=\"").append(colorActivityFill).append("\"")
                        .append(" stroke=\"").append(colorActivityStroke).append("\"")
                        .append(" stroke-width=\"").append(strokeWidth).append("\"")
                        .append(" />\n");
            } else if (flowNode.type == ShapeType.EVENT) {
                log.debug("Exporting event {}", flowNode.bpmnElementId);
                int rx = flowNodeDimension.width / 2;
                int ry = flowNodeDimension.height / 2;
                int cx = flowNodeDimension.x + rx;
                int cy = flowNodeDimension.y + ry;
                content.append("<ellipse")
                        .append(" cx=\"").append(cx).append("\"")
                        .append(" cy=\"").append(cy).append("\"")
                        .append(" rx=\"").append(rx).append("\"")
                        .append(" ry=\"").append(ry).append("\"")
                        .append(" fill=\"").append(colorEventFill).append("\"")
                        .append(" stroke=\"").append(colorEventStroke).append("\"")
                        .append(" stroke-width=\"").append(strokeWidth).append("\"")
                        .append(" pointer-events=\"all\"")
                        .append(" />\n");
            } else if (flowNode.type == ShapeType.GATEWAY) {
                log.debug("Exporting gateway {}", flowNode.bpmnElementId);
                // TODO gateway should be a rhombus/diamond instead of a rectangle
                content.append("<rect")
                        .append(" x=\"").append(flowNodeDimension.x).append("\"")
                        .append(" y=\"").append(flowNodeDimension.y).append("\"")
                        .append(" width=\"").append(flowNodeDimension.width).append("\"")
                        .append(" height=\"").append(flowNodeDimension.height).append("\"")
                        .append(" rx=\"").append(flowNode.rx).append("\"")
                        .append(" fill=\"").append(colorGatewayFill).append("\"")
                        .append(" stroke=\"").append(colorGatewayStroke).append("\"")
                        .append(" stroke-width=\"").append(strokeWidth).append("\"")
                        .append(" />\n");
            }

            content.append("<text")
                    .append(" x=\"").append(labelDimension.x).append("\"")
                    .append(" y=\"").append(labelDimension.y).append("\"")
                    .append(" text-anchor=\"middle\" font-size=\"").append(label.fontSize).append("\"")
                    // TODO extract color
                    .append(" fill=\"#374962\">")
                    .append(label.text)
                    .append("</text>\n");
        }
        content.append("</svg>");
        return content.toString().getBytes();
    }

}
