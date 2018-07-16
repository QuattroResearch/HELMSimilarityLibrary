package org.pistoiaalliance.helm.HELMSimilarityLibrary;

import org.helm.notation2.parser.ConverterHELM1ToHELM2;
import org.helm.notation2.parser.ParserHELM2;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.notation.HELM2Notation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FingerprintTest {
    HELM2Notation parentNotation;
    HELM2Notation childNotation;

    @Before
    public void initializeNotations() throws ExceptionState {
        String parentHELM = "RNA1{R(A)P.R(G)P}$$$$V2.0";
		String childHELM = "RNA1{R(A)P.R(G)P.R(C)P}$$$$V2.0";
	    ConverterHELM1ToHELM2 converter = new ConverterHELM1ToHELM2();
	    ParserHELM2 parserHELM2 = new ParserHELM2();
	    parentHELM = converter.doConvert(parentHELM);
	    parserHELM2.parse(parentHELM);
	    parentNotation = parserHELM2.getHELM2Notation();

	    ConverterHELM1ToHELM2 converter2 = new ConverterHELM1ToHELM2();
	    ParserHELM2 parserHELM2two = new ParserHELM2();
	    childHELM = converter2.doConvert(childHELM);
	    parserHELM2two.parse(childHELM);
	    childNotation = parserHELM2two.getHELM2Notation();
	}


	@Test
	public void testSubset() throws Exception {
		Assert.assertEquals(true, Subset.checkHelmRelationship(parentNotation, childNotation));
	}

	@Test
	public void testNegativeSubset() throws Exception {
		Assert.assertEquals(false, Subset.checkHelmRelationship(childNotation, parentNotation));
	}

	@Test
	public void testSimilarity() throws Exception {
		Assert.assertEquals( 0.6, Similarity.calculateSimilarity(parentNotation, childNotation), 0.1);
	}

	@Test
	public void testSimilarityNatAnalogs() throws Exception {
		Assert.assertEquals( 0.6, Similarity.calculateSimilarityNatAnalogs(parentNotation, childNotation), 0.1);
	}

	@Test
	public void testIdenticalHELM() throws Exception {
		Assert.assertEquals(1, Similarity.calculateSimilarity(parentNotation, parentNotation), 0);
	}

}
