package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq
import org.nextprot.parser.bed.commons.constants.BEDEffects
import org.nextprot.parser.bed.commons.constants.BEDEffects._
import org.nextprot.parser.bed.commons.constants.BEDModifiers
import org.nextprot.parser.bed.commons.constants.BEDModifiers._
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.commons.constants.BEDRelationsString._

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

  class RelationInfo (effect: BEDEffects.Value, impacts: List[BEDModifiers.Value], description: String) {

    def getEffect(): BEDEffects.Value = {return effect}
    def getImpacts(): List[BEDModifiers.Value] = {return impacts}
    def getDescription(): String = {return description}
    
    def getImpactString: String = {
      if (impacts.size > 1) {
        return ("AMBIGOUS: " + impacts.mkString(" or "));
      } else return impacts(0).toString;
    }
  }

  class RelationInfoSimple(effect: BEDEffects.Value, impacts: List[BEDModifiers.Value]) extends RelationInfo (effect, impacts, "");

  def getRelationInformation(relation: String, isNegative: Boolean): RelationInfo = {

    val IS_NEGATIVE = true;
    val IS_POSITIVE = false;

    (relation, isNegative) match {

      // Sub-cellular location ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // Sub-cellular location
      case (IncreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(INCREASE));
      case (DecreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(DECREASE));
      case (HasNormalLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED));
      case (LocalisesToANewCompartment.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(GAIN));

      // Sub-cellular location (isNegative = IS_NEGATIVE)
      case (IncreasesLocalization.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case (DecreasesLocalization.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED, INCREASE, GAIN));
      case (HasNormalLocalization.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED, DECREASE, GAIN));
      case (LocalisesToANewCompartment.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////

      // Effect on catalytic activity and cellular processes
      case (HasNormal.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED));
      case (Impairs.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(CHANGED));
      case (Increases.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(INCREASE));
      case (Decreases.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(DECREASE));
      case (Gains.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(GAIN));

      // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
      case (HasNormal.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(CHANGED, INCREASE, DECREASE, GAIN));
      case (Impairs.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, INCREASE, DECREASE, GAIN));
      case (Increases.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, DECREASE, GAIN));
      case (Decreases.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, INCREASE, GAIN));
      case (Gains.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, INCREASE, DECREASE));

      // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
      case (HasNormalBinding.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(NOT_CHANGED));
      case (IncreasesBindingTo.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(INCREASE));
      case (DecreasesBindingTo.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(DECREASE));
      case (GainsBindingTo.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN));

      // Effect on protein interaction (NEGATIVE)
      case (HasNormalBinding.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, INCREASE, DECREASE));
      case (IncreasesBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, NOT_CHANGED, DECREASE));
      case (DecreasesBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, INCREASE, NOT_CHANGED));
      case (GainsBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_INTERACTION, List(NOT_CHANGED, INCREASE, DECREASE));

      // Effect on stability ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (IsALabileFormOf.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(DECREASE));
      case (IsAMoreStableFormOF.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(INCREASE));
      case (HasNoEffectOnStability.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED));

      // Effect on stability (NEGATIVE)
      case (IsALabileFormOf.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED, INCREASE));
      case (IsAMoreStableFormOF.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED, DECREASE));
      case (HasNoEffectOnStability.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PROTEIN_STABILITY, List(INCREASE, DECREASE));

      // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      case (RemovesPTMSite.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(LOSS));
      case (GainsPTMSite.name, IS_POSITIVE) => return new RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(GAIN));

      // Effect on phosphorylation (Negative)
      case (RemovesPTMSite.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(NOT_CHANGED));
      case (GainsPTMSite.name, IS_NEGATIVE) => return new RelationInfoSimple(EFFECT_ON_PHOSPHORYLATION, List(NOT_CHANGED));

      // Effect on Mammalian Phenotype
      case (CausesPhenotype.name, IS_POSITIVE) => return new RelationInfo(EFFECT_ON_MAMMALIAN_PHENOTYPE, List(GAIN), "impact gain???");
      case (DoesNotCausePhenotype.name, IS_POSITIVE) => return new RelationInfo(EFFECT_ON_MAMMALIAN_PHENOTYPE, List(NOT_CHANGED), "MammalianPhenotype???");
      case (CausesPhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case (DoesNotCausePhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      
      // Effect on substract
      case (IsAPoorerSubstrateFor.name, IS_POSITIVE) => return new RelationInfo(EFFECT_ON_SUBSTRACT, List(DECREASE), "???");
      case (IsAPoorerSubstrateFor.name, IS_NEGATIVE) =>  return new RelationInfo(EFFECT_ON_SUBSTRACT, List(NOT_CHANGED, INCREASE), "How to translate this?");

      case (IsADominantNegativeForm.name, IS_POSITIVE) => return new RelationInfo(EFFECT_ON_NEGATIVE_FORM, List(INCREASE), "impact on negative form, is it increase???");
      case (IsADominantNegativeForm.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      
      case _ => return throw new Exception("Relation " + relation + " is not supported");

    }

  }

}