package org.nextprot.parser.hpa
import java.io.File
import scala.io.Source

/**
 * Configuration for HPA.
 * hpa.mapping.file property must be set with the file location for the mapping hpa -> swissprot
 * @author Alain Gateau and Daniel Teixeira
 */
object HPAConfig {

  def readHPACVTermsMapFile: Map[String, (String, String)] = {

    val mappingFileProperty = "hpa.mapping.file";
    val mappingFile = if (System.getProperty(mappingFileProperty) != null) {
      new File(System.getProperty(mappingFileProperty));
    } else null;

    if ((mappingFile == null)) {
      println(mappingFileProperty + " property is not set. Set environment " + mappingFileProperty + " system property");
      System.exit(1);
    } else if (!mappingFile.exists()) {
      println("Mapping file " + mappingFile.getName() + " not found!.");
      System.exit(1);
    } else println("Loading HPA->Swissprot mapping file: " + mappingFile)

    // Build once for all HPA-NX subcell location vocabulary map from file      
    val kvp = Source.fromFile(mappingFile).getLines().
      filter(!_.startsWith("#")). //Remove the lines starting with a comment
      map(_.split("\t+")).map(fields => fields(0) -> (fields(1), if(fields(2).startsWith("Note")) fields(2) else null)).toList //Split by tabs and take the first and second field

    val map = Map(kvp: _*)
    println(map.size + " mappings found: " + map)
    println()

    return map

  }

}
