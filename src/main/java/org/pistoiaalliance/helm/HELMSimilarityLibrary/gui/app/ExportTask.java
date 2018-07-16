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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.FilenameUtils;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyText;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * ExportTask class extends javafx.concurrent.Task and runs the task to export
 * results in a new thread. It is not running on the JavaFX Application thread.
 *
 * @author bueltel
 *
 */
public class ExportTask extends Task<Integer> {
	protected static MyText exportInfoText = new MyText(true);

	public ExportTask() {

	}

	@Override
	protected Integer call() throws Exception {
		updateUIinfoText("Exporting results to textfile...");
		String outputfilename = Input.getDatabaseDirPath() + "/" + FilenameUtils.getBaseName(Input.getTextfileName()) + "_results.txt";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfilename), "utf-8"));
		ObservableList<ResultUnit> items = Results.getResultNotations().getItems();
		items.forEach(item -> {
			try {
				writer.write(item.getId() + "\t" + item.getHelmNotation() + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		writer.close();
		updateUIinfoText("Export done to: " + outputfilename +  ".");

		return null;
	}

	private void updateUIinfoText(String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				exportInfoText.setText(text);
			}
		});
	}
}