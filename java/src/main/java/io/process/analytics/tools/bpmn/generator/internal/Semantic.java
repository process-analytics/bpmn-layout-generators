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

import io.process.analytics.tools.bpmn.generator.internal.model.*;
import lombok.*;

import javax.xml.bind.JAXBElement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Helper to access to the BPMN semantic part
 */
@RequiredArgsConstructor
public class Semantic {

    @NonNull
    @Getter
    private final TDefinitions definitions;

    public List<TParticipant> getParticipants() {
        List<TCollaboration> collaborations = definitions.getRootElement().stream()
                .map(JAXBElement::getValue)
                .filter(TCollaboration.class::isInstance)
                .map(o -> (TCollaboration) o)
                .collect(Collectors.toList());
        // TODO check at most 1
        //        if (collaborations.isEmpty()) {
        //            return Collections.emptyList();
        //        }
        //        return collaborations.get(0).getParticipant();
        return Optional.of(collaborations).map(list -> list.get(0).getParticipant())
                .orElseGet(Collections::emptyList);
    }

}
