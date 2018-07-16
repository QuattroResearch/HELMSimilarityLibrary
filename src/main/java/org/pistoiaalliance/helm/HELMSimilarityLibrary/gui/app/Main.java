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

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
/**
 * Main class to set the layout of the user interface and its node positions
 * within the stage.
 * 
 * @author bueltel
 *
 */
public class Main extends Application implements EventHandler<ActionEvent> {
	private BorderPane mainLayout;
	private GridPane topLayout;
	private GridPane centerLayout;
	private GridPane bottomLayout;


	/**
	 *  Method performs initialization of layouts prior to actual starting
	 *  of the application. Main layout is a BorderPane, inside are GridPanes.
	 */
	@Override
	public void init() {
		mainLayout = new BorderPane();
		topLayout = new GridPane();
		centerLayout = new GridPane();
		bottomLayout = new GridPane();
	}


	@Override
	public void start(Stage stage) {
		try {
//			mainLayout = FXMLLoader.load(getClass().getResource("fxml.SceneBuilderTest.fxml"));

			// Layout and size of the main window.
			Scene scene = new Scene(mainLayout, 700, 720);

			Options options = new Options();

			// Construct the individual layouts.
			setMainLayout();
			setTopLayout(stage);
			setCenterLayout(options);
			setBottomLayout(options);

			stage.getIcons().add(new Image("file:./images/HELM_logo.png"));
			stage.setTitle("HELMSimilarityLibrary");
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private void setMainLayout() {
		mainLayout.setTop(topLayout);
		mainLayout.setCenter(centerLayout);
		mainLayout.setBottom(bottomLayout);
	}


	/**
	 * Method sets the layout of the interfaces input section. All of the controls
	 * for processing a textfile, building SQLite database and selecting directories
	 * are handled here.
	 *
	 * @param stage the jfx stage
	 */
	private void setTopLayout(Stage stage) {
		// for debugging:
//		topLayout.setGridLinesVisible(true);
		Input input = new Input(stage);
		topLayout.getChildren().addAll(input.inputTitle, input.helpButton,
				input.textfileText,	input.textfileButton,
				input.buildDatabaseButton, input.textfileNameDisplay,
				input.databaseText,
				input.databaseNameDisplay, FileToDatabase.progress,
				FileToDatabase.infoText);
		topLayout.setPadding(new Insets(10, 10, 10, 10));
		topLayout.setVgap(8);
		topLayout.setHgap(10);

		GridPane.setConstraints(input.inputTitle, 0, 0);
		GridPane.setConstraints(input.helpButton, 1, 0);
		GridPane.setConstraints(input.textfileText, 0, 1, 3, 1);
		GridPane.setConstraints(input.textfileButton, 3, 1);
		GridPane.setConstraints(input.textfileNameDisplay, 4, 1);
		GridPane.setConstraints(input.buildDatabaseButton, 3, 2);
		GridPane.setConstraints(input.databaseText, 0, 2, 3, 1);
		GridPane.setConstraints(input.databaseNameDisplay, 4, 2);
		GridPane.setConstraints(FileToDatabase.progress, 0, 3, 5, 1);
		GridPane.setConstraints(FileToDatabase.infoText, 0, 4, 2, 1);
	}


	/**
	 * Method sets the layout of the interfaces options section. The controls
	 * for the natural analog filter and the desired similarity are handled here.
	 *
	 * @param options the options object
	 */
	private void setCenterLayout(Options options) {
//		centerLayout.setGridLinesVisible(true);
		centerLayout.getChildren().addAll(options.title, options.analogsCheckBox,
				options.tenSimilarCheckBox,
				options.radioSimSearch, options.radioSubset, options.scrollCheckBox,
				options.tanimotoUnit, options.scrollBar, options.tanimotoTextField);
		centerLayout.setPadding(new Insets(10, 10, 10, 10));
		centerLayout.setVgap(8);
		centerLayout.setHgap(10);

		GridPane.setConstraints(options.title, 0, 0);
//		GridPane.setConstraints(filters.label, 0, 0, 2, 1);
		GridPane.setConstraints(options.radioSimSearch, 0, 1);
		GridPane.setConstraints(options.scrollCheckBox, 0, 2);
		GridPane.setMargin(options.scrollCheckBox, new Insets(0, 0, 0, 30));
		GridPane.setConstraints(options.scrollBar, 1, 2);
		GridPane.setMargin(options.scrollBar, new Insets(0, 0, 0, 30));
		GridPane.setConstraints(options.tanimotoTextField, 2, 2);
		GridPane.setConstraints(options.tanimotoUnit, 3, 2);
		GridPane.setMargin(options.tanimotoUnit, new Insets(0, 20, 0, 0));
		GridPane.setConstraints(options.tenSimilarCheckBox, 0, 3);
		GridPane.setMargin(options.tenSimilarCheckBox, new Insets(0, 0, 0, 30));
		GridPane.setConstraints(options.analogsCheckBox, 0, 4);
		GridPane.setMargin(options.analogsCheckBox, new Insets(0, 0, 0, 30));
		GridPane.setConstraints(options.radioSubset, 0, 5);
	}


	/**
	 * Method sets the layout of the interfaces result section. The controls
	 * for the query, listing of results and export of results are handled here.
	 *
	 * @param options the options object
	 */
	private void setBottomLayout(Options options) {
//		bottomLayout.setGridLinesVisible(true);
		Results results = new Results();
		bottomLayout.setPadding(new Insets(10, 10, 10, 10));
		bottomLayout.setVgap(8);
		bottomLayout.setHgap(10);
		bottomLayout.getChildren().addAll(options.helmText, options.queryHelmTextField,
				Options.button, results.title, Results.infoText, Results.getResultNotations(),
				SimilaritySearchTask.progressbar, results.exportButton,
				ExportTask.exportInfoText);
		bottomLayout.setPadding(new Insets(10, 10, 10, 10));

		GridPane.setConstraints(options.helmText, 0, 0, 2, 1);
		GridPane.setConstraints(options.queryHelmTextField, 0, 1, 2, 1);
		GridPane.setConstraints(options.button, 1, 1);
		GridPane.setHalignment(options.button, HPos.RIGHT);
		GridPane.setConstraints(results.title, 0, 2);
		GridPane.setMargin(results.title, new Insets(7, 0, 0, 0));
		GridPane.setConstraints(Results.getResultNotations(), 0, 3, 3, 1);
		GridPane.setConstraints(SimilaritySearchTask.progressbar, 0, 4, 2, 1);
		GridPane.setConstraints(Results.infoText, 0, 5, 2, 1);
		GridPane.setConstraints(results.exportButton, 0, 6);
		GridPane.setConstraints(ExportTask.exportInfoText, 1, 6);
	}


	@Override
	public void stop() {

	}


	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
	}
}