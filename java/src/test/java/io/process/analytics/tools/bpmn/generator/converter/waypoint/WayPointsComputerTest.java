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
package io.process.analytics.tools.bpmn.generator.converter.waypoint;

import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Direction.*;
import static io.process.analytics.tools.bpmn.generator.converter.waypoint.Orientation.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import io.process.analytics.tools.bpmn.generator.model.Shape;
import io.process.analytics.tools.bpmn.generator.model.ShapeType;

class WayPointsComputerTest {

    // =================================================================================================================
    // from and to in the same row - left to right
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_same_row_from_on_left() {
        EdgeDirection edgeDirection = computeEdgeDirection(positionSameRow(10), positionSameRow(100));
        assertThat(edgeDirection.direction).isEqualTo(LeftToRight);
        assertThat(edgeDirection.orientation).isEqualTo(Horizontal);
    }

    @Test
    public void computeEdgeDirection_same_row_left_to_right_with_elements_between_none_on_bottom() {
        //  +--------------------+
        //  | from    elt     to |
        //  |                    |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1,1), position(4,1), Grid.of(position(3, 1)));
        assertThat(edgeDirection.direction).isEqualTo(LeftToRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnBottom(edgeDirection.bendConfiguration, 1);
    }

    @Test
    public void computeEdgeDirection_same_row_left_to_right_with_elements_between_no_elements_on_top() {
        //  +--------------------+
        //  |                    |
        //  | from    elt     to |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1,2), position(4,2), Grid.of(position(3, 2), position(3, 3)));
        assertThat(edgeDirection.direction).isEqualTo(LeftToRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnTop(edgeDirection.bendConfiguration, 1);
    }

    @Test
    public void computeEdgeDirection_same_row_left_to_right_with_elements_between_more_occupied_rows_on_top() {
        //  +--------------------+
        //  |         elt        |
        //  |         elt        |
        //  | from    elt     to |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1,3), position(4,3),
                Grid.of(position(3, 1), position(3, 2), position(3, 3), position(3, 4)));
        assertThat(edgeDirection.direction).isEqualTo(LeftToRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnBottom(edgeDirection.bendConfiguration, 2);
    }

    @Test
    public void computeEdgeDirection_same_row_left_to_right_with_elements_between_more_occupied_rows_on_bottom() {
        //  +--------------------+
        //  |         elt        |
        //  | from    elt     to |
        //  |         elt        |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1,2), position(4,2),
                Grid.of(position(3, 1), position(3, 2), position(3, 3), position(3, 4)));
        assertThat(edgeDirection.direction).isEqualTo(LeftToRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnTop(edgeDirection.bendConfiguration, 2);
    }

    @Test
    public void computeEdgeDirection_same_row_left_to_right_with_elements_between_as_much_on_bottom_as_on_top() {
        //  +--------------------+
        //  |         elt        |
        //  |         elt        |
        //  | from    elt     to |
        //  |         elt        |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1,3), position(4,3),
                Grid.of(position(3, 1), position(2, 2), position(3, 3), position(3, 4), position(2, 5)));
        assertThat(edgeDirection.direction).isEqualTo(LeftToRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnBottom(edgeDirection.bendConfiguration, 3);
    }

    // =================================================================================================================
    // from and to in the same row - right to left
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_same_row_from_on_right() {
        EdgeDirection edgeDirection = computeEdgeDirection(positionSameRow(100), positionSameRow(20));
        assertThat(edgeDirection.direction).isEqualTo(RightToLeft);
        assertThat(edgeDirection.orientation).isEqualTo(Horizontal);
    }

    @Test
    public void computeEdgeDirection_same_row_right_to_left_with_elements_between_none_on_bottom() {
        //  +--------------------+
        //  | to    elt     from |
        //  |                    |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(4,1), position(1,1), Grid.of(position(3, 1)));
        assertThat(edgeDirection.direction).isEqualTo(RightToLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnBottom(edgeDirection.bendConfiguration, 1);
    }

    @Test
    public void computeEdgeDirection_same_row_right_to_left_with_elements_between_no_elements_on_top() {
        //  +--------------------+
        //  |                    |
        //  | to      elt   from |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(4,2), position(1,2), Grid.of(position(3, 2), position(3, 3)));
        assertThat(edgeDirection.direction).isEqualTo(RightToLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnTop(edgeDirection.bendConfiguration, 1);
    }

    @Test
    public void computeEdgeDirection_same_row_right_to_left_with_elements_between_more_occupied_rows_on_top() {
        //  +--------------------+
        //  |         elt        |
        //  |         elt        |
        //  | to      elt   from |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(4,3), position(1,3),
                Grid.of(position(3, 1), position(3, 2), position(3, 3), position(3, 4)));
        assertThat(edgeDirection.direction).isEqualTo(RightToLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnBottom(edgeDirection.bendConfiguration, 2);
    }

    @Test
    public void computeEdgeDirection_same_row_right_to_left_with_elements_between_more_occupied_rows_on_bottom() {
        //  +--------------------+
        //  |         elt        |
        //  | to      elt   from |
        //  |         elt        |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(4,2), position(1,2),
                Grid.of(position(3, 1), position(3, 2), position(3, 3), position(3, 4)));
        assertThat(edgeDirection.direction).isEqualTo(RightToLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnTop(edgeDirection.bendConfiguration, 2);
    }

    @Test
    public void computeEdgeDirection_same_row_right_to_left_with_elements_between_as_much_on_bottom_as_on_top() {
        //  +--------------------+
        //  |         elt        |
        //  |         elt        |
        //  | to      elt   from |
        //  |         elt        |
        //  |         elt        |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(4,3), position(1,3),
                Grid.of(position(3, 1), position(2, 2), position(3, 3), position(3, 4), position(2, 5)));
        assertThat(edgeDirection.direction).isEqualTo(RightToLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontalVertical);
        assertBendConfigurationOnBottom(edgeDirection.bendConfiguration, 3);
    }

    // =================================================================================================================
    // from and to in the same column
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_same_column_from_on_top() {
        EdgeDirection edgeDirection = computeEdgeDirection(positionSameColumn(10), positionSameColumn(100));
        assertThat(edgeDirection.direction).isEqualTo(TopToBottom);
        assertThat(edgeDirection.orientation).isEqualTo(Vertical);
    }

    @Test
    public void computeEdgeDirection_same_column_from_on_bottom() {
        EdgeDirection edgeDirection = computeEdgeDirection(positionSameColumn(100), positionSameColumn(20));
        assertThat(edgeDirection.direction).isEqualTo(BottomToTop);
        assertThat(edgeDirection.orientation).isEqualTo(Vertical);
    }

    // =================================================================================================================
    // Add waypoints when no elements between from and to
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_no_elements_between_from_on_top_left_to_on_bottom_right() {
        //  +-----------------+
        //  | from            |
        //  |          to     |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(10, 10), position(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_no_elements_between_from_on_top_right_to_on_bottom_right() {
        //  +-----------------+
        //  |          from   |
        //  | to              |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(50, 10), position(10, 50));
        assertThat(edgeDirection.direction).isEqualTo(TopRightToBottomLeft);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_no_elements_between_from_on_bottom_left_to_on_top_right() {
        //  +-----------------+
        //  |          to     |
        //  | from            |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(10, 100), position(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_no_elements_between_from_on_bottom_right_to_on_top_left() {
        //  +-----------------+
        //  | to              |
        //  |          from   |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(50, 50), position(10, 10));
        assertThat(edgeDirection.direction).isEqualTo(BottomRightToTopLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    // =================================================================================================================
    // Add waypoints when from is a gateway
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_from_join_gateway_on_bottom_left_to_on_top_right() {
        //  +---------------------+
        //  |                  to |
        //  | from-gw-join        |
        //  +---------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewayJoin(10, 100), position(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_join_gateway_on_top_left_to_on_bottom_right() {
        //  +---------------------+
        //  | from-gw-join        |
        //  |                  to |
        //  +---------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewayJoin(1, 1), position(5, 5));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_split_gateway_on_bottom_left_to_on_top_right() {
        //  +---------------------+
        //  |                  to |
        //  | from-gw-split       |
        //  +---------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(10, 100), position(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_bottom_left_to_gateway_join_on_top_right() {
        //  +-----------------------------+
        //  |                 to-gw-join  |
        //  | from-gw-split               |
        //  +-----------------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(10, 100), positionGatewayJoin(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_bottom_left_to_gateway_split_on_top_right() {
        //  +-----------------------------+
        //  |                 to-gw-split |
        //  | from-gw-split               |
        //  +-----------------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(10, 100), positionGatewaySplit(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_top_left_to_on_bottom_right() {
        //  +-----------------------+
        //  | from-gw-split         |
        //  |                  to   |
        //  +-----------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(10, 10), position(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_top_left_to_gateway_join_on_bottom_right() {
        //  +------------------------------+
        //  | from-gw-split                |
        //  |                  to-gw-join  |
        //  +------------------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(10, 10), positionGatewayJoin(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_top_left_to_gateway_split_on_bottom_right() {
        //  +-----------------------------+
        //  | from-gw-split               |
        //  |                 to-gw-split |
        //  +-----------------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(10, 10), positionGatewaySplit(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_top_left_to_gateway_join_on_bottom_right_with_element_on_top_left() {
        //  +------------------------------+
        //  | from-gw-split            elt |
        //  |                  to-gw-join  |
        //  +------------------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(1, 1), positionGatewayJoin(2, 2),
                Grid.of(position(2, 1)));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    // =================================================================================================================
    // Add waypoints when 'to' is a gateway and not already covered by other tests
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_from_on_top_left_to_gateway_join_on_bottom_right() {
        //  +--------------------+
        //  | from               |
        //  |        to-gw-join  |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(10, 10), positionGatewayJoin(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_on_bottom_left_to_gateway_join_on_top_right() {
        //  +-----------------------+
        //  |           to-gw-join  |
        //  | from                  |
        //  +-----------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(10, 100), positionGatewayJoin(50, 50));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_on_top_right_to_gateway_join_on_bottom_left() {
        //  +-----------------------+
        //  |                  from |
        //  | to-gw-join            |
        //  +-----------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(50, 50), positionGatewayJoin(10, 100));
        assertThat(edgeDirection.direction).isEqualTo(TopRightToBottomLeft);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_from_gateway_split_on_top_right_to_bottom_left() {
        //  +-----------------------+
        //  |         from-gw-split |
        //  | elt                   |
        //  +-----------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(positionGatewaySplit(2, 1), position(1, 2),
                Grid.of(position(2, 2)));
        assertThat(edgeDirection.direction).isEqualTo(TopRightToBottomLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_from_on_bottom_right_to_gateway_on_top_left() {
        //  +--------------------+
        //  | to-gw-join         |
        //  |               from |
        //  +--------------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(50, 50), positionGatewayJoin(10, 10));
        assertThat(edgeDirection.direction).isEqualTo(BottomRightToTopLeft);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    // =================================================================================================================
    // Add waypoints when there are elements between from and to
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_left() {
        //  +-----------------+
        //  | from            |
        //  | element  to     |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1, 1), position(2, 2), Grid.of(position(1, 2)));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_left_case_2() {
        //  +-----------------+
        //  | from   element  |
        //  |          to     |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1, 1), position(2, 2), Grid.of(position(2, 1)));
        assertThat(edgeDirection.direction).isEqualTo(TopLeftToBottomRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_left() {
        //  +-----------------+
        //  | element  to     |
        //  | from            |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1, 2), position(2, 1), Grid.of(position(1, 1)));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_left_case2() {
        //  +-----------------+
        //  |          to     |
        //  | from   element  |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(1, 2), position(2, 1), Grid.of(position(2, 2)));
        assertThat(edgeDirection.direction).isEqualTo(BottomLeftToTopRight);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_right() {
        //  +-----------------+
        //  | element  from   |
        //  | to              |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(2, 1), position(1, 2), Grid.of(position(1, 1)));
        assertThat(edgeDirection.direction).isEqualTo(TopRightToBottomLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_right_case2() {
        //  +-----------------+
        //  |        from     |
        //  | to     element  |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(2, 1), position(1, 2), Grid.of(position(2, 2)));
        assertThat(edgeDirection.direction).isEqualTo(TopRightToBottomLeft);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_right() {
        //  +-----------------+
        //  | to              |
        //  | element  from   |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(2, 2), position(1, 1), Grid.of(position(1, 2)));
        assertThat(edgeDirection.direction).isEqualTo(BottomRightToTopLeft);
        assertThat(edgeDirection.orientation).isEqualTo(VerticalHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_right_case2() {
        //  +-----------------+
        //  | to     element  |
        //  |        from     |
        //  +-----------------+
        EdgeDirection edgeDirection = computeEdgeDirection(position(2, 2), position(1, 1), Grid.of(position(2, 1)));
        assertThat(edgeDirection.direction).isEqualTo(BottomRightToTopLeft);
        assertThat(edgeDirection.orientation).isEqualTo(HorizontalVertical);
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static EdgeDirection computeEdgeDirection(Position positionFrom, Position positionTo) {
        return computeEdgeDirection(positionFrom, positionTo, new Grid());
    }

    private static EdgeDirection computeEdgeDirection(Position positionFrom, Position positionTo, Grid grid) {
        return new WayPointsComputer(grid, null).computeEdgeDirection(positionFrom, positionTo);
    }

    private static Position positionSameRow(int x) {
        return position(x,12); // arbitrary row
    }

    private static Position positionSameColumn(int y) {
        return position(50,y); // arbitrary column
    }

    private static Position position(int x, int y) {
        return Position.builder().x(x).y(y).build();
    }

    private static Position positionGatewaySplit(int x, int y) {
        return Position.position(new Shape(null, null, ShapeType.GATEWAY, true), x, y);
    }

    private static Position positionGatewayJoin(int x, int y) {
        return Position.position(new Shape(null, null, ShapeType.GATEWAY, false), x, y);
    }

    private static void assertBendConfigurationOnBottom(BendConfiguration bendConfiguration, int offset) {
        assertThat(bendConfiguration.direction).isEqualTo(BendDirection.BOTTOM);
        assertThat(bendConfiguration.offset).isEqualTo(offset);
    }

    private static void assertBendConfigurationOnTop(BendConfiguration bendConfiguration, int offset) {
        assertThat(bendConfiguration.direction).isEqualTo(BendDirection.TOP);
        assertThat(bendConfiguration.offset).isEqualTo(offset);
    }

}
