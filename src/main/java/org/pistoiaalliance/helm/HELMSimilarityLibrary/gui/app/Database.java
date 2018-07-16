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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.pistoiaalliance.helm.HELMSimilarityLibrary.Subset;
import org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout.AlertBox;

import javafx.application.Platform;

/**
 * This database singleton class implements methods to work with a SQLite
 * database while doing similarity search.
 * 
 * @author bueltel
 *
 */
public class Database {

	private static Database DatabaseObject = null;

	/**
	 * Private constructor prevents any other class from instantiating.
	 */
	private Database() {

	}

	/**
	 * This is the static instance method.
	 *
	 * @return Database
	 */
	public static Database getInstance() {
		if (DatabaseObject == null) {
			DatabaseObject = new Database();
		}
		return DatabaseObject;
	}

	/**
	 * Method loads the database's driver dynamically once for entire application's
	 * lifetime.
	 *
	 * @param drivername the drivername
	 * @throws ClassNotFoundException if anything goes wrong
	 */
	public void loadDriver(String drivername) throws ClassNotFoundException {
		Class.forName(drivername);
	}

	/**
	 * Method creates a table with two columns (int, text) for ID and HELM in a
	 * database.
	 *
	 * @param url the url of the database
	 * @param tableName the name of the table
	 * @param column1 the name of the first column
	 * @param column2 the name of the second column
	 * @throws SQLException if the sql statement goes wrong
	 */
	public void createTable(String url, String tableName, String column1, String column2) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		Statement stmt = con.createStatement();
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " " + "(" + column1 + " INT PRIMARY KEY," + column2
				+ "             TEXT)";
		stmt.executeUpdate(sql);
		stmt.close();
		con.close();
	}

	/**
	 * Adds a new column to a given table of a database.
	 *
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the first column
	 * @throws SQLException if the sql statement goes wrong
	 */
	public void addColumn(String url, String tablename, String columnname) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		Statement stmt = con.createStatement();
		stmt.executeUpdate("ALTER TABLE " + tablename + " ADD COLUMN " + columnname);
		stmt.close();
		con.close();
	}

	/**
	 * Method deletes content from a database in order to be able to overwrite it
	 * with new content.
	 *
	 * @param url the url of the database
	 * @throws SQLException if the sql statement goes wrong
	 */
	public void deleteContentFromDB(String url) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM HELMnotations WHERE 1=1;");
		stmt.close();
		con.close();
	}

	/**
	 * Fills the table with CID and HELM from an inputfile.
	 *
	 * @param url the url of the database
	 * @param infilename the input filename
	 * @param createDatabase createDatabaseTask
	 * @throws SQLException if the sql statement goes wrong
	 * @throws IOException if anything goes wrong
	 */
	public void fillHELMTable(String url, String infilename, CreateDatabaseTask createDatabase)
			throws SQLException, IOException {
		Connection con = DriverManager.getConnection(url);
		con.setAutoCommit(false);
		BufferedReader fileReader = new BufferedReader(new FileReader(infilename));
		Path path = Paths.get(infilename);
		Statement stmt;
		boolean hasID = true;
		// Pattern regExp = Pattern.compile("(\\d+\\t)?(.+)");
		// Pattern regExp = Pattern.compile("(.+)\\t(.+)");
		Pattern regExp = Pattern.compile("(\\d+)\\t(\\S+)|(\\S+)");
		String line = fileReader.readLine();
		Matcher m = regExp.matcher(line);
		int linecounter = 0;
		String idMatch;
		int id = 0;
		String helm = null;

		m.find();
		// Check if text file contains corresponding IDs to HELM notations
		if (m.group(1) == null) {
			hasID = false;
		}

		long lineCount = 0;
		try (Stream<String> stream = Files.lines(path)) {
			lineCount = stream.count();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		while (line != null) {
			linecounter++;
			m = regExp.matcher(line);
			m.find();
			createDatabase.updateProgress(linecounter, lineCount);

			// If text file contains corresponding IDs to HELMs, save ID and HELM
			if (hasID) {
				idMatch = m.group(1);
				try {
					id = Integer.parseInt(idMatch);
					helm = m.group(2);
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							AlertBox.ErrorBox("Format Error", e.getMessage() +
									". Please make sure you are using the correct file format. "
									+ "Only load textfiles with one HELM notation per line or one ID and one HELM " +
									"notation separated by "
									+ "one tab per line.");
						}
					});
					// TODO exit the task in running thread! But don't exit the JavaFX Application thread.
				}
			} else { // If text file does not contain corresponding IDs to HELMs, generate them
				// automatically.
				id = linecounter;
				helm = m.group(3);
			}

			stmt = con.createStatement();
			String sql = "INSERT INTO HELMnotations (ID, HELM) " + "VALUES (" + id + ", '" + helm + "');";
			stmt.executeUpdate(sql);

			stmt.close();

			line = fileReader.readLine();
		}
		con.commit();
		Input.databaseNameDisplay.setText(Input.getDatabaseName() + " (" + Integer.toString(linecounter) + " entries)");
		con.close();
		fileReader.close();
	}

	/**
	 * Fill table with HELM fingerprint at the corresponding ID.
	 *
	 * @param url the url of the database
	 * @param fingerprints fingerprint of a HELM notation
	 * @param naturalFingerprints natural fingerprint of HELM notation
	 * @param idList the list of iDs of HELM
	 * @param tablename the name of the table
	 * @throws SQLException if anything goes wrong
	 */
	public void insertFingerprints(String url, List<BitSet> fingerprints, List<BitSet> naturalFingerprints,
								   List<Integer> idList, String tablename) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		con.setAutoCommit(false);
		PreparedStatement pStmt = con.prepareStatement(
				"UPDATE " + tablename + " set OriginalFingerprint = ?, NaturalFingerprint = ? where ID = ?");
		for (int i = 0; i < fingerprints.size(); i++) {
			pStmt.setString(1, fingerprints.get(i).toString());
			pStmt.setString(2, naturalFingerprints.get(i).toString());
			pStmt.setInt(3, idList.get(i));
			pStmt.executeUpdate();
		}
		con.commit();
		pStmt.close();
		con.close();
	}

	/**
	 * Method fills table with tanimoto value at the corresponding ID.
	 *
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param idList the list of iDs of HELM
	 * @param tanimotoList the list of tanimoto values
	 * @throws SQLException if the sql statement goes wrong
	 */
	public void fillHELMTanimotoTable(String url, String tablename, List<Integer> idList, List<Double> tanimotoList)
			throws SQLException {
		Connection con = DriverManager.getConnection(url);
		con.setAutoCommit(false);
		PreparedStatement pStmt = con.prepareStatement("UPDATE " + tablename + " SET Similarity = ? WHERE ID = ?;");
		for (int i = 0; i < tanimotoList.size(); i++) {
			pStmt.setDouble(1, tanimotoList.get(i));
			pStmt.setInt(2, idList.get(i));
			pStmt.executeUpdate();
		}
		con.commit();
		pStmt.close();
		con.close();
	}

	/**
	 * Method reads ID and HELM from table.
	 *
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @return Map with ID and HELM
	 * @throws SQLException if the sql statement goes wrong
	 */
	public Map<Integer, String> readIDandHELMRecords(String url, String tablename) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		Map<Integer, String> idWithHELM = new HashMap<>();
		int id;
		String helm;
		List<Integer> idList = new ArrayList<Integer>();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT ID, HELM FROM " + tablename + ";");
		while (rs.next()) {
			id = rs.getInt("ID");
			idList.add(id);
			helm = rs.getString("HELM");
			idWithHELM.put(id, helm);
		}
		rs.close();
		stmt.close();
		con.close();
		return idWithHELM;
	}

	/**
	 * Method selects ten notations with the highest similarity to the query.
	 *
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the column
	 * @return List similar notations
	 * @throws SQLException if the sql statement goes wrong
	 */
	public List<Map<String, Object>> getMostSimilarNotations(String url, String tablename, String columnname)
			throws SQLException {
		Connection con = DriverManager.getConnection(url);
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> row;

		Statement stmt = con.createStatement();
		ResultSet resultNotations = stmt
				.executeQuery("SELECT * FROM " + tablename + " ORDER BY " + columnname + " DESC;");

		ResultSetMetaData metaData = resultNotations.getMetaData();
		Integer columnCount = metaData.getColumnCount();
		int notationCounter = 0;

		while (resultNotations.next() && notationCounter < 10) {
			row = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				row.put(metaData.getColumnName(i), resultNotations.getObject(i));
			}
			resultList.add(row);
			notationCounter++;
		}
		stmt.close();
		con.close();
		return resultList;
	}

	/**
	 * Selects the helm notations with a tanimoto bigger than or equal to the
	 * desired similarity.
	 *
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the column
	 * @param desiredTanimoto the desired minimum tanimoto value
	 * @return List HELM notation with minimum tanimoto value
	 * @throws SQLException if the sql statement goes wrong
	 */
	public List<Map<String, Object>> getNotationsWithSpecificTanimoto(String url, String tablename, String columnname,
																	  double desiredTanimoto) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> row;

		Statement stmt = con.createStatement();
		ResultSet resultNotations = stmt.executeQuery("SELECT * FROM " + tablename + " WHERE " + columnname + " >= "
				+ desiredTanimoto + " ORDER BY " + columnname + " DESC;");

		ResultSetMetaData metaData = resultNotations.getMetaData();
		Integer columnCount = metaData.getColumnCount();

		while (resultNotations.next()) {
			row = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				row.put(metaData.getColumnName(i), resultNotations.getObject(i));
			}
			resultList.add(row);
		}
		stmt.close();
		con.close();
		return resultList;
	}

	/**
	 * Selects the helm notations that the query notation is a subset of.
	 *
	 * @param url the url of the database
	 * @param tablename the name of the table
	 * @param columnname the name of the column
	 * @return List subset notations
	 * @throws SQLException if the sql statement goes wrong
	 */
	public List<Map<String, Object>> getNotationsWithSubset(String url, String tablename, String columnname)
			throws SQLException {
		Connection con = DriverManager.getConnection(url);
		List<Map<String, Object>> resultList = new ArrayList<>();
		Map<String, Object> row;
		Statement stmt = con.createStatement();
		ResultSet resultNotations = stmt.executeQuery(
				"SELECT * FROM " + tablename + " WHERE " + columnname + " = 'true'" + " ORDER BY Similarity DESC;");

		ResultSetMetaData metaData = resultNotations.getMetaData();
		Integer columnCount = metaData.getColumnCount();

		while (resultNotations.next()) {
			row = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				row.put(metaData.getColumnName(i), resultNotations.getObject(i));
			}
			resultList.add(row);
		}
		stmt.close();
		con.close();
		return resultList;
	}

	/**
	 * Method checks for a list of fingerprints if the query notation is a subset of
	 * it and stores true or false in the database for the corresponding
	 * fingerprint.
	 *
	 * @param url the url of the database
	 * @param queryFingerprint the query fingerprint
	 * @param idList the list of ids
	 * @param fingerprints the list of fingerprints
	 * @throws SQLException if the sql statement goes wrong
	 */
	public void setSubset(String url, BitSet queryFingerprint, List<Integer> idList, List<BitSet> fingerprints)
			throws SQLException {
		Connection con = DriverManager.getConnection(url);
		Statement stmt = con.createStatement();
		con.setAutoCommit(false);

		for (int idx = 0; idx < idList.size(); idx++) {
			boolean hasSubset = Subset.checkHelmRelationship(queryFingerprint, fingerprints.get(idx));
			stmt.executeUpdate(
					"UPDATE HELMnotations SET hasSubset = '" + hasSubset + "' WHERE ID = " + idList.get(idx));
		}
		con.commit();
		stmt.close();
		con.close();
	}

	/**
	 * Returns the maximum similarity value that is found of a notation to the
	 * query.
	 *
	 * @param url the url of the database
	 * @return double the biggest found tanimoto value
	 * @throws SQLException if the sql statement goes wrong
	 */
	public double getBiggestTanimoto(String url) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		double biggestTanimoto;
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery("SELECT MAX(Similarity) FROM HELMnotations");
		biggestTanimoto = result.getDouble(1);
		stmt.close();
		result.close();
		con.close();
		return biggestTanimoto;
	}
}
