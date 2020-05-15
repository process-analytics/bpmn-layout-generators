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
package io.process.analytics.tools.bpmn.generator.model;

import static io.process.analytics.tools.bpmn.generator.model.Position.position;
import static io.process.analytics.tools.bpmn.generator.model.Shape.shape;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GridTest {

    private final Grid grid = new Grid();
    private final Shape nodeA = shape("a");
    private final Shape nodeB = shape("b");
    private final Shape nodeC = shape("c");

    @Test
    public void should_move_element_when_adding_row_after() {
        grid.add(position(nodeA, 0, 0));
        grid.add(position(nodeB, 0, 1));
        grid.add(position(nodeC, 0, 2));


        grid.addRowAfter(1);

        assertThat(grid.getPositions()).containsExactly(
                position(nodeA, 0, 0),
                position(nodeB, 0, 1),
                position(nodeC, 0, 3)
        );
    }
    @Test
    public void should_move_element_when_adding_row_before() {
        grid.add(position(nodeA, 0, 0));
        grid.add(position(nodeB, 0, 1));
        grid.add(position(nodeC, 0, 2));


        grid.addRowBefore(1);

        assertThat(grid.getPositions()).containsExactly(
                position(nodeA, 0, 0),
                position(nodeB, 0, 2),
                position(nodeC, 0, 3)
        );
    }

    @Test
    public void should_not_move_element_when_adding_row_after_all_existing_elements() {
        grid.add(position(nodeA, 0, 0));
        grid.add(position(nodeB, 0, 1));
        grid.add(position(nodeC, 0, 2));


        grid.addRowAfter(2);

        assertThat(grid.getPositions()).containsExactly(
                position(nodeA, 0, 0),
                position(nodeB, 0, 1),
                position(nodeC, 0, 2)
        );
    }

}