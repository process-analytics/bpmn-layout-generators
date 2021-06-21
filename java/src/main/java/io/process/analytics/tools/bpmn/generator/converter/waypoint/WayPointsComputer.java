/*
 * Copyright 2021 Bonitasoft S.A.
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
package io.process.analytics.tools.bpmn.generator.converter.waypoint;

import java.util.Collection;
import java.util.List;

import io.process.analytics.tools.bpmn.generator.model.Edge;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.display.DisplayFlowNode;
import io.process.analytics.tools.bpmn.generator.model.display.DisplayPoint;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WayPointsComputer {

    private final Collection<DisplayFlowNode> flowNodes;
    private final GridSearcher gridSearcher;
    private final WayPointsConverter wayPointsConverter = new WayPointsConverter();
    private final WayPointsPositioner wayPointsPositioner;

    public WayPointsComputer(final Grid grid, final Collection<DisplayFlowNode> flowNodes) {
        this.flowNodes = flowNodes;
        gridSearcher = new GridSearcher(grid);
        wayPointsPositioner = new WayPointsPositioner(gridSearcher);
    }

    public List<DisplayPoint> compute(Edge edge) {
        log.debug("Inferring waypoints of edge {}", edge);
        Position positionFrom = gridSearcher.getPositionFrom(edge);
        Position positionTo = gridSearcher.getPositionTo(edge);

        WayPointDescriptor wayPointDescriptor = wayPointsPositioner.computeWaypointDescriptor(positionFrom, positionTo);
        DisplayFlowNode flowNodeFrom = getFlowNode(positionFrom.getShape());
        DisplayFlowNode flowNodeTo = getFlowNode(positionTo.getShape());

        return wayPointsConverter.toDisplayPoints(wayPointDescriptor, flowNodeFrom.dimension, flowNodeTo.dimension);
    }

    private DisplayFlowNode getFlowNode(String flowNodeId) {
        return flowNodes.stream()
                .filter(f -> flowNodeId.equals(f.bpmnElementId))
                .findFirst()
                .get(); // always exist, otherwise error occur on flow node generation
    }

}
