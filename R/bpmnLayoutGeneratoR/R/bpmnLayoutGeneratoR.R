#' @export
generateBpmnLayout <- function(flow_node, sequence_flow, outputType = "BPMN") {
  rJava::.jinit()
  rJava::.jaddClassPath("../../java/target/bpmn-layout-generator-0.1.0-SNAPSHOT-jar-with-dependencies.jar")
  bpmnLayoutJava <- rJava::.jnew("io/process/analytics/tools/bpmn/generator/BPMNLayoutGenerator")
  type <- rJava::J(class = "io/process/analytics.tools/bpmn/generator/BPMNLayoutGenerator$ExportType", "valueOf", outputType)


  flow_node_as_csv <- to_csv(flow_node)
  sequence_flow_as_csv <- to_csv(sequence_flow)
  writeLines("flow nodes are:\n")
  writeLines(flow_node_as_csv)
  writeLines("sequence flows are:")
  writeLines(sequence_flow_as_csv)
  writeLines(class(flow_node_as_csv))
  diagramAsString <- bpmnLayoutJava$generateLayoutFromCSV(flow_node_as_csv, sequence_flow_as_csv, type)
  return(diagramAsString)
}

to_csv <- function(x) {
  connection <- textConnection("fn_as_csv", "w", local = TRUE)
  utils::write.csv(x, connection)
  return(fn_as_csv)
}