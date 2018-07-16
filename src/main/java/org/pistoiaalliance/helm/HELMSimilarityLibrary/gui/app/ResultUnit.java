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
package org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.app;

import java.text.DecimalFormat;

/**
 * ResultUnit class specifies the three parts of one Result Unit: 
 * id, notation, tanimoto.
 *
 * @author bueltel
 *
 */
public class ResultUnit {
	private Integer id;
	private String helmNotation;
	private String tanimotoValue;

	/**
	 * The ResultUnit constructor specifies the display of the similarity value
	 * with four decimal places.
	 * @param id the id of the HELM
	 * @param notation the HELM notation
	 * @param tanimoto the tanimoto value
	 */
	public ResultUnit(Integer id, String notation, Double tanimoto) {
		this.id = id;
		this.helmNotation = notation;
		this.tanimotoValue = new DecimalFormat("#0.0000").format(tanimoto*100) + " %";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHelmNotation() {
		return helmNotation;
	}

	public void setHelmNotation(String helmNotation) {
		this.helmNotation = helmNotation;
	}

	public String getTanimotoValue() {
		return tanimotoValue;
	}

	public void setTanimotoValue(Double tanimotoValue) {
		this.tanimotoValue = new DecimalFormat("#0.0000").format(tanimotoValue*100) + " %";
	}

}