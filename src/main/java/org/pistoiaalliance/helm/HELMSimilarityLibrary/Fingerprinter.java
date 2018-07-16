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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.helm.notation2.parser.notation.HELM2Notation;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Graph;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.MoleculeGraphUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fingerprinter class creates a hashed fingerprint from a set of paths
 * using SHA-2 as the kryptographical hash function.
 *
 * @author bueltel
 */
public class Fingerprinter {
    private static int size = 1024;
    private static final Logger LOG = LoggerFactory.getLogger(Fingerprinter.class);

    /**
     * Calculates the fingerprint for a given HELM notation by enumerating all of the monomer paths and representing
     * each path as one bit in the fingerprint of size 1024.
     *
     * @param notation Input HELM notation that is translated into a bitSet fingerprint
     * @return bit set fingerprint
     * @throws Exception if anything goes wrong
     */
    public static BitSet calculateFingerprint(HELM2Notation notation) throws Exception {
        Graph molecule;
        PathGenerator pathGenerator = new PathGenerator();
        Set<String> allPaths;
        BitSet fingerprint;

        molecule = MoleculeGraphUtils.buildMoleculeGraph(notation.getListOfPolymers(), notation.getListOfConnections());

        pathGenerator.findPaths(molecule);
        allPaths = pathGenerator.getPaths();

        fingerprint = getHashedFingerprint(allPaths);

        pathGenerator.clearPaths();
        allPaths.clear();

        return fingerprint;
    }

    /**
     * Calculates a combined fingerprint for a given HELM notation by enumerating all of the original monomer paths and
     * all of the monomer paths converted into natural analogs and representing each path as one bit in the fingerprint
     * of size 1024.
     *
     * @param notation Input HELM notation that is translated into a bitSet fingerprint
     * @return bit set fingerprint
     * @throws Exception if anything goes wrong
     */
    public static BitSet calculateFingerprintNaturalAnalogs(HELM2Notation notation) throws Exception {
        Graph molecule;
        PathGenerator pathGenerator = new PathGenerator();
        Set<String> allPaths;
        Set<String> allNaturalPaths;
        BitSet fingerprint;
        BitSet fingerprintNatural;

        molecule = MoleculeGraphUtils.buildMoleculeGraph(notation.getListOfPolymers(), notation.getListOfConnections());

        pathGenerator.findPaths(molecule);
        allPaths = pathGenerator.getPaths();
        allNaturalPaths = pathGenerator.getNaturalPaths();

        fingerprint = getHashedFingerprint(allPaths);
        fingerprintNatural = getHashedFingerprint(allNaturalPaths);

        fingerprintNatural.or(fingerprint);

        pathGenerator.clearPaths();
        allPaths.clear();
        allNaturalPaths.clear();

        return fingerprintNatural;
    }

    /**
     *
     * @param totalPaths Set of paths based on the monomers of a HELM notation
     * @return bit set fingerprint
     * @throws NoSuchAlgorithmException if anything goes wrong
     */
    public static BitSet getHashedFingerprint(Set<String> totalPaths) throws NoSuchAlgorithmException {
        BitSet bitSet = new BitSet(size);

        LOG.debug("Calculation of fingerprint started.");
        for(String path: totalPaths){
            bitSet.set(getBitposFromHashcode(path));
        }
        LOG.debug("Calculation of fingerprint successful.");
        return bitSet;
    }

    /**
     *
     * @param path The monomer path that is mapped to a bit position
     * @return int bit position
     * @throws NoSuchAlgorithmException if anything goes wrong
     */
    private static int getBitposFromHashcode(String path) throws NoSuchAlgorithmException {
        // Creates a message digest with the SHA-256 algorithm.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // Performs a final update on the digest using an array of the path (encoded to
        // a bytearray), then completes the digest computation.
        byte[] hash = digest.digest(path.getBytes(StandardCharsets.UTF_8));
        // Converts byte array to hexadecimal string.
        String hashString = DatatypeConverter.printHexBinary(hash);
        // Converts hexadecimal string to bitSet.
        BitSet pathBitSet = BitSet.valueOf(DatatypeConverter.parseHexBinary(hashString));
        pathBitSet = foldBitSet(pathBitSet);
        return getDecimalFromBitSet(pathBitSet);
    }

    /**
     *
     * @param bitSet input bit set fingerprint
     * @return int converted from bit set hash
     */
    private static int getDecimalFromBitSet(BitSet bitSet) {
        if (bitSet.isEmpty()) {
            return 0;
        } else {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < bitSet.size(); i++) {
                s.append(bitSet.get(i) ? 1 : 0);
            }

            String str = s.toString();
            str = str.substring(0, 10);

            int decimal = 0;
            int base0 = (int) '0';
            for (int i = 0; i < str.length(); i++) {
                decimal = decimal * 2 + ((int) str.charAt(i) - base0);
            }
            return decimal;
        }
    }

    /**
     * Function applies a folding-algorithm on a BitSet with the xor-operator until
     * there are only 10 bit positions left that can represent an integer between 0
     * and 1024.
     **/
    private static BitSet foldBitSet(BitSet pathBitSet) {
        LOG.debug("Folding bit set...");
        BitSet firstHalf = pathBitSet.get(0, pathBitSet.size() / 2);
        BitSet secondHalf = pathBitSet.get(pathBitSet.size() / 2, pathBitSet.size());
        BitSet temp = firstHalf;
        int size = 128;

        // BitSet is separated into two halves and the xor-operator is applied on it
        // until it is of the size of 10.
        while (size >= 10) {
            firstHalf.xor(secondHalf);

            temp = firstHalf;
            firstHalf = temp.get(0, size / 2);
            secondHalf = temp.get(size / 2, size);

            size = size / 2;
        }

        // Size of BitSet is now 16, xor has to be applied on six more bits.
        firstHalf = temp.get(0, 10);
        secondHalf = temp.get(10, 16);
        for (int i = 0; i < 6; i++) {
            boolean xorValue = (firstHalf.get(i) || secondHalf.get(i)) && !(firstHalf.get(i) && secondHalf.get(i));
            firstHalf.set(i, xorValue);
        }
        LOG.debug("Folding bit set successful.");
        return firstHalf;
    }
}
