package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq
import org.nextprot.parser.bed.commons.constants.BEDEffects
import org.nextprot.parser.bed.commons.constants.BEDEffects._
import org.nextprot.parser.bed.commons.constants.BEDModifiers
import org.nextprot.parser.bed.commons.constants.BEDModifiers._
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.commons.constants.BEDRelationsString._
import org.nextprot.parser.bed.commons.constants.BEDTerminology
import org.nextprot.parser.bed.commons.constants.BEDTerminology._
import org.nextprot.parser.bed.commons.constants.BEDCategory
import org.nextprot.parser.bed.commons.constants.BEDCategory._

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

  class RelationInfo(category: List[BEDCategory.Value], terminology: List[BEDTerminology.Value], effect: BEDEffects.Value, impacts: List[BEDModifiers.Value], description: String, bioObject: Boolean) {

    def getTerminology(): List[BEDTerminology.Value] = { return terminology }
    def getEffect(): BEDEffects.Value = { return effect }
    def getBioObject(): Boolean = { return bioObject }
    def getCategory(): List[BEDCategory.Value] = { return category }
    def getImpacts(): List[BEDModifiers.Value] = { return impacts }
    def getDescription(): String = { return description }

    def getImpactString: String = {
      if (impacts.size > 1) {
        return ("AMBIGOUS: " + impacts.mkString(" or "));
      } else return impacts(0).toString;
    }
  }

  class RelationInfoSimple(category: List[BEDCategory.Value], terminoloy: List[BEDTerminology.Value], effect: BEDEffects.Value, impacts: List[BEDModifiers.Value], bioObject: Boolean) extends RelationInfo(category, terminoloy, effect, impacts, "", bioObject);

  val NOT_A_VP = "May not be a VP";
  val WRONG_BIOOBJECT = "Note that on these cases the BioEditor object does NOT correspond to the BioObject";
  val NEED_CV_TERM = "We need a CV term for this either a cvTerm or Effect like Vario";

  def getRelationInformation(relation: String, isNegative: Boolean): RelationInfo = {

    val IS_NEGATIVE = true;
    val IS_POSITIVE = false;
    val l_BP_MF_Cv = List(GoBiologicalProcessCv, GoMolecularFunctionCv);
    val l_BP_MF = List(GoBiologicalProcess, GoMolecularFunction);
      
    (relation, isNegative) match {

      // Sub-cellular location ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // Sub-cellular location
      case (IncreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(INCREASE), false);
      case (DecreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(DECREASE), false);
      case (HasNormalLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED), false);
      case (LocalisesToANewCompartment.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(GAIN), false);

      // Sub-cellular location (isNegative = IS_NEGATIVE)
      case (IncreasesLocalization.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case (DecreasesLocalization.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED, INCREASE, GAIN), false);
      case (HasNormalLocalization.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, List(NOT_CHANGED, DECREASE, GAIN), false);
      case (LocalisesToANewCompartment.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////

      // Effect on catalytic activity and cellular processes
      case (HasNormal.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED), false);
      case (Impairs.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(CHANGED), false);
      case (Increases.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(INCREASE), false);
      case (Decreases.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(DECREASE), false);
      case (Gains.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(GAIN), false);

      // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
      case (HasNormal.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(CHANGED, INCREASE, DECREASE, GAIN), false);
      case (Impairs.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, INCREASE, DECREASE, GAIN), false);
      case (Increases.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, DECREASE, GAIN), false);
      case (Decreases.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, INCREASE, GAIN), false);
      case (Gains.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, List(NOT_CHANGED, CHANGED, INCREASE, DECREASE), false);

      // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
      case (HasNormalBinding.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(NOT_CHANGED), "NO CV TERM FOR THIS", true);
      case (IncreasesBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(INCREASE), "NO CV TERM FOR THIS BUT COULD BE", true);
      case (DecreasesBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(DECREASE), "NO CV T...", true);
      case (GainsBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(GAIN) , "NO CV TERM BUT COULD BE", true);

      // Effect on protein interaction (NEGATIVE)
      case (HasNormalBinding.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, INCREASE, DECREASE), true);
      case (IncreasesBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, NOT_CHANGED, DECREASE), true);
      case (DecreasesBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(GAIN, INCREASE, NOT_CHANGED), true);
      case (GainsBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, List(NOT_CHANGED, INCREASE, DECREASE), true);

      // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      case (RemovesPTMSite.name, IS_POSITIVE) => return new RelationInfo(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, List(LOSS), NOT_A_VP + "should be moved to MP" + "there might be a terminology term accession=PTM-0135 terminology=uniprot-ptm-cv", false);
      case (GainsPTMSite.name, IS_POSITIVE) => return new RelationInfo(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, List(GAIN), "there might be a terminology term accession=PTM-0135 terminology=uniprot-ptm-cv", false);

      // Effect on phosphorylation (Negative)
      case (RemovesPTMSite.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, List(NOT_CHANGED), false);
      case (GainsPTMSite.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, List(NOT_CHANGED), false);

      // Effect on Mammalian Phenotype
      case (CausesPhenotype.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(MammalianPhenotypeCv), EFFECT_ON_MAMMALIAN_PHENOTYPE, List(GAIN), NEED_CV_TERM, false);
      case (DoesNotCausePhenotype.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(MammalianPhenotypeCv), EFFECT_ON_MAMMALIAN_PHENOTYPE, List(NOT_CHANGED), NEED_CV_TERM, false);
      case (CausesPhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case (DoesNotCausePhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      // Effect on stability ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (IsALabileFormOf.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(),  EFFECT_ON_PROTEIN_STABILITY, List(DECREASE), WRONG_BIOOBJECT + NEED_CV_TERM, false);
      case (IsAMoreStableFormOF.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation),List(),  EFFECT_ON_PROTEIN_STABILITY, List(INCREASE), WRONG_BIOOBJECT + NEED_CV_TERM, false);
      case (HasNoEffectOnStability.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation),List(),  EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED), WRONG_BIOOBJECT + NEED_CV_TERM, false);

      // Effect on stability (NEGATIVE)
      case (IsALabileFormOf.name, IS_NEGATIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(), EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED, INCREASE), WRONG_BIOOBJECT + NEED_CV_TERM, false);
      case (IsAMoreStableFormOF.name, IS_NEGATIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(),  EFFECT_ON_PROTEIN_STABILITY, List(NOT_CHANGED, DECREASE), WRONG_BIOOBJECT + NEED_CV_TERM, false);
      case (HasNoEffectOnStability.name, IS_NEGATIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(), EFFECT_ON_PROTEIN_STABILITY, List(INCREASE, DECREASE), WRONG_BIOOBJECT + NEED_CV_TERM, false);

      // Effect on substract
      case (IsAPoorerSubstrateFor.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(), EFFECT_ON_SUBSTRACT, List(DECREASE), NEED_CV_TERM, false);
      case (IsAPoorerSubstrateFor.name, IS_NEGATIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(),  EFFECT_ON_SUBSTRACT, List(NOT_CHANGED, INCREASE), NEED_CV_TERM, false);

      case (IsADominantNegativeForm.name, IS_POSITIVE) => return new RelationInfo(List(ToBeDefinedAnnotation), List(), EFFECT_ON_NEGATIVE_FORM, List(INCREASE), NEED_CV_TERM + "Needs a flag because it is orthognal", false);
      case (IsADominantNegativeForm.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      case _ => return throw new Exception("Relation " + relation + " is not supported");

    }

  }

}