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
package io.process.analytics.tools.bpmn.generator.converter;

import io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.EdgeDirection;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import io.process.analytics.tools.bpmn.generator.model.Position;
import org.junit.jupiter.api.Test;

import static io.process.analytics.tools.bpmn.generator.converter.AlgoToDisplayModelConverter.EdgeDirection.*;
import static org.assertj.core.api.Assertions.assertThat;

class AlgoToDisplayModelConverterTest {

    @Test
    public void computeEdgeDirection_same_row_from_on_left() {
        assertThat(computeEdgeDirection(positionSameRow(10), positionSameRow(100))).isEqualTo(HorizontalLeftToRight);
    }

    @Test
    public void computeEdgeDirection_same_row_from_on_right() {
        assertThat(computeEdgeDirection(positionSameRow(100), positionSameRow(20))).isEqualTo(HorizontalRightToLeft);
    }

    @Test
    public void computeEdgeDirection_same_column_from_on_top() {
        assertThat(computeEdgeDirection(positionSameColumn(10), positionSameColumn(100))).isEqualTo(VerticalTopToBottom);
    }

    @Test
    public void computeEdgeDirection_same_column_from_on_bottom() {
        assertThat(computeEdgeDirection(positionSameColumn(100), positionSameColumn(20))).isEqualTo(VerticalBottomToTop);
    }

    // =================================================================================================================
    // Add waypoints when no elements between from and to
    // =================================================================================================================

    @Test
    public void computeEdgeDirection_no_elements_between_from_and_top_with_from_on_top_left() {
        //  +-----------------+
        //  | from            |
        //  |          to     |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(10, 10), position(50, 50)))
                .isEqualTo(TopLeftToBottomRight_FirstHorizontal);
    }

    @Test
    public void computeEdgeDirection_no_elements_between_from_and_top_with_from_on_top_right() {
        //  +-----------------+
        //  |          from   |
        //  | to              |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(50, 10), position(10, 50)))
                .isEqualTo(TopRightToBottomLeft_FirstHorizontal);
    }

    @Test
    public void computeEdgeDirection_no_elements_between_from_and_top_with_from_on_bottom_left() {
        //  +-----------------+
        //  |          to     |
        //  | from            |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(10, 100), position(50, 50)))
                .isEqualTo(BottomLeftToTopRight_FirstVertical);
    }

    @Test
    public void computeEdgeDirection_no_elements_between_from_and_top_with_from_on_bottom_right() {
        //  +-----------------+
        //  | to              |
        //  |          from   |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(50, 50), position(10, 10)))
                .isEqualTo(BottomRightToTopLeft_FirstVertical);
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
        assertThat(computeEdgeDirection(position(1, 1), position(2, 2), Grid.of(position(1, 2))))
                .isEqualTo(TopLeftToBottomRight_FirstHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_left_case_2() {
        //  +-----------------+
        //  | from   element  |
        //  |          to     |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(1, 1), position(2, 2), Grid.of(position(2, 1))))
                .isEqualTo(TopLeftToBottomRight_FirstVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_left() {
        //  +-----------------+
        //  | element  to     |
        //  | from            |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(1, 2), position(2, 1), Grid.of(position(1, 1))))
                .isEqualTo(BottomLeftToTopRight_FirstHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_left_case2() {
        //  +-----------------+
        //  |          to     |
        //  | from   element  |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(1, 2), position(2, 1), Grid.of(position(2, 2))))
                .isEqualTo(BottomLeftToTopRight_FirstVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_right() {
        //  +-----------------+
        //  | element  from   |
        //  | to              |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(2, 1), position(1, 2), Grid.of(position(1, 1))))
                .isEqualTo(TopRightToBottomLeft_FirstVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_top_right_case2() {
        //  +-----------------+
        //  |        from     |
        //  | to     element  |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(2, 1), position(1, 2), Grid.of(position(2, 2))))
                .isEqualTo(TopRightToBottomLeft_FirstHorizontal);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_right() {
        //  +-----------------+
        //  | to              |
        //  | element  from   |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(2, 2), position(1, 1), Grid.of(position(1, 2))))
                .isEqualTo(BottomRightToTopLeft_FirstVertical);
    }

    @Test
    public void computeEdgeDirection_has_elements_between_from_and_top_with_from_on_bottom_right_case2() {
        //  +-----------------+
        //  | to     element  |
        //  |        from     |
        //  +-----------------+
        assertThat(computeEdgeDirection(position(2, 2), position(1, 1), Grid.of(position(2, 1))))
                .isEqualTo(BottomRightToTopLeft_FirstHorizontal);
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static EdgeDirection computeEdgeDirection(Position positionFrom, Position positionTo) {
        return computeEdgeDirection(positionFrom, positionTo, new Grid());
    }

    private static EdgeDirection computeEdgeDirection(Position positionFrom, Position positionTo, Grid grid) {
        return AlgoToDisplayModelConverter.computeEdgeDirection(positionFrom, positionTo, grid);
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

}
