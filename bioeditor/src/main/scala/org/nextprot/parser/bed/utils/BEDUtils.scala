package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq

import org.nextprot.parser.bed.commons.constants.BEDEffects
import org.nextprot.parser.bed.commons.constants.BEDEffects.EFFECT_ON_MAMMALIAN_PHENOTYPE
import org.nextprot.parser.bed.commons.constants.BEDEffects.EFFECT_ON_PHOSPHORYLATION
import org.nextprot.parser.bed.commons.constants.BEDEffects.EFFECT_ON_PROTEIN_ACTIVITY
import org.nextprot.parser.bed.commons.constants.BEDEffects.EFFECT_ON_PROTEIN_INTERACTION
import org.nextprot.parser.bed.commons.constants.BEDEffects.EFFECT_ON_PROTEIN_STABILITY
import org.nextprot.parser.bed.commons.constants.BEDEffects.EFFECT_ON_SUBCELLULAR_LOCALIZATION
import org.nextprot.parser.bed.commons.constants.BEDModifiers
import org.nextprot.parser.bed.commons.constants.BEDModifiers.CHANGED
import org.nextprot.parser.bed.commons.constants.BEDModifiers.DECREASE
import org.nextprot.parser.bed.commons.constants.BEDModifiers.GAIN
import org.nextprot.parser.bed.commons.constants.BEDModifiers.INCREASE
import org.nextprot.parser.bed.commons.constants.BEDModifiers.LOSS
import org.nextprot.parser.bed.commons.constants.BEDModifiers.NOT_CHANGED
import org.nextprot.parser.bed.datamodel.BEDVariant

object BEDUtils {

  def camelToDashes(name: String) = "[A-Z\\d]".r.replaceAllIn(name, { m =>
    "-" + m.group(0).toLowerCase()
  })

  def getEntryAccession(entry: NodeSeq): String = {
    return (entry \ "nxprotein" \ "@accession").text;
  }

  def getBEDVariants(entry: NodeSeq): List[BEDVariant] = {
    return (entry \ "variants" \\ "variant").map(xmlV => {

      new BEDVariant((xmlV \ "@uniqueName").text)

    }).toList;
  }

  trait RelationInfo {
    def effect: BEDEffects.Value
    def impacts: List[BEDModifiers.Value]

    def getImpactString: String = {
      if (impacts.size > 1) {
        return ("AMBIGOUS" + impacts.mkString(" or "));
      } else return impacts(0).toString;
    }
  }

  case class RelationInfoSimple(effect: BEDEffects.Value, impacts: List[BEDModifiers.Value]) extends RelationInfo;
  case class RelationInfoExtended(effect: BEDEffects.Value, impacts: List[BEDModifiers.Value], description: String) extends RelationInfo;

  def getRelationInformation(relation: String, isNegative: Boolean): RelationInfo = {

    val IS_NEGATIVE = true;
    val IS_POSITIVE = false;

    (relation, isNegative) match {

      // Sub-cellular location ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // Sub-cellular location
      case ("increases localization to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(INCREASE));
      case ("decreases localization to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(DECREASE));
      case ("has normal localization to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED));
      case ("localizes to a new compartment", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(GAIN));

      // Sub-cellular location (isNegative = IS_NEGATIVE)
      case ("decreases localization to", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED, INCREASE, GAIN));
      case ("has normal localization to", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED, DECREASE, GAIN));
      case ("increases localization to", IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case ("localizes to a new compartment", IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////

      // Effect on catalytic activity and cellular processes
      case ("has normal", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED));
      case ("impairs", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(CHANGED));
      case ("increases", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(INCREASE));
      case ("decreases", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(DECREASE));
      case ("gains function", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(GAIN));

      // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
      case ("has normal", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(CHANGED, INCREASE, DECREASE, GAIN));
      case ("impairs", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, INCREASE, DECREASE, GAIN));
      case ("increases", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, DECREASE, GAIN));
      case ("decreases", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, INCREASE, GAIN));
      case ("gains function", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, INCREASE, DECREASE));

      // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
      case ("has normal binding to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(NOT_CHANGED));
      case ("increases binding to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(INCREASE));
      case ("decreases binding to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(DECREASE));
      case ("gains binding to", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN));

      // Effect on protein interaction (NEGATIVE)
      case ("has normal binding to", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, INCREASE, DECREASE));
      case ("increases binding to", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, NOT_CHANGED, DECREASE));
      case ("decreases binding to", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, INCREASE, NOT_CHANGED));
      case ("gains binding to", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(NOT_CHANGED, INCREASE, DECREASE));

      // Effect on stability ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case ("is a labile form of", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(DECREASE));
      case ("is a more stable form of", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(INCREASE));
      case ("has no effect on stability of", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED));

      // Effect on stability (NEGATIVE)
      case ("is a labile form of", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED, INCREASE));
      case ("is a more stable form of", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED, DECREASE));
      case ("has no effect on stability of", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(INCREASE, DECREASE));

      // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      case ("removes PTM site", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(LOSS));
      case ("gains PTM site", IS_POSITIVE) => return RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(GAIN));

      // Effect on phosphorylation (Negative)
      case ("removes PTM site", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(NOT_CHANGED));
      case ("gains PTM site", IS_NEGATIVE) => return RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(NOT_CHANGED));

      // Effect on Mammalian Phenotype
      case ("causes phenotype", IS_POSITIVE) => return RelationInfoExtended(EFFECT_ON_MAMMALIAN_PHENOTYPE, List(GAIN), "impact gain???");
      case ("does not cause phenotype", IS_POSITIVE) => return RelationInfoExtended(EFFECT_ON_MAMMALIAN_PHENOTYPE, List(NOT_CHANGED), "MammalianPhenotype???");
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