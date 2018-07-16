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

import org.helm.notation2.parser.ConverterHELM1ToHELM2;
import org.helm.notation2.parser.ParserHELM2;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.notation.HELM2Notation;

/**
 * HELM2Object class to generate HELM2Notation object from HELM string
 *
 * @author bueltel
 */
public class HELM2Object {

	private static ConverterHELM1ToHELM2 converter = new ConverterHELM1ToHELM2();
	private static ParserHELM2 parser = new ParserHELM2();

	/**
	 * Generates HELM2Notation object from HELM string
	 *
	 * @param helm helmString
	 * @return HELM2Notation object
	 * @throws ExceptionState if anything goes wrong
	 */
	public static HELM2Notation makeHELM2NotationObject(String helm) throws ExceptionState {

		HELM2Notation notation;

		helm = converter.doConvert(helm);
		parser.parse(helm);
		notation = parser.getHELM2Notation();

		return notation;
	}
}
