package org.pistoiaalliance.helm.HELMSimilarityLibrary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.MonomerLoadingException;
import org.helm.notation2.exception.NotationException;
import org.helm.notation2.parser.notation.polymer.MonomerNotationUnit;
import org.junit.BeforeClass;
import org.junit.Test;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.exception.NaturalAnalogException;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Graph;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Vertex;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Vertex.Type;

public class PathGeneratorTest {

	static PathGenerator dfs;

	@BeforeClass
	public static void instantiate() {
		dfs = new PathGenerator();
	}

	@Test
	public void checkAndStorePathTest() {
		// Adding one path to dfs.totalPaths that is a palindrome of an existing path
		// and lexicographically smaller than the existing path stored in
		// dfs.totalPaths.
		Set<String> totalPathTest = new HashSet<>();

		totalPathTest.add("R");
		totalPathTest.add("AR");
		totalPathTest.add("RP");
		totalPathTest.add("RPR");
		totalPathTest.add("RPRP");

		dfs.totalPaths.add("R");
		dfs.totalPaths.add("RA");
		dfs.totalPaths.add("RP");
		dfs.totalPaths.add("RPR");
		dfs.totalPaths.add("RPRP");

		List<String> listNewPath = new ArrayList<>();
		listNewPath.add("A");
		listNewPath.add("R");
		List<String> listNewNatPath = new ArrayList<>();
		listNewNatPath.add("A");
		listNewNatPath.add("R");
		dfs.checkAndStorePath("AR", listNewPath, "AR", listNewNatPath);

		assertTrue("Paths are not equal!", totalPathTest.equals(dfs.totalPaths));
		dfs.clearPaths();
	}

	@Test
	public void checkAndStorePathNegativeTest() {
		// Negative Test. Adding one path to dfs.totalPaths that is a palindrome of an
		// existing path and lexicographically smaller than the existing path
		// stored in dfs.totalPath.
		Set<String> totalPathTest = new HashSet<>();

		totalPathTest.add("R");
		totalPathTest.add("RA");	//in dfs.totalPaths "RA" is replaced by "AR" in dfs.totalPaths because it is lexicographically smaller
		totalPathTest.add("RP");
		totalPathTest.add("RPR");
		totalPathTest.add("RPRP");

		dfs.totalPaths.add("R");
		dfs.totalPaths.add("RA");
		dfs.totalPaths.add("RP");
		dfs.totalPaths.add("RPR");
		dfs.totalPaths.add("RPRP");

		List<String> listNewPath = new ArrayList<>();
		listNewPath.add("A");
		listNewPath.add("R");
		List<String> listNewNatPath = new ArrayList<>();
		listNewNatPath.add("A");
		listNewNatPath.add("R");
		dfs.checkAndStorePath("AR", listNewPath, "AR", listNewNatPath);

		assertFalse("Paths are equal but should not be!", totalPathTest.equals(dfs.totalPaths));
		dfs.clearPaths();
	}

	@Test
	public void checkAndStoreNaturalPathTest() {
		// Adding one path to dfs.totalPaths and its natural paths to dfs.totalNaturalPaths
		// that is a palindrome of an existing path and lexicographically smaller than the
		// existing path stored in dfs.totalPaths and dfs.totalNaturalPaths.
		Set<String> totalPathTest = new HashSet<>();
		Set<String> totalNaturalPathTest = new HashSet();

		totalPathTest.add("[dR]");
		totalPathTest.add("A[dR]");
		totalPathTest.add("[dR]P");
		totalPathTest.add("[dR]P[LR]");
		totalPathTest.add("[dR]P[LR][sP]");

		totalNaturalPathTest.add("R");
		totalNaturalPathTest.add("AR");
		totalNaturalPathTest.add("RP");
		totalNaturalPathTest.add("RPR");
		totalNaturalPathTest.add("RPRP");

		dfs.totalPaths.add("[dR]");
		dfs.totalPaths.add("A[dR]");
		dfs.totalPaths.add("[dR]P");
		dfs.totalPaths.add("[dR]P[LR]");
		dfs.totalPaths.add("[dR]P[LR][sP]");

		dfs.totalNaturalPaths.add("R");
		dfs.totalNaturalPaths.add("AR");
		dfs.totalNaturalPaths.add("RP");
		dfs.totalNaturalPaths.add("RPR");
		dfs.totalNaturalPaths.add("RPRP");

		List<String> listNewPath = new ArrayList<>();
		listNewPath.add("A");
		listNewPath.add("[dR]");
		List<String> listNewNatPath = new ArrayList<>();
		listNewNatPath.add("A");
		listNewNatPath.add("R");
		dfs.checkAndStorePath("A[dR]", listNewPath, "AR", listNewNatPath);

		assertTrue("Paths are not equal!", totalPathTest.equals(dfs.totalPaths));
		assertTrue("Natural paths are not equal!", totalNaturalPathTest.equals(dfs.totalNaturalPaths));
		dfs.clearPaths();
	}

	@Test
	public void checkAndStoreNaturalPathNegativeTest() {
		// Negative Test. Adding one path to dfs.totalPaths and its natural paths to
		// dfs.totalNaturalPaths that is a palindrome of an existing path and
		// lexicographically smaller than the existing path stored in dfs.totalPaths
		// and dfs.totalNaturalPaths.
		Set<String> totalPathTest = new HashSet<>();
		Set<String> totalNaturalPathTest = new HashSet();

		totalPathTest.add("[dR]");
		totalPathTest.add("[dR]A");
		totalPathTest.add("[dR]P");
		totalPathTest.add("[dR]P[LR]");
		totalPathTest.add("[dR]P[LR][sP]");

		totalNaturalPathTest.add("R");
		totalNaturalPathTest.add("AR");
		totalNaturalPathTest.add("RP");
		totalNaturalPathTest.add("RPR");
		totalNaturalPathTest.add("RPRP");

		dfs.totalPaths.add("[dR]");
		dfs.totalPaths.add("A[dR]");
		dfs.totalPaths.add("[dR]P");
		dfs.totalPaths.add("[dR]P[LR]");
		dfs.totalPaths.add("[dR]P[LR][sP]");

		dfs.totalNaturalPaths.add("R");
		dfs.totalNaturalPaths.add("AR");
		dfs.totalNaturalPaths.add("RP");
		dfs.totalNaturalPaths.add("RPR");
		dfs.totalNaturalPaths.add("RPRP");

		List<String> listNewPath = new ArrayList<>();
		listNewPath.add("A");
		listNewPath.add("[dR]");
		List<String> listNewNatPath = new ArrayList<>();
		listNewNatPath.add("A");
		listNewNatPath.add("R");
		dfs.checkAndStorePath("A[dR]", listNewPath, "AR", listNewNatPath);
		assertFalse("Paths are equal but should not be!", totalPathTest.equals(dfs.totalPaths));
		assertTrue("Natural paths are not equal!", totalNaturalPathTest.equals(dfs.totalNaturalPaths));
		dfs.clearPaths();
	}

	@Test
	public void findPathsTest() throws MonomerLoadingException, ChemistryException, NotationException, NaturalAnalogException, org.helm.notation2.parser.exceptionparser.NotationException {
		Set<String> totalPaths = new HashSet<>();
		Set<String> totalNaturalPaths = new HashSet<>();
		Vertex v1 = new Vertex(new MonomerNotationUnit("[LR]","RNA"), Type.RNA, true, false, 0);
		Vertex v2 = new Vertex(new MonomerNotationUnit("A","RNA"), Type.RNA, false, true, 1);
		Vertex v3 = new Vertex(new MonomerNotationUnit("P","RNA"), Type.RNA, false, true, 2);
		Vertex v4 = new Vertex(new MonomerNotationUnit("[LR]","RNA"), Type.RNA, true, false, 3);
		Vertex v5 = new Vertex(new MonomerNotationUnit("A","RNA"), Type.RNA, false, true, 4);
		Vertex v6 = new Vertex(new MonomerNotationUnit("[Test_m]","CHEM"), Type.CHEM, false, false, 5);
		v1.addBothNeighbours(v2);
		v1.addBothNeighbours(v3);
		v3.addBothNeighbours(v4);
		v4.addBothNeighbours(v5);
		v6.addBothNeighbours(v1);
		List<Vertex> vertexList = new ArrayList<>();
		vertexList.add(v1);
		vertexList.add(v2);
		vertexList.add(v3);
		vertexList.add(v4);
		vertexList.add(v5);
		vertexList.add(v6);
		Graph testGraph = new Graph(vertexList);

		totalPaths.add("[LR]");
		totalPaths.add("a");
		totalPaths.add("p");
		totalPaths.add("[Test_m]");
		totalPaths.add("[LR]a");
		totalPaths.add("[LR]p");
		totalPaths.add("[LR]p[LR]");
		totalPaths.add("[LR]p[LR]a");
		totalPaths.add("[LR][Test_m]");
		totalPaths.add("a[LR]p");
		totalPaths.add("a[LR]p[LR]a");
		totalPaths.add("[Test_m][LR]a");
		totalPaths.add("[Test_m][LR]p");
		totalPaths.add("[LR]p[LR][Test_m]");
		totalPaths.add("[Test_m][LR]p[LR]a");

		totalNaturalPaths.add("r");
		totalNaturalPaths.add("a");
		totalNaturalPaths.add("p");
		totalNaturalPaths.add("[Test_m]");
		totalNaturalPaths.add("ar");
		totalNaturalPaths.add("pr");
		totalNaturalPaths.add("rpr");
		totalNaturalPaths.add("arpr");
		totalNaturalPaths.add("arp");
		totalNaturalPaths.add("arpra");
		totalNaturalPaths.add("[Test_m]r");
		totalNaturalPaths.add("[Test_m]ra");
		totalNaturalPaths.add("[Test_m]rp");
		totalNaturalPaths.add("[Test_m]rpr");
		totalNaturalPaths.add("[Test_m]rpra");

		dfs.findPaths(testGraph);

		assertTrue("Paths are not equal!", totalPaths.equals(dfs.totalPaths));
		assertTrue("Paths are not equal!", totalNaturalPaths.equals(dfs.totalNaturalPaths));
		dfs.clearPaths();
	}

	@Test
	public void unequalPathsTest() throws NotationException, MonomerLoadingException, ChemistryException, NaturalAnalogException, org.helm.notation2.parser.exceptionparser.NotationException {
		Set<String> totalPathsRNA = new HashSet<>();
		Set<String> totalNaturalPathsRNA = new HashSet<>();
		Set<String> totalPathsPeptide = new HashSet<>();
		Set<String> totalNaturalPathsPeptide = new HashSet<>();

		Vertex v1 = new Vertex(new MonomerNotationUnit("R","RNA"), Type.RNA, false, true, 0);
		Vertex v2 = new Vertex(new MonomerNotationUnit("A","RNA"), Type.RNA, false, true, 1);
		Vertex v3 = new Vertex(new MonomerNotationUnit("P","RNA"), Type.RNA, false, true, 2);
		Vertex v4 = new Vertex(new MonomerNotationUnit("R","RNA"), Type.RNA, false, true, 3);
		Vertex v5 = new Vertex(new MonomerNotationUnit("C","RNA"), Type.RNA, false, true, 4);


		Vertex v7 = new Vertex(new MonomerNotationUnit("R", "PEPTIDE"), Type.PEPTIDE, false, false, 0);
		Vertex v8 = new Vertex(new MonomerNotationUnit("A", "PEPTIDE"), Type.PEPTIDE, false, false, 1);
		Vertex v9 = new Vertex(new MonomerNotationUnit("P", "PEPTIDE"), Type.PEPTIDE, false, false, 2);
		Vertex v10 = new Vertex(new MonomerNotationUnit("R", "PEPTIDE"), Type.PEPTIDE, false, false, 3);
		Vertex v11 = new Vertex(new MonomerNotationUnit("C", "PEPTIDE"), Type.PEPTIDE, false, false, 4);

		v1.addBothNeighbours(v2);
		v1.addBothNeighbours(v3);
		v3.addBothNeighbours(v4);
		v4.addBothNeighbours(v5);

		v7.addBothNeighbours(v8);
		v8.addBothNeighbours(v9);
		v9.addBothNeighbours(v10);
		v10.addBothNeighbours(v11);

		List<Vertex> vertexListRNA = new ArrayList<>();
		vertexListRNA.add(v1);
		vertexListRNA.add(v2);
		vertexListRNA.add(v3);
		vertexListRNA.add(v4);
		vertexListRNA.add(v5);
		Graph testGraphRNA = new Graph(vertexListRNA);

		List<Vertex> vertexListPeptide = new ArrayList<>();
		Graph testGraphPeptide = new Graph(vertexListPeptide);
		vertexListPeptide.add(v7);
		vertexListPeptide.add(v8);
		vertexListPeptide.add(v9);
		vertexListPeptide.add(v10);
		vertexListPeptide.add(v11);

		totalPathsRNA.add("r");
		totalPathsRNA.add("a");
		totalPathsRNA.add("p");
		totalPathsRNA.add("c");
		totalPathsRNA.add("ar");
		totalPathsRNA.add("arp");
		totalPathsRNA.add("arpr");
		totalPathsRNA.add("arprc");
		totalPathsRNA.add("pr");
		totalPathsRNA.add("rpr");
		totalPathsRNA.add("crpr");
		totalPathsRNA.add("crp");
		totalPathsRNA.add("cr");

		totalNaturalPathsRNA.add("r");
		totalNaturalPathsRNA.add("a");
		totalNaturalPathsRNA.add("p");
		totalNaturalPathsRNA.add("c");
		totalNaturalPathsRNA.add("ar");
		totalNaturalPathsRNA.add("arp");
		totalNaturalPathsRNA.add("arpr");
		totalNaturalPathsRNA.add("arprc");
		totalNaturalPathsRNA.add("pr");
		totalNaturalPathsRNA.add("rpr");
		totalNaturalPathsRNA.add("crpr");
		totalNaturalPathsRNA.add("crp");
		totalNaturalPathsRNA.add("cr");

		totalPathsPeptide.add("R");
		totalPathsPeptide.add("A");
		totalPathsPeptide.add("P");
		totalPathsPeptide.add("C");
		totalPathsPeptide.add("AR");
		totalPathsPeptide.add("PAR");
		totalPathsPeptide.add("RAPR");
		totalPathsPeptide.add("CRPAR");
		totalPathsPeptide.add("AP");
		totalPathsPeptide.add("APR");
		totalPathsPeptide.add("APRC");
		totalPathsPeptide.add("PR");
		totalPathsPeptide.add("CRP");
		totalPathsPeptide.add("CR");

		totalNaturalPathsPeptide.add("R");
		totalNaturalPathsPeptide.add("A");
		totalNaturalPathsPeptide.add("P");
		totalNaturalPathsPeptide.add("C");
		totalNaturalPathsPeptide.add("AR");
		totalNaturalPathsPeptide.add("PAR");
		totalNaturalPathsPeptide.add("RAPR");
		totalNaturalPathsPeptide.add("CRPAR");
		totalNaturalPathsPeptide.add("AP");
		totalNaturalPathsPeptide.add("APR");
		totalNaturalPathsPeptide.add("APRC");
		totalNaturalPathsPeptide.add("PR");
		totalNaturalPathsPeptide.add("CRP");
		totalNaturalPathsPeptide.add("CR");

		dfs.findPaths(testGraphRNA);

		assertTrue("Paths are not equal!", totalPathsRNA.equals(dfs.totalPaths));
		assertTrue("Paths are not equal!", totalNaturalPathsRNA.equals(dfs.totalNaturalPaths));
		dfs.clearPaths();

		dfs.findPaths(testGraphPeptide);
		assertTrue("Paths are not equal!", totalPathsPeptide.equals(dfs.totalPaths));
		assertTrue("Paths are not equal!", totalNaturalPathsPeptide.equals(dfs.totalNaturalPaths));
		dfs.clearPaths();
	}

}
