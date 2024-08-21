# BPMN Layout Generator Java Implementation

## Requirements

To build and run, use:
> JDK 17 or JDK 21

> [!NOTE]
> May work with older versions of JDK, but it has not been tested


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
Notice that `ASCII` and `SVG` ouput types have been developed to get feedback when running tests i.e. to get a quick preview of the
algorithm result. They are not fully implemented and won't probably never be (if you have some interest on that
topic, feel free to provide a Pull Request)


To generate BPMN semantic and diagram layout from discovery CSV files, run
```
java -jar target/bpmn-layout-generator-*-jar-with-dependencies.jar \
  --input-type=CSV \
  --output=<path_to_output_file> \
  csv/PatientsProcess/nodeSimple.csv csv/PatientsProcess/edgeSimple.cs
```
