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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.helm.notation2.exception.ChemistryException;
import org.helm.notation2.exception.MonomerLoadingException;
import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.notation.HELM2Notation;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.HELM2Object;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.exception.NaturalAnalogException;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.AlertBox;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyButton;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyCheckBox;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyLabel;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyRadioButton;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyText;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyTextField;

import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

/**
 * Options class handles the options section of the user interface.
 * 
 * @author bueltel
 *
 */
public class Options extends Main {
	private ToggleGroup radioButtonGroup;
	private ToggleGroup simSearchGroup;
	protected static MyRadioButton radioSimSearch;
	protected static MyRadioButton radioSubset;
	protected MyLabel title;
	protected MyLabel helmText;
	protected MyRadioButton scrollCheckBox;
	protected static MyButton button;
	protected MyTextField queryHelmTextField;
	private HELM2Notation queryNotation;
	protected MyTextField tanimotoTextField;
	private int defaultTanimoto = 50;
	private double desiredSimilarity;
	protected MyText tanimotoUnit;
	protected ScrollBar scrollBar;
	protected static MyRadioButton tenSimilarCheckBox;
	protected static MyCheckBox analogsCheckBox;

	/**
	 * Options constructor instantiates all of the controls of the interfaces option
	 * section and calls several methods to set the controls properties and event
	 * handling.
	 */
	public Options() {
		radioButtonGroup = new ToggleGroup();
		simSearchGroup = new ToggleGroup();

		radioSimSearch = new MyRadioButton("Similarity search", 14, radioButtonGroup);
		radioButtSimSearchAction();
		radioSubset = new MyRadioButton("Substructure filter", 14, radioButtonGroup);
		radioButtSubsetAction();

		title = new MyLabel("Options");

		helmText = new MyLabel("Query HELM notation:");

		scrollCheckBox = new MyRadioButton("Show notations with a minimum similarity of", 13,
				simSearchGroup);
		scrollCheckBoxAction();

		button = new MyButton("Run query");
		button.setDisable(true);
		setButtonProperties();

		tanimotoUnit = new MyText("%");
		tanimotoUnit.setFont(Font.font("Calibri", 13));

		tanimotoTextField = new MyTextField(Double.toString(defaultTanimoto), 35, 11);
		setTanimotoTextFieldAction();

		queryHelmTextField = new MyTextField("Paste HELM notation here.", 590, 12);
		setHelmTextFieldProperties();
		// only for testing:
//		queryHelmTextField.setText("RNA1{[LR](A)}$$$$");

		scrollBar = new ScrollBar();
		setScrollBarProperties();
		setBindingProperties();

		tenSimilarCheckBox = new MyRadioButton("Show 10 most similar notations", 13, simSearchGroup);
		tenSimilarCheckBoxAction();

		analogsCheckBox = new MyCheckBox("Consider natural analogs");
		analogsCheckBox.setSelected(false);
	}

	/**
	 * Method sets the similarity search radiobutton on action.
	 */
	private void radioButtSimSearchAction() {
		radioSimSearch.setSelected(true);
		radioSimSearch.setOnAction(radioSimSearchAction -> {
			scrollCheckBox.setStyle("-fx-opacity: 1");
			scrollBar.setDisable(false);
			tanimotoUnit.setStyle("-fx-opacity: 1");
			tanimotoTextField.setDisable(false);
			tenSimilarCheckBox.setDisable(false);
			analogsCheckBox.setDisable(false);
		});
	}

	/**
	 * Method sets the substructure filter radiobutton on action.
	 */
	private void radioButtSubsetAction() {
		radioSubset.setOnAction(radioSubsetAction -> {
			if (radioSubset.isSelected()) {
				scrollCheckBox.setStyle("-fx-opacity: 0.4");
				scrollBar.setDisable(true);
				tanimotoUnit.setStyle("-fx-opacity: 0.4");
				tanimotoTextField.setDisable(true);
				tenSimilarCheckBox.setDisable(true);
				analogsCheckBox.setDisable(true);
			}
		});
	}

	/**
	 * Method sets the check box to define the desired similarity on action.
	 */
	private void scrollCheckBoxAction() {
		scrollCheckBox.setSelected(true);
		scrollCheckBox.setOnAction(event -> {
			tenSimilarCheckBox.setSelected(false);
			scrollBar.setDisable(false);
			tanimotoTextField.setDisable(false);
			tanimotoUnit.setStyle("-fx-opacity: 1");
		});
	}

	/**
	 * Method sets the query button on action.
	 */
	private void setButtonProperties() {
		button.setOnAction(helmEvent -> {
			if (queryHelmTextField.getCharacters().toString() == null ||
					queryHelmTextField.getCharacters().toString() == "") {
				AlertBox.ErrorBox("Invalid HELM notation", "Please enter your query HELM notation.");
			}
			Tooltip queryHelmTooltip = new Tooltip();
			queryHelmTooltip.setText(queryHelmTextField.getText());
			queryHelmTextField.setTooltip(queryHelmTooltip);
			runQueryButton();
		});
	}

	/**
	 * Method sets the properties for the query helm text field.
	 */
	private void setHelmTextFieldProperties() {
		queryHelmTextField.setMinWidth(600);
		queryHelmTextField.setMaxWidth(600);
		queryHelmTextField.setOnAction(this);
	}

	/**
	 * Method sets the tanimoto textfield on action and sets its properties.
	 */
	private void setTanimotoTextFieldAction() {
		tanimotoTextField.setText(Integer.toString(defaultTanimoto));
		tanimotoTextField.setEditable(false);
		tanimotoTextField.setOnAction(this);
	}

	/**
	 * Method sets the properties of the scrollbar like its default value.
	 */
	private void setScrollBarProperties() {
		scrollBar.setMinWidth(200);
//		scrollBar.setMinHeight(20);
		scrollBar.setMin(0);
		scrollBar.setMax(100);
		scrollBar.setValue(defaultTanimoto);
	}

	/**
	 * Method binds the tanimoto textfield to the scrollbar.
	 */
	private void setBindingProperties() {
		scrollBar.valueProperty().addListener((scrollTanimoto, oldScroll, newScroll) -> {
			int value = newScroll.intValue();
			String valueString = Integer.toString(value);
			tanimotoTextField.setText(valueString);
		});
	}

	/**
	 * Method sets check box for the ten most similar notations
	 * on action.
	 */
	private void tenSimilarCheckBoxAction() {
		tenSimilarCheckBox.setSelected(false);
		tenSimilarCheckBox.setOnAction(event -> {
			scrollCheckBox.setSelected(false);
			scrollBar.setDisable(true);
			tanimotoTextField.setDisable(true);
			tanimotoUnit.setStyle("-fx-opacity: 0.4");
		});
	}

	/**
	 * Method handles the event of the query button by running the
	 * similarity search and processing the results.
	 */
	private void runQueryButton() {
		if (queryHelmTextField.getCharacters().toString() == null ||
				queryHelmTextField.getCharacters().toString() == "") {
			AlertBox.ErrorBox("Invalid HELM notation", "Please enter your query HELM notation.");
		} else {
			try {
				queryNotation = HELM2Object.makeHELM2NotationObject(queryHelmTextField.getCharacters().toString());
				desiredSimilarity = Double.parseDouble(tanimotoTextField.getText());
				desiredSimilarity = desiredSimilarity / 100;
				@SuppressWarnings("unused")
				SimilaritySearch simSearch = new SimilaritySearch(queryNotation, desiredSimilarity);
			} catch (ExceptionState e) {
				AlertBox.ErrorBox("Notation Error", "Invalid HELM notation.");
			}
		}
	}

	public HELM2Notation getQueryNotation() {
		return this.queryNotation;
	}

	public double getSimilarity() {
		return this.desiredSimilarity;
	}
}
