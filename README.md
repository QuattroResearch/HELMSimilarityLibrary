The HELMSimilarityLibrary is a java library that provides functions to compute the similarity and the substructure relationship of HELM notations. Based on the enumeration of monomer paths, a fingerprint for each notation can be generated. The similarity is calculated via Tanimoto coefficient of two fingerprints. Substructure relationship is also determined by comparing two fingerprints. For the similarity calculation, there is a possible extension of taking the natural analogs of modified monomers into account. Depending on the use case, it will get a more accurate similarity value.

See APIs.txt for the use of HELMSimilarityLibrary.


IMPORTANT if you are using the graphical user interface of the library:
Extract HELMSimilarityLibrary-UI.zip. The UI is called by executing HELMSimilarityLibrary-<version>-jfx.jar. From the command line this would be "java -jar HELMSimilarityLibrary-<version>-jfx.jar".
If you are having problems starting the UI please make sure you have a directory called .helm in your user directory. It must contain the two files Chemistry.property and MonomerStoreConfig.properties. You might need to change "use.webservice=true" in MonomerStoreConfig.properties to "use.werbservice=false".
While using the UI and encountering a problem, please restart  the program.
See README-UI.md in HELMSimilarityLibrary-UI.zip archive for more information.