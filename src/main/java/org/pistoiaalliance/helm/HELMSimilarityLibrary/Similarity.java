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

import java.util.BitSet;

import org.helm.notation2.parser.notation.HELM2Notation;

/**
 * Similarity class calculates the Tanimoto value of two hashed bitstring fingerprints.
 *
 * @author bueltel
 *
 */
public class Similarity {

	/**
	 * Method calculates the similarity (tanimoto coefficient) of two HELM notations
	 *
	 * @param notation1 the first HELM notation
	 * @param notation2 the second HELM notation
	 * @return double tanimoto coefficient
	 * @throws Exception if anything goes wrong
	 */
	public static double calculateSimilarity(HELM2Notation notation1, HELM2Notation notation2) throws Exception {
		BitSet fingerprint1 = Fingerprinter.calculateFingerprint(notation1);
		BitSet fingerprint2 = Fingerprinter.calculateFingerprint(notation2);
		return calculateSimilarity(fingerprint1, fingerprint2);
	}

	/**
	 * Method calculates the similarity (tanimoto coefficient) of two HELM notations and takes
	 * the natural analogs of modified monomers into account.
	 *
	 * @param notation1 the first HELM notation
	 * @param notation2 the second HELM notation
	 * @return double tanimoto coefficient
	 * @throws Exception if anything goes wrong
	 */
	public static double calculateSimilarityNatAnalogs(HELM2Notation notation1, HELM2Notation notation2)
			throws Exception {
		BitSet fingerprint1 = Fingerprinter.calculateFingerprintNaturalAnalogs(notation1);
		BitSet fingerprint2 = Fingerprinter.calculateFingerprintNaturalAnalogs(notation2);
		return calculateSimilarity(fingerprint1, fingerprint2);
	}

	/**
	 * Method calculates the similarity (tanimoto coefficient) of two bitset fingerprints.
	 *
	 * @param helmBitFingerprintI the first bit set fingerprint
	 * @param helmBitFingerprintJ the second bit set fingerprint
	 * @return double tanimoto coefficient
	 */
	public static double calculateSimilarity(BitSet helmBitFingerprintI, BitSet helmBitFingerprintJ) {
		BitSet resultHELMFingerprint;
		resultHELMFingerprint = (BitSet) helmBitFingerprintI.clone();

		resultHELMFingerprint.and(helmBitFingerprintJ);
		double denominator = helmBitFingerprintI.cardinality() + helmBitFingerprintJ.cardinality() -
				resultHELMFingerprint.cardinality();

		return resultHELMFingerprint.cardinality() / denominator;
	}
}