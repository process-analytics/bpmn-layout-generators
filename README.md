<h1 align="center">BPMN Layout Generators</h1> <br>
<p align="center">
    <p align="center">
        <a href="https://github.com/process-analytics/bpmn-layout-generators/releases">
            <img alt="GitHub release (latest by date including pre-releases" src="https://img.shields.io/github/v/release/process-analytics/bpmn-layout-generators?color=orange&include_prereleases"> 
        </a> 
        <a href="https://travis-ci.com/process-analytics/bpmn-layout-generators">
            <img alt="Build" src="https://travis-ci.com/process-analytics/bpmn-layout-generators.svg?branch=master"> 
        </a> 
        <br>
        <a href="CONTRIBUTING.md">
            <img alt="PRs Welcome" src="https://img.shields.io/badge/PRs-welcome-ff69b4.svg?style=flat-square"> 
        </a> 
        <a href="https://github.com/process-analytics/.github/blob/main/CODE_OF_CONDUCT.md">
            <img alt="Contributor Covenant" src="https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg"> 
        </a> 
        <a href="LICENSE">
            <img alt="License" src="https://img.shields.io/github/license/process-analytics/bpmn-layout-generators?color=blue"> 
        </a> 
    </p>
</p>

:warning: THIS IS AN EXPERIMENTAL PROJECT :warning:

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

## License

`bpmn-layout-generators` is released under the [Apache 2.0](LICENSE) license. \
Copyright &copy; 2020, Bonitasoft S.A.
