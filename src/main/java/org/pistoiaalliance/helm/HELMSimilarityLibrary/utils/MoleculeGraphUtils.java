/*******************************************************************************
 * Copyright C 2016, QUATTRO RESEARCH GMBH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.pistoiaalliance.helm.HELMSimilarityLibrary.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.helm.notation2.parser.exceptionparser.NotationException;
import org.helm.notation2.parser.notation.connection.ConnectionNotation;
import org.helm.notation2.parser.notation.polymer.MonomerNotationUnitRNA;
import org.helm.notation2.parser.notation.polymer.PolymerNotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MoleculeGraphUtils class implements a moleculeGraph of type Graph with a list of
 * vertices and edges between vertices that are represented by neighbors for
 * each vertex.
 *
 * @author bueltel
 */
public class MoleculeGraphUtils {
    private static Map<String, Integer> polymerIDMap = new HashMap<>();
    private static List<Integer> polymerIDList = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(MoleculeGraphUtils.class);

    /**
     *      * Method builds a moleculeGraph from a list of PolymerNotations and a
     *      * list of Connections. Each vertex is a monomer and vertices are
     *      * neighbors if there is a connection between them.
     *
     * @param listPolymerNotations list of polymer notations
     * @param listOfConnections list of connections
     * @return The moleculeGraph of a list of polymer notations and connections
     * @throws NotationException if anything goes wrong
     */
    public static Graph buildMoleculeGraph(List<PolymerNotation> listPolymerNotations,
                                           List<ConnectionNotation> listOfConnections) throws NotationException {
        Graph moleculeGraph = new Graph();
        LOG.debug("Building molecule graph...");

        // Iterate over every polymer of the HELM notation
        for (int idx = 0; idx < listPolymerNotations.size(); idx++) {

            // If polymer is RNA, consider that ribose and phosphate are backbone and bases are branching from ribose
            if (listPolymerNotations.get(idx).getListMonomers().get(0).getType().equalsIgnoreCase("RNA")) {
                moleculeGraph.addAllVertices(buildRNAPart(listPolymerNotations.get(idx), idx));
            }

            // If polymer is peptide there are only "linear" connections. Branching connections are handled exceptionally.
            else if (listPolymerNotations.get(idx).getListMonomers().get(0).getType().equalsIgnoreCase("PEPTIDE")) {
                moleculeGraph.addAllVertices(buildPeptidePart(listPolymerNotations.get(idx), idx));
            }

            //If polymer is CHEM, there is only one monomer in the notation
            else if (listPolymerNotations.get(idx).getListMonomers().get(0).getType().equalsIgnoreCase("CHEM")) {
                moleculeGraph.addAllVertices(buildChemPart(listPolymerNotations.get(idx), idx));
            }

            // Map to save the order of polymers in the notation.
            polymerIDMap.put(listPolymerNotations.get(idx).getPolymerID().toString(), idx);
        }

        // Save an index for each vertex to be able to iterate successionally in depth first search.
        for (int vertexIdx = 0; vertexIdx < moleculeGraph.getVertices().size(); vertexIdx++) {
            moleculeGraph.getVertices().get(vertexIdx).setIndex(vertexIdx);
        }

        // Check for intramolecular or intermolecular connections.
        if (!listOfConnections.isEmpty()) {
            moleculeGraph = addConnections(moleculeGraph, listPolymerNotations, listOfConnections);
        }

        polymerIDList.clear();
        polymerIDMap.clear();
        return moleculeGraph;
    }

    /**
     * Builds the RNA part of the molecule.
     *
     * @param polymer the polymer notation of type rna to be build as a graph part
     * @param polymerNumber number of the polymer
     * @return RNA part molecule graph
     * @throws NotationException if anything goes wrong
     * @throws NotationException if anything goes wrong
     */
    public static Graph buildRNAPart(PolymerNotation polymer, int polymerNumber) throws NotationException {
        Graph moleculeGraph = new Graph();
        List<String> monomerUnitTypes = new ArrayList<>();
        LOG.debug("Building RNA part of molecule graph.");

        for (int n = 0; n < polymer.getListMonomers().size(); n++) {
            MonomerNotationUnitRNA unit = new MonomerNotationUnitRNA(polymer.getListMonomers().get(n).getUnit(), "RNA");

            // Add monomerUnit vertices to the graph, save a flag for ribose, base and phosphate,
            // keep track of the polymer ID for each monomer in polymerIDList to be able to check
            // if two monomers are of the same polymer.
            for (int m = 0; m < unit.getContents().size(); m++) {
                moleculeGraph.addVertex(unit.getContents().get(m));
                Vertex lastAdded = moleculeGraph.getVertices().get(moleculeGraph.getVertices().size() - 1);
                lastAdded.monomerType = Vertex.Type.RNA;
                if (lastAdded.getMonomer().getUnit().length() > 1) {
                    lastAdded.setNonNatural();
                } else {
                    lastAdded.setHasUniqueUnit(true);
                }
                monomerUnitTypes.add(unit.getInformation().get(m));
                polymerIDList.add(polymerNumber);
            }
        }

        for (int i = 0; i < moleculeGraph.vertexCount() - 1; i++) {
            Vertex currentVertex = moleculeGraph.getVertices().get(i);
            String typeCurrent = monomerUnitTypes.get(i);

            if (typeCurrent.equalsIgnoreCase("R")) {
                Vertex secondVertex = moleculeGraph.getVertices().get(i + 1);
                String typeSecond = monomerUnitTypes.get(i + 1);
                if (typeSecond.equalsIgnoreCase("P")) {
                    currentVertex.addBothNeighbours(secondVertex);
                } else if (typeSecond.equalsIgnoreCase("X")) {
                    currentVertex.addBothNeighbours(secondVertex);


                    if ((i + 2) < moleculeGraph.vertexCount()) {
                        Vertex thirdVertex = moleculeGraph.getVertices().get(i + 2);
                        String typeThird = monomerUnitTypes.get(i + 2);

                        if (typeThird.equalsIgnoreCase("P")) {
                            currentVertex.addBothNeighbours(thirdVertex);
                        }
                    }
                }
            } else if (typeCurrent.equalsIgnoreCase("P")) {
                Vertex secondVertex = moleculeGraph.getVertices().get(i + 1);
                String typeSecond = monomerUnitTypes.get(i + 1);
                if (typeSecond.equalsIgnoreCase("R")) {
                    currentVertex.addBothNeighbours(secondVertex);
                }
            } else {
                continue;
            }

        }
        LOG.debug("Building RNA part of molecule graph successful.");
        return moleculeGraph;
    }

    /**
     * Builds the PEPTIDE part of the molecule.
     *
     * @param polymer the polymer notation of type peptide to be build as a graph part
     * @param polymerNumber number of the polymer
     * @return RNA part molecule graph
     */
    public static Graph buildPeptidePart(PolymerNotation polymer, int polymerNumber) {
        Graph moleculeGraph = new Graph();
        LOG.debug("Building PEPTIDE part of molecule graph.");

        // Add monomer vertices to the graph, keep track of the polymer ID for each monomer
        // in polymerIDList to be able to check if two monomers are of the same polymer
        for (int l = 0; l < polymer.getListMonomers().size(); l++) {
            // create nodes for each monomer and save index for each monomer respectively
            moleculeGraph.addVertex(polymer.getListMonomers().get(l));
            Vertex lastAdded = moleculeGraph.getVertices().get(moleculeGraph.getVertices().size() - 1);
            lastAdded.monomerType = Vertex.Type.PEPTIDE;
            if (lastAdded.getMonomer().getUnit().length() > 1) {
                lastAdded.setNonNatural();
            }
            polymerIDList.add(polymerNumber);
        }

        // Create edges (bonds) between nodes (monomers) if peptide consists of more than two monomers.
        for (int i = 0; i < moleculeGraph.vertexCount() - 1; i++) {
            Vertex currentVertex = moleculeGraph.getVertices().get(i);
            Vertex nextVertex = moleculeGraph.getVertices().get(i + 1);
            currentVertex.addBothNeighbours(nextVertex);
        }
        LOG.debug("Building PEPTIDE part of molecule graph successful..");
        return moleculeGraph;
    }

    /**
     * Builds the CHEM part of the molecule.
     *
     * @param polymer the polymer notation of type chem to be build as a graph part
     * @param polymerNumber number of the polymer
     * @return RNA part molecule graph
     */
    public static Graph buildChemPart(PolymerNotation polymer, int polymerNumber) {
        Graph moleculeGraph = new Graph();
        LOG.debug("Building CHEM part of molecule graph.");

        // add monomer vertex to the graph, keep track of the polymer ID
        // for the monomer in polymerIDList to be able to check if two
        // monomers are of the same polymer
        moleculeGraph.addVertex(polymer.getListMonomers().get(0));
        moleculeGraph.getVertices().get(0).monomerType = Vertex.Type.CHEM;
        polymerIDList.add(polymerNumber);
        LOG.debug("Building CHEM part of molecule graph successful.");
        return moleculeGraph;
    }

    /**
     * Adds the connections between polymers of the molecule.
     *
     * @param moleculeGraph The moleculeGraph of a HELM notation without connections
     * @param polymerList list of polymers
     * @param listOfConnections list of connections
     * @return The moleculeGraph of a HELM notation with connections
     */
    public static Graph addConnections(Graph moleculeGraph, List<PolymerNotation> polymerList,
                                       List<ConnectionNotation> listOfConnections) {
        LOG.debug("Adding connections to molecule graph.");

        for (int index = 0; index < listOfConnections.size(); index++) {
            ConnectionNotation connection = listOfConnections.get(index);

            if (connection.getSourceId().toString().equals(connection.getTargetId().toString())) {
                // There is a connection between monomers of the same polymer.
                LOG.debug("Adding connection between monomers of same polymer.");
                String polymerID = connection.getSourceId().toString();
                Integer polymerIdx = polymerIDMap.get(polymerID);
                int idxi = 0;
                int idxj = 0;
                if (polymerIdx == 0) {
                    idxi = Integer.parseInt(connection.getSourceUnit()) - 1;
                    idxj = Integer.parseInt(connection.getTargetUnit()) - 1;
                } else if (polymerIdx > 0) {
                    for (int polymerNumber = 0; polymerNumber < polymerIdx; polymerNumber++) {
                        idxi += polymerList.get(polymerNumber).getListMonomers().size();
                    }
                    idxj = idxi + Integer.parseInt(connection.getTargetUnit()) - 1;
                    idxi += Integer.parseInt(connection.getSourceUnit()) - 1;
                }
                Vertex leftVertex = moleculeGraph.getVertices().get(idxi);
                Vertex rightVertex = moleculeGraph.getVertices().get(idxj);
                leftVertex.addBothNeighbours(rightVertex);
                LOG.debug("Adding connection between monomers of same polymer successful.");
            } else {
                // There is a connection between monomers of different polymers.
                LOG.debug("Adding connection between monomers of different polymers.");
                String polymerID1 = connection.getSourceId().toString();
                String polymerID2 = connection.getTargetId().toString();
                Integer polymerIdx1 = polymerIDMap.get(polymerID1);
                Integer polymerIdx2 = polymerIDMap.get(polymerID2);
                int idxii = 0;
                if (polymerIdx1 == 0) {
                    idxii = Integer.parseInt(connection.getSourceUnit()) - 1;
                } else if (polymerIdx1 > 0) {
                    for (int polymerNumber = 0; polymerNumber < moleculeGraph.vertexCount(); polymerNumber++) {
                        if (polymerIDList.get(polymerNumber) == polymerIdx1) {
                            break;
                        }
                        idxii++;
                    }
                    idxii += Integer.parseInt(connection.getSourceUnit()) - 1;
                }
                int idxjj = 0;
                if (polymerIdx2 == 0) {
                    idxjj = Integer.parseInt(connection.getTargetUnit()) - 1;
                } else if (polymerIdx2 > 0) {
                    for (int polymerNumber2 = 0; polymerNumber2 < moleculeGraph.vertexCount(); polymerNumber2++) {
                        if (polymerIDList.get(polymerNumber2) == polymerIdx2) {
                            break;
                        }
                        idxjj++;
                    }
                    idxjj += Integer.parseInt(connection.getTargetUnit()) - 1;
                }
                Vertex leftVertex = moleculeGraph.getVertices().get(idxii);
                Vertex rightVertex = moleculeGraph.getVertices().get(idxjj);
                leftVertex.addBothNeighbours(rightVertex);
                LOG.debug("Adding connection between monomers of different polymers successful.");
            }
        }
        LOG.debug("Adding connections to molecule graph successful.");
        return moleculeGraph;
    }
}
