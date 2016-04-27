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

		  // Sub-cellular location ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  		  // Sub-cellular location
		  case ("increases localization to", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, INCREASE);
  		  case ("decreases localization to", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, DECREASE);
		  case ("has normal localization to", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED);
  		  case ("localizes to a new compartment", IS_POSITIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, GAIN);

  		  // Sub-cellular location (isNegative = IS_NEGATIVE)
  		  case ("decreases localization to", IS_NEGATIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, AMBIGUOUS + List(NOT_CHANGED, INCREASE, GAIN).mkString(" or "));
		  case ("has normal localization to", IS_NEGATIVE) => return (EFFECT_ON_SUBCELLULAR_LOCALIZATION, AMBIGUOUS + List(NOT_CHANGED, DECREASE, GAIN).mkString(" or ")); 
		  case ("increases localization to", IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
		  case ("localizes to a new compartment", IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
		  
  		  // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////
	  
  		  // Effect on catalytic activity and cellular processes
		  case ("has normal", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED);
  		  case ("impairs", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, CHANGED);
		  case ("increases", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, INCREASE);
  		  case ("decreases", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, DECREASE);
  		  case ("gains function", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, GAIN);


  		  // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
		  case ("has normal", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS + List(CHANGED, INCREASE, DECREASE, GAIN).mkString(" or "));
		  case ("impairs", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS + List(NOT_CHANGED, INCREASE, DECREASE, GAIN).mkString(" or "));
		  case ("increases", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS + List(NOT_CHANGED, CHANGED, DECREASE, GAIN).mkString(" or "));
  		  case ("decreases", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS + List(NOT_CHANGED, CHANGED, INCREASE, GAIN).mkString(" or "));
  		  case ("gains function", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_ACTIVITY,  AMBIGUOUS + List(NOT_CHANGED, CHANGED, INCREASE, DECREASE).mkString(" or "));
		  
		  // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
  		  case ("has normal binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED);
  		  case ("increases binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, INCREASE);
		  case ("decreases binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, DECREASE);
		  case ("gains binding to", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, GAIN);

		  // Effect on protein interaction (NEGATIVE)
  		  case ("has normal binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS + List(GAIN, INCREASE, DECREASE).mkString(" or "));
  		  case ("increases binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS + List(GAIN, NOT_CHANGED, DECREASE).mkString(" or "));
		  case ("decreases binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS + List(GAIN, INCREASE, NOT_CHANGED).mkString(" or "));
		  case ("gains binding to", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS + List(NOT_CHANGED, INCREASE, DECREASE).mkString(" or "));
  		  

  		  // Effect on stability ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 		  case ("is a labile form of", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_STABILITY, DECREASE);
		  case ("is a more stable form of", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_STABILITY, INCREASE);
  		  case ("has no effect on stability of", IS_POSITIVE) => return (EFFECT_ON_PROTEIN_STABILITY, NOT_CHANGED);

  		  // Effect on stability (NEGATIVE)
  		  case ("is a labile form of", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_STABILITY, AMBIGUOUS + List(NOT_CHANGED, INCREASE).mkString(" or "));
		  case ("is a more stable form of", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_STABILITY, AMBIGUOUS + List(NOT_CHANGED, DECREASE).mkString(" or "));
  		  case ("has no effect on stability of", IS_NEGATIVE) => return (EFFECT_ON_PROTEIN_STABILITY, AMBIGUOUS + List(INCREASE, DECREASE).mkString(" or "));

    	  // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  		  case ("removes PTM site", IS_POSITIVE) => return (EFFECT_ON_PHOSPHORYLATION, LOSS);
  		  case ("gains PTM site", IS_POSITIVE) => return (EFFECT_ON_PHOSPHORYLATION, GAIN);
  		  
  		  // Effect on phosphorylation (Negative)
  		  case ("removes PTM site", IS_NEGATIVE) => return (EFFECT_ON_PHOSPHORYLATION, NOT_CHANGED);
  		  case ("gains PTM site", IS_NEGATIVE) => return (EFFECT_ON_PHOSPHORYLATION, NOT_CHANGED);
  		  
  		  // Effect on Mammalian Phenotype
  		  case ("causes phenotype", IS_POSITIVE) => return (EFFECT_ON_MAMMALIAN_PHENOTYPE, GAIN + "MammalianPhenotype???");
  		  case ("does not cause phenotype", IS_POSITIVE) => return (EFFECT_ON_MAMMALIAN_PHENOTYPE, NOT_CHANGED + "MammalianPhenotype???");
		  case ("causes phenotype", IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
		  case ("does not cause phenotype", IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

		  /*
		   * is a poorer substrate for	NEDD4L	protein
			 is a poorer substrate for	PG:ERK1/2	proteinGroup
			 is a dominant negative form of	SCN2A	protein
			 is a dominant negative form of	MSH2	protein
		   */
  		  case _ => return throw new Exception("Relation " + relation + " is not supported");
  		  
    }
    
    
   }

}