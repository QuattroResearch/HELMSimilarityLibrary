package org.pistoiaalliance.helm.HELMSimilarityLibrary;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.helm.notation2.exception.NotationException;
import org.helm.notation2.parser.ConverterHELM1ToHELM2;
import org.helm.notation2.parser.ParserHELM2;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.notation.HELM2Notation;
import org.helm.notation2.parser.notation.polymer.MonomerNotationUnit;
import org.junit.Before;
import org.junit.Test;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Graph;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.MoleculeGraphUtils;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Vertex;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Vertex.Type;

public class MoleculeGraphTest {

	HELM2Notation notation = new HELM2Notation();

	@Before
	public void getHELM2notation() {
		String testhelm = "RNA1{[LR](A)P.[LR](A)}|CHEM1{[Test_m]}$CHEM1,RNA1,1:R1-1:R1$$$V2.0";
		ConverterHELM1ToHELM2 converter = new ConverterHELM1ToHELM2();
		ParserHELM2 parser = new ParserHELM2();
		testhelm = converter.doConvert(testhelm);
		try {
			parser.parse(testhelm);
		} catch (ExceptionState e) {
			e.printStackTrace();
		}
		notation = parser.getHELM2Notation();
	}

	@Test
	public void buildGraphTest() throws NotationException, org.helm.notation2.parser.exceptionparser.NotationException {
		Vertex v1 = new Vertex(new MonomerNotationUnit("[LR]","RNA"), Type.RNA, true, true, 0);
		Vertex v2 = new Vertex(new MonomerNotationUnit("A","RNA"), Type.RNA, false, true, 1);
		Vertex v3 = new Vertex(new MonomerNotationUnit("P","RNA"), Type.RNA, false, true, 2);
		Vertex v4 = new Vertex(new MonomerNotationUnit("[LR]","RNA"), Type.RNA, true, true, 3);
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
		Graph expectedGraph = new Graph(vertexList);

		Graph testGraph = new Graph();
		testGraph = MoleculeGraphUtils.buildMoleculeGraph(notation.getListOfPolymers(), notation.getListOfConnections());
		assertTrue("Graphs are not equal!", expectedGraph.equals(testGraph));
	}

}
