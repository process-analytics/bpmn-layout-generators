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

import static io.process.analytics.tools.bpmn.generator.internal.StringUtils.defaultIfNull;

import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter;
import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.*;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.ShapeType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SVGExporter {

    private final AlgoToDisplayModelConverter converter = new AlgoToDisplayModelConverter();

    public String export(Grid grid, Diagram diagram) {
        DisplayModel model = converter.convert(grid, diagram);

        // TODO introduce a method to generate escaped double quote and avoid double quote escaping when writing xml
        StringBuilder content = new StringBuilder();
        content.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"")
                .append(model.width)
                .append("\" height=\"")
                .append(model.height).append("\">\n");

        // TODO extract colors to constant or configurable field
        final String colorActivityFill = "#E3E3E3";
        final String colorActivityStroke = "#92ADC8";
        final String colorEventFill = "LightSalmon";
        final String colorEventStroke = "FireBrick";
        final String colorGatewayFill = "Gold";
        final String colorGatewayStroke = "GoldenRod";
        final String colorEgeStroke = "Black";
        final int edgeStrokeWidth = 2;
        final double edgeStrokeOpacity = 0.5;

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
            }
            // draw circle (with an eclipse to eventually detect if shape is not squared)
            else if (flowNode.type == ShapeType.EVENT) {
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
            }
            // draw rhombus/diamond
            else if (flowNode.type == ShapeType.GATEWAY) {
                log.debug("Exporting gateway {}", flowNode.bpmnElementId);
                int x = flowNodeDimension.x;
                int y = flowNodeDimension.y;
                int width = flowNodeDimension.width;
                int height = flowNodeDimension.height;

                int midWidth = width / 2;
                int midHeight = height / 2;

                content.append("<polygon")
                        .append(" points=\"")
                        .append(x + midWidth).append(",").append(y)
                        .append(" ").append(x + width).append(",").append(y + midHeight)
                        .append(" ").append(x + midWidth).append(",").append(y + height)
                        .append(" ").append(x).append(",").append(y + midHeight)
                        .append("\"")
                        .append(" style=\"")
                        .append("fill:").append(colorGatewayFill)
                        .append(";stroke:").append(colorGatewayStroke)
                        .append(";stroke-width:").append(strokeWidth)
                        .append("\"")
                        .append(" />\n");
            }

            String labelFillColor = "#374962";
            String labelTextAnchor = "middle";

            content.append("<text")
                    .append(" x=\"").append(labelDimension.x).append("\"")
                    .append(" y=\"").append(labelDimension.y).append("\"")
                    .append(" text-anchor=\"").append(labelTextAnchor).append("\"")
                    .append(" font-size=\"").append(label.fontSize).append("\"")
                    .append(" fill=\"").append(labelFillColor).append("\"")
                    .append(">\n");
            // handle multi-lines label text
            String labelText = defaultIfNull(label.text);
            boolean isFirstLabelTextLine = true;
            for (String labelTextLine : labelText.split("\n")) {
                content.append("  <tspan")
                        .append(" x=\"").append(labelDimension.x).append("\"");
                if (!isFirstLabelTextLine) {
                    content.append(" dy=\"1.2em\"");
                }
                content.append(">")
                        .append(labelTextLine)
                        .append("</tspan>\n");
                isFirstLabelTextLine = false;
            }
            content.append("</text>\n");
        }

        for (DisplayEdge edge : model.edges) {
            // TODO manage couples in a generic way or build svg path instead of line (remove duplication)
            if (edge.wayPoints.size() >= 2) {
                DisplayPoint start = edge.wayPoints.get(0);
                DisplayPoint end = edge.wayPoints.get(1);
                content.append("<line")
                        .append(" x1=\"").append(start.x).append("\"")
                        .append(" y1=\"").append(start.y).append("\"")
                        .append(" x2=\"").append(end.x).append("\"")
                        .append(" y2=\"").append(end.y).append("\"")
                        .append(" stroke=\"").append(colorEgeStroke).append("\"")
                        .append(" stroke-width=\"").append(edgeStrokeWidth).append("\"")
                        .append(" stroke-opacity=\"").append(edgeStrokeOpacity).append("\"")
                        .append(" />\n");

                if (edge.wayPoints.size() >= 3) {
                    start = end;
                    end = edge.wayPoints.get(2);
                    content.append("<line")
                            .append(" x1=\"").append(start.x).append("\"")
                            .append(" y1=\"").append(start.y).append("\"")
                            .append(" x2=\"").append(end.x).append("\"")
                            .append(" y2=\"").append(end.y).append("\"")
                            .append(" stroke=\"").append(colorEgeStroke).append("\"")
                            .append(" stroke-width=\"").append(edgeStrokeWidth).append("\"")
                            .append(" stroke-opacity=\"").append(edgeStrokeOpacity).append("\"")
                            .append(" />\n");
                }
                if (edge.wayPoints.size() >= 4) {
                    start = end;
                    end = edge.wayPoints.get(3);
                    content.append("<line")
                            .append(" x1=\"").append(start.x).append("\"")
                            .append(" y1=\"").append(start.y).append("\"")
                            .append(" x2=\"").append(end.x).append("\"")
                            .append(" y2=\"").append(end.y).append("\"")
                            .append(" stroke=\"").append(colorEgeStroke).append("\"")
                            .append(" stroke-width=\"").append(edgeStrokeWidth).append("\"")
                            .append(" stroke-opacity=\"").append(edgeStrokeOpacity).append("\"")
                            .append(" />\n");
                }
            }
        }

        content.append("</svg>");
        return content.toString();
    }

}
