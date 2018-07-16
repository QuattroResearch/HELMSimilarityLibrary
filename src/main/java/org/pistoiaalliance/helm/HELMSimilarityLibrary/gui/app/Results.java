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

import java.util.List;
import java.util.Map;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyButton;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyTableColumn;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyText;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

/**
 * Results class handles the result section of the user interface.
 * 
 * @author bueltel
 *
 */
public class Results extends Main {
	protected MyText title;
	private static TableView<ResultUnit> resultNotations;
	private static MyTableColumn<ResultUnit, Integer> ids;
	private static MyTableColumn<ResultUnit, String> notations;
	private static MyTableColumn<ResultUnit, String> tanimotoValues;
	protected static MyButton exportButton;
	protected static MyText infoText = new MyText(true);


	/**
	 * Results constructor instantiates all of the controls of the interfaces
	 * results section and calls several methods to set the controls properties and
	 * event handling.
	 */
	public Results() {
		infoText.setFont(Font.font("Calibri", FontPosture.ITALIC, 12));
		title = new MyText("Results");
		resultNotations = new TableView<>();
		resultNotations.setMaxHeight(200);
		resultNotations.setMinWidth(675);
		resultNotations.setMaxWidth(675);
		resultNotations.getSelectionModel().setCellSelectionEnabled(true);
		resultNotations.setPlaceholder(new Label(""));

		ids = new MyTableColumn<>("ID", 50, "id");
		ids.setMaxWidth(50);

		notations = new MyTableColumn<>("HELM", 552, "helmNotation");

		tanimotoValues = new MyTableColumn<>("Similarity", 70, "tanimotoValue");

		exportButton = new MyButton("Export results to textfile");
		exportButton.setDisable(true);
		exportButtonAction();
	}


	/**
	 * Method processes the results to be stores in the TableView.
	 *
	 * @param resultMapList map with the results
	 */
	public static void doResults(List<Map<String, Object>> resultMapList) {
		resultNotations.setItems(getResultsAsObservableList(resultMapList));

		Platform.runLater(new Runnable() {
			@Override public void run() {
				Results.getResultNotations().getColumns().clear();
				resultNotations.getColumns().addAll(ids, notations, tanimotoValues);
			}
		});

		resultNotations.setEditable(false);

		enableCopyAction();
		enableHelmTooltip();
	}


	/**
	 * Method makes an ObservableList from the list of result notations.
	 *
	 * @param resultMapList map with the results
	 * @return ObservableList<ResultUnit>
	 */
	private static ObservableList<ResultUnit> getResultsAsObservableList(List<Map<String, Object>> resultMapList) {
		ObservableList<ResultUnit> resultDataset = FXCollections.observableArrayList();

		resultMapList.forEach(listEntry -> {
			int id = (int) listEntry.get("ID");
			String helm = (String) listEntry.get("HELM");
			double tanimoto = (double) listEntry.get("Similarity");
			resultDataset.add(new ResultUnit(id, helm, tanimoto));
		});

		return resultDataset;
	}


	/**
	 * Method implements a context menu that enables to copy one of the selected
	 * cells into the clipboard.
	 */
	private static void enableCopyAction() {
		ContextMenu copyContext = new ContextMenu();
		MenuItem copyMenuItem = new MenuItem("Copy");
		copyContext.getItems().add(copyMenuItem);

		copyMenuItem.setOnAction(copyEvent -> {
			ObservableList<TablePosition> selectedCells = resultNotations.getSelectionModel().getSelectedCells();
			ResultUnit selectedItem = resultNotations.getSelectionModel().getSelectedItem();
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent copiedContent = new ClipboardContent();

			if (selectedCells.get(0).getColumn() == 0) {
				copiedContent.putString(selectedItem.getId().toString());
			} else if (selectedCells.get(0).getColumn() == 1) {
				copiedContent.putString(selectedItem.getHelmNotation());
			} else if (selectedCells.get(0).getColumn() == 2) {
				copiedContent.putString(selectedItem.getTanimotoValue().toString());
			}
			clipboard.setContent(copiedContent);
		});
		resultNotations.setContextMenu(copyContext);
	}


	/**
	 * Method implements a tooltip for each table row to display the whole
	 * HELM notation.
	 */
	private static void enableHelmTooltip() {
		resultNotations.setRowFactory(table -> new TableRow<ResultUnit>(){
			private Tooltip helmTooltip = new Tooltip();
			@Override
			public void updateItem(ResultUnit unit, boolean empty) {
				super.updateItem(unit, empty);
				if(unit == null) {
					setTooltip(null);
				} else {
					helmTooltip.setText(unit.getHelmNotation());
					setTooltip(helmTooltip);
				}
			}
		});
	}

	/**
	 * Method exports the results to a textfile. Task is done in a new Thread. 
	 */
	private void exportButtonAction() {
		exportButton.setOnAction(exportEvent -> {
			ExportTask expTask = new ExportTask();
			new Thread(expTask).start();
		});
	}

	public static void changeInfoText(String text) {
		infoText.setText(text);
	}

	public static TableView<ResultUnit> getResultNotations() {
		return resultNotations;
	}
}
