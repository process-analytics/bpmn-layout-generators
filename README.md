# bpmn-layout-generators [![Build Status](https://travis-ci.com/process-analytics/bpmn-layout-generators.svg?branch=master)](https://travis-ci.com/process-analytics/bpmn-layout-generators) 
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)

Tools for generating missing BPMNDiagram elements in BPMN files

Plan work for the 1st implementation
- could be based on https://www.researchgate.net/publication/221542866_A_Simple_Algorithm_for_Automatic_Layout_of_BPMN_Processes
- will be implemented in `Java`. If it works, `javascript`/`TypeScript` and `R` implementations will be done


## Implementations

- [java](java/README.md)

## Existing alternatives

Java
- https://github.com/camunda-consulting/code/tree/382f1521a4e9cd6bb92c2f9eacbe64a0e3835242/snippets/bpmndi-generator (latest available commit on 2020-04-08)
- https://github.com/camunda-consulting/migrate-to-camunda-tools/: tools to migrate from several vendors to Camunda, adaptation of the `bpmndi-generator`
- Camunda `fluent builder API`: https://docs.camunda.org/manual/7.9/user-guide/model-api/bpmn-model-api/fluent-builder-api/#generation-of-diagram-interchange

Javascript
- https://github.com/bpmn-io/bpmn-auto-layout/
