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
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.helm.notation2.parser.exceptionparser.ExceptionState;
import org.helm.notation2.parser.exceptionparser.NotationException;
import org.helm.notation2.parser.notation.HELM2Notation;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.Fingerprinter;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.HELM2Object;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.PathGenerator;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.Similarity;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.exception.NaturalAnalogException;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.AlertBox;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.MyProgressBar;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.Graph;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.utils.MoleculeGraphUtils;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * SimilaritySearch class to perform the algorithm of calculating the similarity
 * of a query helm notation and a database of more helm notations in the
 * following steps
 * <p>
 * 1. Calculation of the fingerprint of the query notation
 * <p>
 * 2. Calculation of the fingerprints of the notations in database
 * <p>
 * 3. Calculation of tanimoto of the query notation and every other notation
 * <p>
 * 4. Checking for subset in database
 * <p>
 * 5a. Returning the notations with a specified minimum similarity in descending
 * order.
 * <p>
 * 5b. Or returning the notations that the query notation is a subset of.
 * 
 * @author bueltel
 *
 */

public class SimilaritySearchTask extends Task<Integer> {
	String url;
	BitSet queryFingerprint = new BitSet();
	BitSet queryNaturalFingerprint = new BitSet();
	private static Map<Integer, String> idWithHELM;
	protected static List<Integer> idList;
	protected static List<HELM2Notation> helmList;
	private static List<Graph> molecules;
	private static PathGenerator dfs = new PathGenerator();
	private static Set<String> allPaths = new HashSet<String>();
	private static Set<String> allPathsNaturalAnalogs = new HashSet<String>();
	private static List<BitSet> fingerprints = new ArrayList<BitSet>();
	private static List<BitSet> fingerprintsNaturalAnalogs = new ArrayList<BitSet>();
	protected static List<Double> tanimotoList;
	protected static List<Map<String, Object>> resultNotations;
	protected static MyProgressBar progressbar = new MyProgressBar();
	private HELM2Notation queryNotation;
	private double desiredTanimoto;
	public static final double MAX_PROGRESS = 100;
	private double workDone = 0;
	private double steps;

	public SimilaritySearchTask(HELM2Notation queryNotation, double desiredTanimoto) {
		this.queryNotation = queryNotation;
		this.desiredTanimoto = desiredTanimoto;
	}

	@Override
	protected Integer call() {
		doQuery(queryNotation, desiredTanimoto);
		return null;
	}

	/**
	 * Method calls the corresponding methods to build moleculeGraphs, find paths,
	 * generate fingerprints, calculate tanimoto values, check for subset, store the
	 * values in a database and select the desired notations with a specified
	 * minimum similarity or subset notations.
	 *
	 * @param queryNotation the query notation
	 * @param desiredTanimoto the desired minimum tanimoto
	 */
	public void doQuery(HELM2Notation queryNotation, double desiredTanimoto) {

		if (Input.getDatabasePath() == null) {
			updateUIerrorReport("File Error", "File not found.");
		} else if (!new File(Input.getDatabasePath()).exists()) {
			updateUIerrorReport("File Error", Input.getDatabasePath() + " not found or invalid.");
		} else {
			processQueryNotation(queryNotation);
			url = "jdbc:sqlite:" + Input.getDatabasePath();

			try {
				Database dbInstance = Database.getInstance();
				dbInstance.loadDriver("org.sqlite.JDBC");

				// If fingerprints have not been calculated before, start with the
				// initial generation of HELM2 notation objects, molecule graphs and
				// fingerprints (with and without natural analogs).
				if (!columnExists(url, "HELMnotations", "OriginalFingerprint")) {
					notationsToHELM2Object(dbInstance);
					buildMoleculeGraphs();
					generateFingerprints();
					dbInstance.addColumn(url, "HELMnotations", "OriginalFingerprint");
					dbInstance.addColumn(url, "HELMnotations", "NaturalFingerprint");
					insertFingerprintsToDatabase(dbInstance, url);
				} // Else use the fingerprints from before and only calculate
				// tanimoto values again.
				else {
					tanimotoList.clear();
					steps = MAX_PROGRESS / (idList.size() * 3);
					updateProgress(workDone, MAX_PROGRESS);
				}
				// For substructure filter, only use the fingerprints of original paths.
				if (Options.radioSimSearch.isSelected()) {
					// If naturalAnalogFlag is enabled, the natural analogs of non-natural monomers
					// will be taken into account.
					if (Options.analogsCheckBox.isSelected()) {
						calculateTanimotoToQuery(fingerprintsNaturalAnalogs, queryNaturalFingerprint);
					} else {
						calculateTanimotoToQuery(fingerprints, queryFingerprint);
					}
				} else {
					calculateTanimotoToQuery(fingerprints, queryFingerprint);
				}

				// If the similarity has not been calculated before, add a new column
				// to database.
				if (!columnExists(url, "HELMnotations", "Similarity")) {
					dbInstance.addColumn(url, "HELMnotations", "Similarity");
				}
				insertTanimotoToDatabase(dbInstance, url);

				// If substructure filter is selected, execute subset algorithm and get
				// the notations from the database that the query is a subset of.
				if (Options.radioSubset.isSelected()) {
					doSubstructureFilter(dbInstance, url, queryFingerprint);
					getSubsetNotationsFromDatabase(dbInstance, url, "HELMnotations", "hasSubset");
				} else {
					getSimilarNotationsFromDatabase(dbInstance, url, "HELMnotations",
							"Similarity", desiredTanimoto);
				}

				// Fill the table view of UI with the resultNotations.
				Results.doResults(resultNotations);

				// After results have been calculated, enable the possibility
				// to export the results as a textfile:
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Results.exportButton.setDisable(false);
					}
				});

			} catch (ClassNotFoundException e1) {
				updateUIerrorReport("Error", "");
			} catch (SQLException e) {
				updateUIerrorReport("SQL Error", "Couldn't add new column to database.");
			}
		}
	}

	/**
	 * Method builds a moleculeGraph of the queryNotation, finds every path in it
	 * and generates its fingerprint.
	 *
	 * @param queryNotation the query notation
	 */
	private void processQueryNotation(HELM2Notation queryNotation) {
		List<Graph> queryMolecule = new ArrayList<>();
		PathGenerator dfs = new PathGenerator();
		Set<String> paths;
		Set<String> naturalPaths;

		try {
			queryMolecule.add(
					MoleculeGraphUtils.buildMoleculeGraph(queryNotation.getListOfPolymers(),
							queryNotation.getListOfConnections()));
			paths = dfs.getPaths();
			naturalPaths = dfs.getNaturalPaths();
			dfs.findPaths(queryMolecule.get(0));
			queryFingerprint = Fingerprinter.getHashedFingerprint(paths);
			queryNaturalFingerprint = Fingerprinter.getHashedFingerprint(naturalPaths);
			queryNaturalFingerprint.or(queryFingerprint);

		} catch (NotationException e) {
			updateUIerrorReport("NotationException", "");
		}  catch (NaturalAnalogException e) {
			updateUIerrorReport("NaturalAnalogException", "Couldn't find natural analog to monomer.");
		} catch (NoSuchAlgorithmException e) {
			updateUIerrorReport("NoSuchAlgorithmException", "");
		}
	}

	/**
	 * Method reads HELM notations with ID from database and generates HELM2Objects.
	 *
	 * @param dbInstance the database instance
	 */
	private void notationsToHELM2Object(Database dbInstance) {
		try {
			idWithHELM = new HashMap<>();
			idList = new ArrayList<>();
			helmList = new ArrayList<>();

			updateUItextDisplay("Reading database...");
			idWithHELM = dbInstance.readIDandHELMRecords(url, "HELMnotations");
			steps = MAX_PROGRESS / (idWithHELM.size() * 7);
			updateUItextDisplay("Creating HELM2 objects...");

			idWithHELM.forEach((id, helm) -> {
				idList.add(id);
				try {
					helmList.add(HELM2Object.makeHELM2NotationObject(helm));
				} catch (ExceptionState e) {

					System.out.println(e.getMessage() + " at " + id + ": " + helm);
					updateUIerrorReport("hier ExceptionState", e.getMessage() + " at " + id + ": "
							+ helm);
					updateUItextDisplay("");
				}
				workDone += steps;
				updateProgress(workDone, MAX_PROGRESS);
			});
		} catch (SQLException e) {
			updateUIerrorReport("SQL Exception", "Couldn't read ID and HELM from database.");
		}
	}

	/**
	 * Method checks if a column in a database already exists.
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the column
	 * @return boolean
	 */
	private boolean columnExists(String url, String tablename, String columnname) {
		try {
			Connection con = DriverManager.getConnection(url);
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getColumns(null, null, tablename, columnname);
			if (rs.next()) {
				return true;
			}
			rs.close();
			con.close();
		} catch (SQLException e) {
			updateUIerrorReport("SQLException", "Failed to check if column " + columnname +
					" exists.");
		}
		return false;
	}

	/**
	 * Method builds moleculeGraphs from a list of helm notations.
	 */
	private void buildMoleculeGraphs() {
		updateUItextDisplay("Building molecule graphs...");
		molecules = new ArrayList<>();

		helmList.forEach(helm -> {
			try {
				molecules.add(MoleculeGraphUtils.buildMoleculeGraph(helm.getListOfPolymers(),
						helm.getListOfConnections()));
				workDone += steps;
				updateProgress(workDone, MAX_PROGRESS);
			} catch (NotationException e) {
				updateUIerrorReport("NotationException", e.getMessage());
				updateUItextDisplay("");
			}
		});
		updateUItextDisplay("Molecule graphs built successfully.");
	}

	/**
	 * Method finds every path in the moleculeGraphs and generates fingerprints for
	 * every moleculeGraph.
	 *
	 */
	private void generateFingerprints() {
		updateUItextDisplay("Generating fingerprints...");
		dfs = new PathGenerator();
		allPaths = new HashSet<>();
		allPathsNaturalAnalogs = new HashSet<>();
		fingerprints = new ArrayList<>();
		fingerprintsNaturalAnalogs = new ArrayList<>();

		for (int j = 0; j < molecules.size(); j++) {
			try {
				dfs.findPaths(molecules.get(j));
				allPaths = dfs.getPaths();
				allPathsNaturalAnalogs = dfs.getNaturalPaths();
				fingerprints.add(Fingerprinter.getHashedFingerprint(allPaths));
				fingerprintsNaturalAnalogs.add(Fingerprinter.getHashedFingerprint(allPathsNaturalAnalogs));
				dfs.clearPaths();
				allPaths.clear();
				allPathsNaturalAnalogs.clear();

				fingerprintsNaturalAnalogs.get(j).or(fingerprints.get(j));

				workDone += steps;
				updateProgress(workDone, MAX_PROGRESS);
			} catch (NaturalAnalogException e) {
				updateUIerrorReport("NaturalAnalogException",
						"Couldn't find natural analog to monomer.");
			} catch (NoSuchAlgorithmException e) {
				updateUIerrorReport("NoSuchAlgorithmException", "");
			}
		}
		updateUItextDisplay("Fingerprints generated successfully.");
	}

	/**
	 * Method adds a new column to a SQLite database and inserts fingerprints to it.
	 *
	 * @param dbInstance the database instance
	 * @param url the url of the database
	 */
	private void insertFingerprintsToDatabase(Database dbInstance, String url) {
		updateUItextDisplay("Storing fingerprints in database...");
		try {
			dbInstance.insertFingerprints(url, fingerprints, fingerprintsNaturalAnalogs, idList,
					"HELMnotations");
			workDone += steps * fingerprints.size();
			updateProgress(workDone, MAX_PROGRESS);
		} catch (SQLException e) {
			updateUIerrorReport("SQLException", "Couldn't insert fingerprints to database.");
		}
	}

	/**
	 * Method calls the function for substructure filter for each notation and saves
	 * the information with true or false in database column.
	 *
	 * @param dbInstance the database instance
	 * @param url the url of the database
	 * @param queryFingerprint the query fingerprint
	 */
	private void doSubstructureFilter(Database dbInstance, String url, BitSet queryFingerprint) {
		updateUItextDisplay("Checking for subset...");
		try {
			if (!columnExists(url, "HELMnotations", "hasSubset")) {
				dbInstance.addColumn(url, "HELMnotations", "hasSubset");
			}
			dbInstance.setSubset(url, queryFingerprint, idList, fingerprints);
			workDone += steps;
			updateProgress(workDone, MAX_PROGRESS);
		} catch (SQLException e) {
			updateUIerrorReport("SQLError", "Column 'hasSubset' could not be created");
		}
	}

	/**
	 * Method calculates the tanimoto value between the query notation and every
	 * other notation.
	 *
	 * @param queryFingerprint the query fingerprint
	 */
	private void calculateTanimotoToQuery(List<BitSet> fingerprints, BitSet queryFingerprint) {
		updateUItextDisplay("Calculating similarity...");
		tanimotoList = new ArrayList<>();
		fingerprints.forEach(fprint -> {
			tanimotoList.add(Similarity.calculateSimilarity(queryFingerprint, fprint));
			workDone += steps;
			updateProgress(workDone, MAX_PROGRESS);
		});
	}

	/**
	 * Method inserts tanimoto values to a SQLite database.
	 *
	 * @param dbInstance the database instance
	 * @param url the url of the database
	 */
	private void insertTanimotoToDatabase(Database dbInstance, String url) {
		updateUItextDisplay("Storing similarity values in database...");
		try {
			dbInstance.fillHELMTanimotoTable(url, "HELMnotations", idList, tanimotoList);
			workDone += (steps * tanimotoList.size()) - 1;
			updateProgress(workDone, MAX_PROGRESS);
		} catch (SQLException e) {
			updateUIerrorReport("SQLException", "Couldn't fill database with similarity values.");
		}
	}

	/**
	 * Method selects the notations from database that the query is a subset of.
	 *
	 * @param dbInstance the database instance
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the column
	 */
	private void getSubsetNotationsFromDatabase(Database dbInstance, String url, String tablename, String columnname) {
		updateUItextDisplay("Selecting notations with subset from database...");
		resultNotations = new ArrayList<>();
		try {
			resultNotations = dbInstance.getNotationsWithSubset(url, tablename, columnname);
			updateProgress(MAX_PROGRESS, MAX_PROGRESS);
			if (resultNotations.size() == 0) {
				updateUItextDisplay("Query notation is not a substructure of your set of notations.");
			} else if (resultNotations.size() == 1) {
				updateUItextDisplay("Query notation is a substructure of 1 notation.");
			} else {
				updateUItextDisplay("Query notation is a substructure of " + resultNotations.size() + " notations.");
			}
		} catch (SQLException e) {
			updateUIerrorReport("SQLError", "Couldn't select notations with subset.");
		}
	}

	/**
	 * Method selects the helm notations that are at least of a minimum similarity
	 * to the query notation.
	 *
	 * @param dbInstance the database instance
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the column
	 * @param desiredTanimoto the desired minimum tanimoto
	 */
	private void getSimilarNotationsFromDatabase(Database dbInstance, String url, String tablename, String columnname,
												 double desiredTanimoto) {
		resultNotations = new ArrayList<>();
		try {
			updateUItextDisplay("Selecting similar notations...");
			if(Options.tenSimilarCheckBox.isSelected()) {
				resultNotations = dbInstance.getMostSimilarNotations(url, tablename, columnname);
			} else {
				resultNotations = dbInstance.getNotationsWithSpecificTanimoto(url, tablename, columnname,
						desiredTanimoto);
			}
			updateProgress(MAX_PROGRESS, MAX_PROGRESS);

			if (resultNotations.size() == 0) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							double nextBiggestTanimoto;
							nextBiggestTanimoto = dbInstance.getBiggestTanimoto(url);
							String tanimoto = new DecimalFormat("#0.0000").format(nextBiggestTanimoto * 100)
									+ " %";
							updateUItextDisplay("No similar notations found.");
							AlertBox.InfoBox(
									"No similar notations found. The next similar notation has a similarity of "
											+ tanimoto + " to the query.");
						} catch (SQLException e) {
							updateUIerrorReport("SQL Error", "Error while accessing similarity column.");
						}
					}
				});
			} else if (resultNotations.size() == 1) {
				updateUItextDisplay("1 similar notation found.");
			} else {
				updateUItextDisplay(resultNotations.size() + " similar notations found.");
			}
		} catch (SQLException e) {
			updateUIerrorReport("SQLException", "Couldn't get similar notations from database.");
		}
	}

	@Override
	protected void updateProgress(double workDone, double max) {
		// System.out.println("WorkDone "+workDone+" max: "+max);
		super.updateProgress(workDone, max);
	}

	void updateUItextDisplay(String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Results.changeInfoText(text);
			}
		});
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

