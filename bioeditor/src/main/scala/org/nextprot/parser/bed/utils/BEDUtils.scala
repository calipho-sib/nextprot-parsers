package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.datamodel.BEDAnnotation
import org.nextprot.parser.bed.datamodel.BEDAnnotation
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.datamodel.BEDEvidence


import org.nextprot.parser.bed.commons.constants.BEDRelationTerms._
import org.nextprot.parser.bed.commons.constants.BEDRelationAttributes._

object BEDUtils {

  def getEntryAccession(entry: NodeSeq): String = {
    return (entry \ "nxprotein" \ "@accession").text;
  }

  def getBEDVariants(entry: NodeSeq): List[BEDVariant] = {
    return (entry \ "variants" \\ "variant").map(xmlV => {

      //println(xmlV \ "@uniqueName");

      new BEDVariant((xmlV \ "@uniqueName").text)

    }).toList;
  }

  def getBEDAnnotations(entry: NodeSeq): List[BEDAnnotation] = {
    return (entry \\ "annotations" \\ "annotation").map(xmlA => {

      val _subject = (xmlA \ "subject" \ "molecularEntityRef").text
      val _relation = (xmlA \ "relationship" \ "cvName").text
      val _object = (xmlA \ "object" \ "term" \ "cvName").text

      val _accession = (xmlA \ "@accession").text

      val evidences = getBEDEvidence(_subject, _relation, _object, xmlA);
      new BEDAnnotation(_accession, _subject, _relation, _object, evidences);

    }).toList;
  }

  def getBEDEvidence(_subject : String, _relation : String, _object : String, xmlA: scala.xml.Node): List[BEDEvidence] = {
    return (xmlA \\ "evidence").map(e => {
      val allelsXml = e \\ "allelicCompositionVariantRef";
      val referencesXml = e \\ "reference";
      val references = referencesXml.map(r => {
        ((r \ "@database").text, (r \ "@accession").text)
      }).toList

      new BEDEvidence(_subject, _relation, _object, false, allelsXml.map(n => n.text).toList, references);
    }).toList;
  }
  
   def getTermAndAttribute (relation : String, isNegative : Boolean): (String, String) = {
    
    (relation, isNegative) match {

  		  // Sub-cellular location
		  case ("increases localization to", false) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, INCREASE);
  		  case ("decreases localization to", false) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, DECREASE);
		  case ("has normal localization to", false) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
  		  case ("localizes to a new compartment", false) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, GAIN);

  		  // Sub-cellular location (isNegative = true)
  		  case ("decreases localization to", true) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
		  case ("has normal localization to", true) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, CHANGED); // Loosing info here, decreases may be on a note
		  case ("increases localization to", true) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
		  case ("localizes to a new compartment", true) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, CHANGED); // check how this case is modelled

  		  // Effect on catalytic activity and cellular processes (isNegative = true)
		  case ("has normal", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED); // Loosing info here
  		  case ("impairs", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
		  case ("increases", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("decreases", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("gains", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);

		  
		  
		  
  		  // Effect on catalytic activity and cellular processes
		  case ("has normal", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("impairs", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED);
		  case ("increases", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, INCREASE);
  		  case ("decreases", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, DECREASE);
  		  case ("gains", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, GAIN);

  		  // Effect on catalytic activity and cellular processes (isNegative = true)
		  case ("has normal", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED); // Loosing info here
  		  case ("impairs", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
		  case ("increases", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("decreases", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("gains", true) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  

  		  // Effect on binding
  		  case ("has normal binding to", false) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
  		  case ("increases binding to", false) => return (EFFECT_ON_PROTEIN_INTERACTION, INCREASE);
		  case ("decreases binding to", false) => return (EFFECT_ON_PROTEIN_INTERACTION, DECREASE);
		  case ("gains binding to", false) => return (EFFECT_ON_PROTEIN_INTERACTION, GAIN);
  		  
  		  case ("has normal binding to", true) => return (EFFECT_ON_PROTEIN_INTERACTION, CHANGED); //Loose information in note
  		  case ("increases binding to", true) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
		  case ("decreases binding to", true) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
		  case ("gains binding to", true) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
  		  
  		  // Effect on stability
		  
  		  case ("removes PTM", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("gains PTM site", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED);
		  case ("is a labile form of", false) => return (EFFECT_ON_PROTEIN_ACTIVITY, INCREASE);
		  case ("is a more stable form of", false) => return (EFFECT_ON_PROTEIN_STABILITY, INCREASE);
  		  case ("has no effect on stability of", false) => return (EFFECT_ON_PROTEIN_STABILITY, NOT_CHANGED);
		  
  		  case ("is a more stable form of", true) => return (EFFECT_ON_PROTEIN_STABILITY, CHANGED);
  		  case ("has no effect on stability of", true) => return (EFFECT_ON_PROTEIN_STABILITY, NOT_CHANGED);
		  
  		  //causes phenotype	
  		  //does not cause phenotype	
  		  
  		  case _ => return ("not-defined", "not-defined");

    }
    
    
   }

}