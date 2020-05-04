# BPMN Layout Generator Java Implementation

## Requirements

> JDK 8 or JDK 11

## Build

The project bundles a Maven Wrapper, so just run
``` bash
./mvnw package
```

## Run
Once you have build the project, run
```
java -jar target/bpmn-layout-generator-*-jar-with-dependencies.jar <path_to_input_bpmn_file> <path_to_output_file> [report_type]
```
where
- `<path_to_input_bpmn_file>`: path to the BPMN file
- `<path_to_output_file>`: path to the file that will store the result of the generation. Notice that the parent folders
will be created by the tool if they don't exist.
- `<export_type>`: optional, the export type.
    - available types: `ascii`, `svg` or `bpmn`. Defaults to `bpmn`.
    - `ascii` and `svg` have been developed to get feedback when running tests i.e. to get a quick preview of the
     algorithm result. They are not fully implemented and won't probably never be (if you have some interest on that
     topic, feel free to provide a Pull Request)
