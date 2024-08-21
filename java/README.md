# BPMN Layout Generator Java Implementation

## Requirements

To build and run, use:
> JDK 17 or JDK 21

> [!WARNING]
> The build produces bytecode for JDK 17 or newer, so you cannot run the provided jar with older JDK versions.


## Build

The project bundles a Maven Wrapper, so just run
``` bash
./mvnw package
```

## Usage

**Note**: for more options, run with the `--help` option

To generate the layout of an existing BPMN file and save the result as a BPMN file, run
```
java -jar target/bpmn-layout-generator-*-jar-with-dependencies.jar --output=<path_to_output_file> <path_to_input_bpmn_file>
```
If you want to have the resulting layout in an SVG file, pass `--output-type=SVG`   
Notice that `ASCII` and `SVG` output types have been developed to get feedback when running tests i.e. to get a quick preview of the
algorithm result. They are not fully implemented and won't probably never be (if you have some interest on that
topic, feel free to provide a Pull Request)


To generate BPMN semantic and diagram layout from discovery CSV files, run
```
java -jar target/bpmn-layout-generator-*-jar-with-dependencies.jar \
  --input-type=CSV \
  --output=<path_to_output_file> \
  csv/PatientsProcess/nodeSimple.csv csv/PatientsProcess/edgeSimple.cs
```
