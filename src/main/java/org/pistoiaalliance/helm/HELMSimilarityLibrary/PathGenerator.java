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
package org.pistoiaalliance.helm.HELMSimilarityLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.helm.notation2.Monomer;
import org.helm.notation2.MonomerFactory;
import org.helm.notation2.MonomerStore;
import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.MonomerLoadingException;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.exception.NaturalAnalogException;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Graph;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  PathGenerator class takes a moleculeGraph and finds all path from each
 *  monomer (vertex) up to a maximal length (searchDepth). Paths are
 *  found by depth first search.
 * 
 * @author bueltel
 *
 */
public class PathGenerator {
	private static int searchDepth = 6;
	public static Set<String> totalPaths;
	public static Set<String> totalNaturalPaths;
	private static Map<String, Map<String, Monomer>> monStore;

	final Logger LOG = LoggerFactory.getLogger(PathGenerator.class);

	public PathGenerator() {
		totalPaths = new HashSet<>();
		totalNaturalPaths = new HashSet<>();
		MonomerStore store;
		try {
			store = MonomerFactory.getInstance().getMonomerStore();
			monStore = store.getMonomerDB();
		} catch (MonomerLoadingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChemistryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  Calls depthFirstSearch for every monomer (vertex).
	 * @param moleculeGraph The moleculeGraph of a HELM notation
	 * @throws NaturalAnalogException if natural analog of a modified monomer cannot be found
	 */
	public void findPaths(Graph moleculeGraph) throws NaturalAnalogException {
		int numOfMonomers = moleculeGraph.getVertices().size();

		for(int i = 0; i < numOfMonomers; i++) {
			Vertex rootVertex = new Vertex(moleculeGraph.getVertices().get(i));
			String monomerUnit = rootVertex.getMonomer().getUnit();
			String naturalAnalog = "";
			List<String> currentOrigPathList = new ArrayList<>();
			List<String> currentNatPathList = new ArrayList<>();

			// Generate original path.
//			// If vertex is RNA, get unique unit for RNA monomer.
			if(rootVertex.hasUniqueUnit()) {
				monomerUnit = monomerUnit.toLowerCase();
			}

			String currentOrigPath = monomerUnit;
			// The original path list is to keep track of the length of path in terms of monomers.
			currentOrigPathList.add(monomerUnit);

			// Generate path with natural analogs.
			// Remove squared brackets to be able to compare non-natural analog with monomer store.
			if(rootVertex.isNonNatural()) {
				monomerUnit = monomerUnit.substring(1, monomerUnit.length()-1);

				// Get natural analog of peptide monomer.
				if(rootVertex.isPeptide()) {
					naturalAnalog = getNaturalPeptide(monomerUnit);
				}

				// Get natural analog of RNA monomer.
				if(rootVertex.isRNA()) {
					naturalAnalog = getNaturalRNA(monomerUnit);
				}

				// No need for natural analog of CHEM.
				if(rootVertex.isChem()) {
					naturalAnalog = rootVertex.getMonomer().getUnit();
				}
			}
			else {
				// If vertex is RNA, get unique unit for natural RNA monomer.
				if(rootVertex.hasUniqueUnit()) {
					naturalAnalog = rootVertex.getMonomer().getUnit().toLowerCase();
				}
				else {
					naturalAnalog = rootVertex.getMonomer().getUnit();
				}
			}

			String currentNatPath = naturalAnalog;
			// The natural path list is to keep track of the length of path in terms of natural monomers
			currentNatPathList.add(currentNatPath);
			checkAndStorePath(currentOrigPath, currentOrigPathList, currentNatPath, currentNatPathList);
			List<Boolean> visited = new ArrayList<>(Arrays.asList(new Boolean[numOfMonomers]));
			depthFirstSearch(moleculeGraph, visited, i, currentOrigPath, currentOrigPathList, currentNatPath,
					currentNatPathList, 0);
		}

		// print all paths
//		printPaths();
	}

	/**
	 * Checks if path already exists as palindrome, stores valide paths depending
	 * on lexicographical order.
	 * @param newOrigPath original path to be checked
	 * @param listNewOrigPath list of original path to be checked
	 * @param newNatPath natural path to be checked
	 * @param listNewNatPath list of natural path to be checked
	 */
	public static void checkAndStorePath(String newOrigPath, List<String> listNewOrigPath, String newNatPath,
										 List<String> listNewNatPath) {
		List<String> listReverseOrigPath = new LinkedList<>(listNewOrigPath);
		Collections.reverse(listReverseOrigPath);
		String reverseOrigPath = String.join("", listReverseOrigPath);

		// Check if current path is palindrome of existing path, only store the
		// lexicographical minimum of both path.
		if(totalPaths.contains(reverseOrigPath)) {
			for(int i = 0; i < reverseOrigPath.length(); i++) {
				if(reverseOrigPath.charAt(i) == (newOrigPath.charAt(i))) {
					continue;
				}
				else if (reverseOrigPath.charAt(i) < (newOrigPath.charAt(i))) {
					break;
				}
				else {
					totalPaths.remove(reverseOrigPath);
					totalPaths.add(newOrigPath);
					break;
				}
			}
		}
		else {
			totalPaths.add(newOrigPath);
		}

		List<String> listReverseNatPath = new LinkedList<>(listNewNatPath);
		Collections.reverse(listReverseNatPath);
		String reverseNatPath = String.join("", listReverseNatPath);

		// Check the same for path with natural analogs.
		if(totalNaturalPaths.contains(reverseNatPath)) {
			for(int i = 0; i < reverseNatPath.length(); i++) {
				if(reverseNatPath.charAt(i) == (newNatPath.charAt(i))) {
					continue;
				}
				else if (reverseNatPath.charAt(i) < (newNatPath.charAt(i))) {
					break;
				}
				else {
					totalNaturalPaths.remove(reverseNatPath);
					totalNaturalPaths.add(newNatPath);
					break;
				}
			}
		}
		else {
			totalNaturalPaths.add(newNatPath);
		}
	}


	/**
	 * DepthFirstSearch through moleculeGraph from a startvertex.
	 *
	 * @param polymerGraph moleculegraph of a polymer
	 * @param visited list of visited monomer indices
	 * @param rootVertexIdx the start vertex of this loop
	 * @param currentOrigPath the current original path
	 * @param currentOrigPathList the current original path list
	 * @param currentNatPath the current natural path
	 * @param currentNatPathList the current natural path list
	 * @param currentDepth the current search depth
	 */
	private void depthFirstSearch(Graph polymerGraph, List<Boolean> visited, int rootVertexIdx, String currentOrigPath,
								  List<String> currentOrigPathList, String currentNatPath,
								  List<String> currentNatPathList, int currentDepth) {
		currentDepth++;
		Vertex rootVertex = new Vertex(polymerGraph.getVertices().get(rootVertexIdx));
		List<Vertex> neighbourVertices = rootVertex.getNeighbourList();

		for(int i = 0; i < neighbourVertices.size(); i++) {
			if (currentDepth == 1) {
				Collections.fill(visited, false);
				visited.set(rootVertexIdx, true);
			}

			if(!visited.get(neighbourVertices.get(i).getIndex())) {

				Vertex neighbourVertex = new Vertex(neighbourVertices.get(i));
				String naturalAnalog = "";
				String monomerUnit = neighbourVertex.getMonomer().getUnit();

				// Generate original path.
				// Get unique units for natural RNA monomers.
				if(neighbourVertex.hasUniqueUnit()) {
					monomerUnit = monomerUnit.toLowerCase();
				}
				String newOrigPath = currentOrigPath + monomerUnit;
				// The original path list is to keep track of the length of path in terms of monomers.
				currentOrigPathList.add(monomerUnit);

				// Generate path with natural analogs.
				// Remove squared brackets to be able to compare non-natural analog with monomer store.
				if(neighbourVertex.isNonNatural()) {
					monomerUnit = monomerUnit.substring(1, monomerUnit.length()-1);

					// Get natural analog of peptide monomer.
					if(neighbourVertex.isPeptide()) {
						naturalAnalog = getNaturalPeptide(monomerUnit);
					}

					// Get natural analog of RNA monomer.
					else if(neighbourVertex.isRNA()) {
						naturalAnalog = getNaturalRNA(monomerUnit);
					}

					//No need for natural analog of CHEM.
					else if(neighbourVertex.isChem()) {
						naturalAnalog = neighbourVertex.getMonomer().getUnit();
					}
				}
				else {
					// If vertex is RNA, get unique unit for natural RNA monomer.
					if(neighbourVertex.hasUniqueUnit()) {
						naturalAnalog = neighbourVertex.getMonomer().getUnit().toLowerCase();
					}
					else {
						naturalAnalog = neighbourVertex.getMonomer().getUnit();
					}
				}

				String newNatPath = currentNatPath + naturalAnalog;

				// The natural path list is to keep track of the length of path in terms of monomers.
				currentNatPathList.add(naturalAnalog);

				// Recursive call of DFS.
				if(currentDepth < searchDepth) {
					checkAndStorePath(newOrigPath, currentOrigPathList, newNatPath, currentNatPathList);
					visited.set(neighbourVertex.getIndex(), true);
					depthFirstSearch(polymerGraph, visited, neighbourVertex.getIndex(), newOrigPath,
							currentOrigPathList, newNatPath, currentNatPathList, currentDepth);
					visited.set(neighbourVertex.getIndex(), false);
					currentOrigPathList.remove(currentOrigPathList.size() - 1);
					currentNatPathList.remove(currentNatPathList.size() - 1);
				}
				else {
					currentOrigPathList.remove(currentOrigPathList.size() - 1);
					currentNatPathList.remove(currentNatPathList.size() - 1);
				}
			}
		}
	}

	/**
	 * Gets the natural analog of a peptide monomer. If no natural analog is found,
	 * the original monomer is returned.
	 *
	 * @param monomerUnit The modified monomer that the natural analog is looked for
	 * @return natural analog string
	 */
	public String getNaturalPeptide(String monomerUnit) {
		for(Map.Entry<String, Map<String, Monomer>> polymerType : monStore.entrySet()) {
			if(polymerType.getKey().equalsIgnoreCase("Peptide")) {
				Map<String, Monomer> peptideMap = polymerType.getValue();
				for (Map.Entry<String, Monomer> monomer : peptideMap.entrySet()) {
					if(monomer.getValue().getAlternateId().equalsIgnoreCase(monomerUnit)){
						return monomer.getValue().getNaturalAnalog();
					}
				}
			}
		}
		LOG.warn("Could not find natural analog to monomer");
		return "[" + monomerUnit + "]";
//		throw new NaturalAnalogException("Couldn't find natural analog to monomer");
	}

	/**
	 * Gets the natural analog of a RNA monomer. If no natural analog is found,
	 * the original monomer is returned.
	 * @param monomerUnit The modified monomer that the natural analog is looked for
	 * @return natural analog string
	 */
	public String getNaturalRNA(String monomerUnit) {
		for(Map.Entry<String, Map<String, Monomer>> polymerType : monStore.entrySet()) {
			if(polymerType.getKey().equalsIgnoreCase("RNA")) {
				Map<String, Monomer> rnaMap = polymerType.getValue();
				for (Map.Entry<String, Monomer> monomer: rnaMap.entrySet()) {
					if(monomer.getValue().getAlternateId().equalsIgnoreCase(monomerUnit)) {
						return monomer.getValue().getNaturalAnalog().toLowerCase();
					}
				}
			}
		}
		LOG.warn("Could not find natural analog to monomer");
		return "[" + monomerUnit + "]";
//		throw new NaturalAnalogException("Couldn't find natural analog to monomer");
	}

	public Set<String> getPaths(){
		return totalPaths;
	}

	public Set<String> getNaturalPaths(){
		return totalNaturalPaths;
	}

	public void clearPaths() {
		totalPaths.clear();
		totalNaturalPaths.clear();
	}
}
