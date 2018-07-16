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
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * MyBitSet class extends java.util.BitSet to create a list from a string of
 * integers that represent the bits that are set and create a java.util.BitSet
 * from the list of set bits.
 *
 * @author bueltel
 */
public class MyBitSet extends BitSet {

    public MyBitSet(int size) {
        super(size);
    }

    public MyBitSet(String bitSetString) {
        List<String> bitPosString;
        List<Integer> bitPosInt = new ArrayList<>();
        bitSetString = bitSetString.substring(1);
        bitSetString = bitSetString.substring(0, bitSetString.length() - 1);
        bitPosString = Arrays.asList(bitSetString.split(", "));
        for(String pos: bitPosString){
            bitPosInt.add(Integer.parseInt(pos));
        }
        for(Integer bit: bitPosInt){
            this.set(bit);
        }
    }
}