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
package org.pistoiaalliance.helm.HELMSimilarityLibrary.gui.layout;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * AlertBox class implements alert dialogs to update the user about
 * errors, information and help.
 * 
 * @author bueltel
 *
 */
public class AlertBox {
	public static Alert alertDialog;

	public static void ErrorBox(String errorType, String content) {
		alertDialog = new Alert(AlertType.ERROR);
		alertDialog.setTitle("Error");
		alertDialog.setHeaderText(errorType);
		alertDialog.setContentText(content);
		alertDialog.showAndWait();
	}
	
	public static void InfoBox(String content) {
		alertDialog = new Alert(AlertType.INFORMATION);
		alertDialog.setTitle("Info");
		alertDialog.setHeaderText("Information");
		alertDialog.setContentText(content);
		alertDialog.showAndWait();
	}
	
	public static void HelpBox(String content) {
		alertDialog = new Alert(AlertType.NONE);
		alertDialog.setTitle("Help");
		alertDialog.setHeaderText("Help");
		alertDialog.setContentText(content);
		alertDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		alertDialog.showAndWait();
	}
}
