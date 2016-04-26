package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.datamodel.BEDAnnotation
import org.nextprot.parser.bed.datamodel.BEDAnnotation
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.commons.constants.BEDRelationTerms._
import org.nextprot.parser.bed.commons.constants.BEDRelationAttributes._
import org.nextprot.parser.bed.datamodel.BEDCV

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
      val _termXML = (xmlA \ "object" \ "term");
      val _objectTerm = new BEDCV((_termXML\ "@accession").text, (_termXML \ "@category").text, (_termXML \ "cvName").text);
      val _bioObject = (xmlA \ "object" \ "molecularEntityRef").text

      val _accession = (xmlA \ "@accession").text

      val evidences = getBEDEvidence(_accession, _subject, _relation, _objectTerm, _bioObject, xmlA);
      new BEDAnnotation(_accession, _subject, _relation, _objectTerm, _bioObject, evidences);

    }).toList;
  }

  def getBEDEvidence(_annotationAccession : String, _subject : String, _relation : String, _objectTerm : BEDCV, _bioObject : String, xmlA: scala.xml.Node): List[BEDEvidence] = {
    return (xmlA \\ "evidence").map(e => {
      val allelsXml = e \\ "allelicCompositionVariantRef";
      val referencesXml = e \\ "reference";
      val isNegative = (e \\ "@isNegative").text.toBoolean;

      val references = referencesXml.map(r => {
        ((r \ "@database").text, (r \ "@accession").text)
      }).toList

      new BEDEvidence(_annotationAccession, _subject, _relation, _objectTerm, _bioObject, isNegative, allelsXml.map(n => n.text).toList, references);
    }).toList;
  }
  
   def getTermAndAttribute (relation : String, isNegative : Boolean): (String, String) = {

	 val IS_NEGATIVE = true;
	 val IS_POSITIVE = false;

    (relation, isNegative) match {

  		  // Sub-cellular location
		  case ("increases localization to", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, INCREASE);
  		  case ("decreases localization to", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, DECREASE);
		  case ("has normal localization to", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
  		  case ("localizes to a new compartment", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, GAIN);

  		  // Sub-cellular location (isNegative = IS_NEGATIVE)
  		  case ("decreases localization to", IS_NEGATIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
		  case ("has normal localization to", IS_NEGATIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, CHANGED); // Loosing info here, decreases may be on a note
		  case ("increases localization to", IS_NEGATIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
		  case ("localizes to a new compartment", IS_NEGATIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, CHANGED); // check how this case is modelled

  		  // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
		  case ("has normal", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED); // Loosing info here
  		  case ("impairs", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
		  case ("increases", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("decreases", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("gains", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);

		  
		  
		  
  		  // Effect on catalytic activity and cellular processes
		  case ("has normal", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("impairs", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED);
		  case ("increases", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, INCREASE);
  		  case ("decreases", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, DECREASE);
  		  case ("gains", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, GAIN);

  		  // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
		  case ("has normal", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED); // Loosing info here
  		  case ("impairs", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
		  case ("increases", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("decreases", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("gains", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  

  		  // Effect on binding
  		  case ("has normal binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
  		  case ("increases binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, INCREASE);
		  case ("decreases binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, DECREASE);
		  case ("gains binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, GAIN);
  		  
  		  case ("has normal binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, CHANGED); //Loose information in note
  		  case ("increases binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
		  case ("decreases binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
		  case ("gains binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
  		  
  		  // Effect on stability
 		  case ("removes PTM site", IS_POSITIVE) => return (EFFECT_ON_PHOSPHORYLATION, NOT_CHANGED);
  		  case ("gains PTM site", IS_POSITIVE) => return (EFFECT_ON_PHOSPHORYLATION, CHANGED);
  		  
		  case ("is a labile form of", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, INCREASE);
		  case ("is a more stable form of", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_STABILITY, INCREASE);
  		  case ("has no effect on stability of", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_STABILITY, NOT_CHANGED);
  		  
  		  case ("is a labile form of", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, INCREASE);
		  case ("is a more stable form of", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_STABILITY, CHANGED);
  		  case ("has no effect on stability of", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_STABILITY, CHANGED);

		  case ("causes phenotype", IS_POSITIVE) => return (EFFECT_ON_MAMMALIAN_PHENOTYPE, GAIN);
  		  case ("does not cause phenotype", IS_POSITIVE) => return (EFFECT_ON_MAMMALIAN_PHENOTYPE, NOT_CHANGED + "???");
		  case ("causes phenotype", IS_NEGATIVE) => return (EFFECT_ON_MAMMALIAN_PHENOTYPE, NOT_CHANGED + "???");
  		  case ("does not cause phenotype", IS_NEGATIVE) => return (EFFECT_ON_MAMMALIAN_PHENOTYPE, GAIN);

  		  case _ => return ("not-defined", "not-defined");

    }
    
    
   }

}