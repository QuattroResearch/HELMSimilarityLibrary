The Graphical User Interface of HELMSimilarityLibrary is called by executing target/jfx/app/HELMSimilarityLibrary-<version>-jfx.jar. From the command line this would be "java -jar HELMSimilarityLibrary-UI.jar".
If you are having problems starting the UI please make sure you have a directory called .helm in your user directory. It must contain the two files Chemistry.property and MonomerStoreConfig.properties.

Input:
Please input a textfile with valid HELM notations in one of the following formats:
1. One HELM notation per line. Example:
    RNA1{R(A)P}$$$$V2.0
    RNA1{R(A)P.R(G)P}$$$$V2.0
    RNA1{R(G)P}$$$$V2.0

2. ID and HELM notation, separated by one tab, per line. Example:
    215 RNA1{R(A)P}$$$$V2.0
    189 RNA1{R(A)P.R(G)P}$$$$V2.0
    890 RNA1{R(G)P}$$$$V2.0
    
Build a database from your input textfile by executing the button "build database".

Options:
"Consider natural analogs": For the similarity calculation, there is a possible extension of taking the natural analogs of modified monomers into account. Depending on the use case, it will provide a more accurate similarity value.

Export:
You can export your results in a textfile by executing the "export results" button. It will generate a textfile with the results and is named after the input file with the suffix "_results".

IMPORTANT
Using the UI is going to create .db files at any time the "build database" button is executed, files with the same name are going to be overwritten. The .db files are safe to to deleted AFTER you have completed your calculations.

While using the UI and encountering a problem, please restart the program.