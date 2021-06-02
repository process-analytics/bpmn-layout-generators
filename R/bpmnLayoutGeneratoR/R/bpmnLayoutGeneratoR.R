#' @export
generateBpmnLayout <- function(flow_node, sequence_flow, outputType = "BPMN") {
  bpmnLayoutJava <- rJava::.jnew("io/process/analytics/tools/bpmn/generator/BPMNLayoutGenerator")
  type <- rJava::J(class = "io/process/analytics.tools/bpmn/generator/BPMNLayoutGenerator$ExportType", "valueOf", outputType)

  flow_node_as_csv <- paste(to_csv(flow_node), collapse = "\n")
  sequence_flow_as_csv <- paste(to_csv(sequence_flow), collapse = "\n")

  diagramAsString <- bpmnLayoutJava$generateLayoutFromCSV(flow_node_as_csv, sequence_flow_as_csv, type)
  return(diagramAsString)
}

to_csv <- function(x) {
  connection <- textConnection("fn_as_csv", "w", local = TRUE)
  utils::write.csv(x, connection)
  return(fn_as_csv)
}

"1,1,Add event on employee calendar,task"

"1,1,1,2012"