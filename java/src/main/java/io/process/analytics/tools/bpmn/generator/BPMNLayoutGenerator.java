package io.process.analytics.tools.bpmn.generator;

import static io.process.analytics.tools.bpmn.generator.export.BPMNExporter.defaultBpmnExporter;
import static io.process.analytics.tools.bpmn.generator.internal.BpmnInOut.defaultBpmnInOut;

import java.io.File;
import java.io.IOException;

import io.process.analytics.tools.bpmn.generator.algo.ShapeLayouter;
import io.process.analytics.tools.bpmn.generator.algo.ShapeSorter;
import io.process.analytics.tools.bpmn.generator.converter.BpmnToAlgoModelConverter;
import io.process.analytics.tools.bpmn.generator.export.ASCIIExporter;
import io.process.analytics.tools.bpmn.generator.export.SVGExporter;
import io.process.analytics.tools.bpmn.generator.input.CSVtoBPMN;
import io.process.analytics.tools.bpmn.generator.internal.BpmnInOut;
import io.process.analytics.tools.bpmn.generator.internal.FileUtils;
import io.process.analytics.tools.bpmn.generator.internal.generated.model.TDefinitions;
import io.process.analytics.tools.bpmn.generator.model.Diagram;
import io.process.analytics.tools.bpmn.generator.model.Grid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BPMNLayoutGenerator {


    public enum ExportType {
        ASCII,
        BPMN,
        SVG
    }

    protected final BpmnInOut bpmnInOut = defaultBpmnInOut();


    /*
       Public methods
     */

    public String generateLayoutFromBPMNSemantic(String bpmn, ExportType exportType) {
        TDefinitions tDefinitions = bpmnInOut.readFromBpmn(bpmn);
        LayoutSortedDiagram layout = layout(tDefinitions);
        return export(layout, exportType);
    }

    public String generateLayoutFromCSV(String nodes, String edges, ExportType exportType) {
        TDefinitions tDefinitions = new CSVtoBPMN().readFromCSV(nodes, edges);
        LayoutSortedDiagram layout = layout(tDefinitions);
        return export(layout, exportType);
    }


    /*
       BPMN --> Diagram
     */

    private LayoutSortedDiagram layout(TDefinitions definitions) {
        log.info("Converting BPMN into internal model");
        Diagram diagram = new BpmnToAlgoModelConverter().toAlgoModel(definitions);
        log.info("Conversion done");

        log.info("Sorting and generating Layout");
        Diagram sortedDiagram = new ShapeSorter().sort(diagram);
        Grid grid = new ShapeLayouter().layout(sortedDiagram);
        log.info("Sort and Layout done");

        return new LayoutSortedDiagram(definitions, grid, sortedDiagram);
    }




    /*
       Diagram --> Exported format
     */

    private String exportToAscii(LayoutSortedDiagram diagram) {
        log.info("Exporting to ASCII file");
        return new ASCIIExporter().export(diagram.getGrid());
    }

    protected String exportToBpmn(LayoutSortedDiagram diagram) {
        log.info("Exporting to BPMN");
        TDefinitions newDefinitions = defaultBpmnExporter().export(diagram.originalDefinitions, diagram.grid, diagram.diagram);
        return bpmnInOut.writeToBpmn(newDefinitions);
    }

    private String exportToSvg(LayoutSortedDiagram diagram) {
        log.info("Exporting to SVG");
        return new SVGExporter().export(diagram.getGrid(), diagram.getDiagram());
    }


    private String export(LayoutSortedDiagram layout, ExportType exportType) {
        switch (exportType) {
            case ASCII:
                return exportToAscii(layout);
            case BPMN:
                return exportToBpmn(layout);
            case SVG:
                return exportToSvg(layout);
            default:
                throw new IllegalStateException("Unexpected Export Type: " + exportType);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class LayoutSortedDiagram {

        private final TDefinitions originalDefinitions;
        private final Grid grid;
        private final Diagram diagram;
    }
}
