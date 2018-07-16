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

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.AlertBox;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyButton;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyLabel;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyText;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Input class handles the input section of the user interface.
 * 
 * @author bueltel
 *
 */
public class Input extends Main {
	protected MyLabel inputTitle;
	protected MyButton helpButton;
	protected MyText textfileText;
	protected MyText databaseText;
	protected MyButton textfileButton;
	protected MyButton buildDatabaseButton;
	protected MyText textfileNameDisplay;
	protected static MyText databaseNameDisplay;
	private String defaultFileName = "No file selected.";
	private FileChooser textfileChooser;
	private static String textfilePath;
	private static String textfileName;
	private static String databaseDirPath;
	private static String databasePath;
	private static String databaseName;

	/**
	 * This constructor instantiates all of the controls of the interfaces input
	 * section and calls several methods to set the controls properties and event
	 * handling.
	 *
	 * @param stage the jfx stage
	 */
	public Input(Stage stage) {
		inputTitle = new MyLabel("Data input");
		helpButton = new MyButton("?");
		helpButton.setOnAction(this);

		textfileText = new MyText("Select a textfile with HELM notations: ");

		databaseText = new MyText("Build database from text file: ");

		textfileButton = new MyButton("Choose file...");
		textfileButton.setMinWidth(88);

		buildDatabaseButton = new MyButton("Build database");

		textfileNameDisplay = new MyText(defaultFileName);
		databaseNameDisplay = new MyText(defaultFileName);

		textfileChooser = new FileChooser();
		setFileChooserProperties(textfileChooser, "TXT", "*.txt", "Input textfile");
		getTextfilePath(stage);
		getDatabaseDirectory(stage);
	}

	/**
	 * Method sets the file choosers title and file extension filter.
	 *
	 * @param chooser the file chooser
	 * @param fileIdentifier the file identifier
	 * @param fileExtension the file extension
	 * @param title the title of the file chooser
	 */
	private void setFileChooserProperties(FileChooser chooser, String fileIdentifier, String fileExtension,
										  String title) {
		// only allow .txt files to be loaded
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileIdentifier, fileExtension));
		chooser.setTitle(title);
	}

	/**
	 * Method handles the textfileButtons action by saving the selected textfile
	 * path and displaying the textfile name in the user interface.
	 *
	 * @param stage the jfx stage
	 */
	private void getTextfilePath(Stage stage) {
		textfileButton.setOnAction(buttonEvent -> {
			File textfile = textfileChooser.showOpenDialog(stage);
			if (textfile != null) {
				textfilePath = textfile.getPath();
				databaseDirPath = textfile.getParentFile().getPath();
				textfileName = textfile.getName();
				textfileNameDisplay.setText(textfileName);
			}
		});
	}


	/**
	 * Method handles the buildDatabaseButtons action by saving the selected path
	 * and displaying the database name in the user interface.
	 *
	 * @param stage the jfx stage
	 */
	private void getDatabaseDirectory(Stage stage) {
		buildDatabaseButton.setOnAction(buttonEvent -> {
			if (textfileName == null) {
				AlertBox.ErrorBox("File Error", "Please select a textfile.");
			} else {
				databaseNameDisplay.setText("");
				String rawfileName = FilenameUtils.getBaseName(textfileName);

				databasePath = databaseDirPath + "/" + rawfileName + ".db";
				databaseName = rawfileName + ".db";

				FileToDatabase.processTextFile(databaseDirPath);
				textfileButton.setDisable(true);
				buildDatabaseButton.setDisable(true);
				Options.button.setDisable(false);
			}
		});
	}


	/**
	 * Method handles the helpButtons action and displays an AlertBox for user
	 * support.
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() == helpButton) {
			AlertBox.HelpBox("The notations will be stored in a SQLite database. "
					+ "Please note that the textfile must contain one ID and one "
					+ "HELM notation per line separated by a tab. If the textfile "
					+ "contains HELM notations without ID, the IDs will be "
					+ "generated automatically.\n\n");
		}
	}

	public static String getTextfilePath() {
		return textfilePath;
	}

	public static String getTextfileName() {
		return textfileName;
	}

	public static String getDatabaseDirPath() {
		return databaseDirPath;
	}

	public static String getDatabasePath() {
		return databasePath;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static void setTextfilePath(String path) {
		textfilePath = path;
	}

	public static void setTextfileName(String name) {
		textfileName = name;
	}

	public static void setDatabaseDirPath(String path) {
		databaseDirPath = path;
	}

	public static void setDatabasePath(String path) {
		databasePath = path;
	}

	public static void setDatabaseName(String name) {
		databaseName = name;
	}
}
