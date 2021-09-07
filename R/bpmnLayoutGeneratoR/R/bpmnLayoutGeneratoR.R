#' @export
generateBpmnLayout <- function(flow_node, sequence_flow, outputType = "BPMN") {
  bpmnLayoutJava <- rJava::.jnew("io/process/analytics/tools/bpmn/generator/BPMNLayoutGenerator")
  type <- rJava::J(class = "io/process/analytics.tools/bpmn/generator/BPMNLayoutGenerator$ExportType", "valueOf", outputType)

  flow_node_as_csv <- paste(to_csv(flow_node), collapse = "\n")
  sequence_flow_as_csv <- paste(to_csv(sequence_flow), collapse = "\n")

  tryCatch( diagramAsString <- bpmnLayoutJava$generateLayoutFromCSV(flow_node_as_csv, sequence_flow_as_csv, type), Exception = function(e){
    e$printStackTrace()
  } )
  # TODO on stack trace, the diagramAsString variable does not exist, so generate an error
  return(diagramAsString)
}

to_csv <- function(x) {
  connection <- textConnection("fn_as_csv", "w", local = TRUE)
  utils::write.csv(x, connection)
  return(fn_as_csv)
}
