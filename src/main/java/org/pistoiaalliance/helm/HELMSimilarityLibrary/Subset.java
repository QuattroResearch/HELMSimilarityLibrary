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
 * Subset class calculates the substructure relationship of two HELM notations.
 *
 * @author bueltel
 *
 */
public class Subset {

    /**
     * Method checks if the parentNotation is completely enclosed in the childNotation.
     *
     * @param parentNotation the parent notation to be checked
     * @param childNotation the child notation to be checked
     * @return boolean true if parent
     * @throws Exception if anything goes wrong
     */
    public static Boolean checkHelmRelationship(HELM2Notation parentNotation, HELM2Notation childNotation)
            throws Exception{
        BitSet parentFingerprint = Fingerprinter.calculateFingerprint(parentNotation);
        BitSet childFingerprint = Fingerprinter.calculateFingerprint(childNotation);

        return checkHelmRelationship(parentFingerprint, childFingerprint);
    }

    /**
     * Method checks if parentFprint is a subset of childFprint and returns true
     * in that case.
     *
     * @param parentFprint the parent fingerprint to be checked
     * @param childFprint the child fingerprint to be checked
     * @return boolean true if parent
     */
    public static Boolean checkHelmRelationship(BitSet parentFprint, BitSet childFprint){
        BitSet tempParentFprint = (BitSet) parentFprint.clone();

        tempParentFprint.and(childFprint);

        return tempParentFprint.equals(parentFprint);
    }
}
