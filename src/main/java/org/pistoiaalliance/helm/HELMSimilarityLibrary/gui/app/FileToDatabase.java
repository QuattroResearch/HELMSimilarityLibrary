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
import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.AlertBox;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyProgressBar;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyText;

import javafx.application.Platform;

/**
 * FromFileToDatabase class takes a text file with helm notations and stores the
 * notations in a SQLite database.
 * 
 * @author bueltel
 *
 */
public class FileToDatabase {
	protected static MyText infoText = new MyText(true);
	protected static MyProgressBar progress = new MyProgressBar();

	/**
	 *
	 *
	 * @param databaseDirPath the directory of the database
	 */
	protected static void processTextFile(String databaseDirPath) {
		Database dbInstance = Database.getInstance();
		createDatabaseWithTable(databaseDirPath, dbInstance);
		storeNotationsInDatabase(dbInstance);

		// After creating the database, enable button "run query"
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Options.button.setDisable(false);
			}
		});
	}

	/**
	 * Method creates a new database with a table and two columns at the given
	 * directory.
	 * @param databaseDirPath the directory of the database
	 * @param dbInstance the database instance
	 */
	private static void createDatabaseWithTable(String databaseDirPath, Database dbInstance) {
		try {
			infoText.setText("Creating database...");
			String rawfileName = FilenameUtils.getBaseName(Input.getTextfileName());
			String url = "jdbc:sqlite:" + databaseDirPath + "/" + rawfileName + ".db";

			File f = new File(Input.getDatabasePath());
			if (f.exists() && !f.isDirectory()) {
				dbInstance.deleteContentFromDB(url);
				f.delete();
			}

			dbInstance.createTable(url, "HELMnotations", "ID", "HELM");

		} catch (SQLException e) {
			updateUIerrorReport("SQLException", "Couldn't create or connect to database: " +
					Input.getDatabasePath());
		}
	}

	/**
	 * Method inserts the helm notations in the database.
	 * @param dbInstance the database instance
	 */
	private static void storeNotationsInDatabase(Database dbInstance) {
		CreateDatabaseTask createDBtask = new CreateDatabaseTask(dbInstance);
		progress.progressProperty().bind(createDBtask.progressProperty());
		infoText.setText("Filling database with HELM notations...");
		new Thread(createDBtask).start();
		infoText.setText("Database created successfully.");
	}

	static void updateUIerrorReport(String errorType, String content) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				AlertBox.ErrorBox(errorType, content);
			}
		});
	}
}