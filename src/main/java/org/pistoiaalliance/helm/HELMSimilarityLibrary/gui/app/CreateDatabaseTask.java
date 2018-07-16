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
import java.sql.SQLException;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.AlertBox;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * CreateDatabaseTask extends javafx.concurrent.Task class in order to run
 * the generation of a database from a helm textfile in a new thread. It
 * is not running on the JavaFX Application thread.
 *
 * @author bueltel
 *
 */
public class CreateDatabaseTask extends Task<Integer>{
	private Database dbInstance;

	public CreateDatabaseTask(Database dbInstance) {
		this.dbInstance = dbInstance;
	}

	@Override
	protected Integer call() {
		try {
			String url = "jdbc:sqlite:" + Input.getDatabasePath();
			dbInstance.fillHELMTable(url, Input.getTextfilePath(), this);
		} catch (SQLException e) {
			updateUIerrorReport("SQLException", "Unable to fill database with ID and HELM: " +
					Input.getDatabasePath() + ".\n" + e.getMessage());
		} catch (IOException e) {
			updateUIerrorReport("IOException", "Unable to process textfile: " +
					Input.getTextfilePath() + ".\n" + e.getMessage());
		}
		return null;
	}

	@Override
	protected void updateProgress(double workDone, double max) {
		super.updateProgress(workDone, max);
	}

	void updateUIerrorReport(String errorType, String content) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				AlertBox.ErrorBox(errorType, content);
			}
		});
	}
}
