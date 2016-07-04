package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq
import org.nextprot.parser.bed.commons.constants.BEDEffects
import org.nextprot.parser.bed.commons.constants.BEDEffects._
import org.nextprot.parser.bed.commons.constants.BEDImpact
import org.nextprot.parser.bed.commons.constants.BEDImpact._
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.commons.constants.BEDRelationsString._
import org.nextprot.parser.bed.commons.constants.NXTerminology
import org.nextprot.parser.bed.commons.constants.NXTerminology._
import org.nextprot.parser.bed.commons.constants.NXCategory
import org.nextprot.parser.bed.commons.constants.NXCategory._

object BEDUtils {

  def camelToDashes(name: String) = "[A-Z\\d]".r.replaceAllIn(name, { m =>
    "-" + m.group(0).toLowerCase()
  })

  def getEntryAccession(entry: NodeSeq): String = {
    return (entry \ "nxprotein" \ "@accession").text;
  }

  class RelationInfo(categories: List[NXCategory.Value], terminology: List[NXTerminology.Value], effect: BEDEffects.Value, impact: BEDImpact.Value, description: String, bioObject: Boolean) {

    def getAllowedTerminologies(): List[NXTerminology.Value] = { return terminology }
    def getEffect(): BEDEffects.Value = { return effect }
    def getBioObject(): Boolean = { return bioObject }
    def getAllowedCategories(): List[NXCategory.Value] = { return categories }
    def getImpact(): BEDImpact.Value = { return impact }
    def getDescription(): String = { return description }

    def getUnicity = {
      (terminology, effect, bioObject, categories, impact)
    }

  }

  class RelationInfoSimple(category: List[NXCategory.Value], terminoloy: List[NXTerminology.Value], effect: BEDEffects.Value, impact: BEDImpact.Value, bioObject: Boolean) extends RelationInfo(category, terminoloy, effect, impact, "", bioObject);

  val WRONG_BIOOBJECT = "BioEditor object does NOT correspond to the BioObject in neXtProt.";
  val NEED_CV_TERM = "Need to agree on annotation category and cv term";

  val noteForInteractions = "In neXtProt interactions do NOT have cv terms, right?";

  def getRelationInformation(relation: String, isNegative: Boolean): RelationInfo = {

    val NL = "\\n"; // new line
    val IS_NEGATIVE = true;
    val IS_POSITIVE = false;
    val l_BP_MF_Cv = List(GoBiologicalProcessCv, GoMolecularFunctionCv);
    val l_BP_MF = List(GoBiologicalProcess, GoMolecularFunction);

    (relation, isNegative) match {

      // Sub-cellular location ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // Sub-cellular location
      case (IncreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, INCREASE, false);
      case (DecreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, DECREASE, false);
      case (HasNormalLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, NOT_CHANGED, false);
      case (LocalisesToANewCompartment.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, GAIN, false);

      // Sub-cellular location (isNegative = IS_NEGATIVE)
      /*case (IncreasesLocalization.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case (DecreasesLocalization.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, AMBIGUOUS, false);
      case (HasNormalLocalization.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), EFFECT_ON_SUBCELLULAR_LOCALIZATION, AMBIGUOUS, false);
      case (LocalisesToANewCompartment.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");*/

      // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////

      // Effect on catalytic activity and cellular processes
      case (HasNormal.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, NOT_CHANGED, false);
      case (Impairs.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, CHANGED, false);
      case (Increases.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, INCREASE, false);
      case (Decreases.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, DECREASE, false);
      case (Gains.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, GAIN, false);

      // Effect on catalytic activity and cellular processes (isNegative = IS_NEGATIVE)
      /*case (HasNormal.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS, false);
      case (Impairs.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS, false);
      case (Increases.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS, false);
      case (Decreases.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS, false);
      case (Gains.name, IS_NEGATIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, EFFECT_ON_PROTEIN_ACTIVITY, AMBIGUOUS, false);*/

      // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
      case (HasNormalBinding.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, NOT_CHANGED, noteForInteractions, true);
      case (IncreasesBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, INCREASE, noteForInteractions, true);
      case (DecreasesBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, DECREASE, noteForInteractions, true);
      case (GainsBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, GAIN, noteForInteractions, true);

      // Effect on protein interaction (NEGATIVE)
      /*case (HasNormalBinding.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS, true);
      case (IncreasesBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS, true);
      case (DecreasesBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS, true);
      case (GainsBindingTo.name, IS_NEGATIVE) => return new RelationInfoSimple(List(BinaryInteraction), List(), EFFECT_ON_PROTEIN_INTERACTION, AMBIGUOUS, true);
      */

      // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      case (RemovesPTMSite.name, IS_POSITIVE) => return new RelationInfo(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, LOSS, "Removes PTM should be moved to MP - not a VP", false);
      case (GainsPTMSite.name, IS_POSITIVE) => return new RelationInfo(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, GAIN, "there might be a terminology term accession=PTM-0135 terminology=uniprot-ptm-cv", false);

      // Effect on phosphorylation (Negative)
      /*
      case (RemovesPTMSite.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, NOT_CHANGED, false);
      case (GainsPTMSite.name, IS_NEGATIVE) => return new RelationInfoSimple(List(GenericPtm), List(), EFFECT_ON_PHOSPHORYLATION, NOT_CHANGED, false);
      */

      // Effect on Mammalian Phenotype
      case (CausesPhenotype.name, IS_POSITIVE) => return new RelationInfo(List(MammalianPhenotype), List(MammalianPhenotypeCv), EFFECT_ON_MAMMALIAN_PHENOTYPE, GAIN, "Note that for phenotypes we have always gains or not changed", false);
      case (DoesNotCausePhenotype.name, IS_POSITIVE) => return new RelationInfo(List(MammalianPhenotype), List(MammalianPhenotypeCv), EFFECT_ON_MAMMALIAN_PHENOTYPE, NOT_CHANGED, "Note that for phenotypes we have always gains or not changed", false);
      //case (CausesPhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");
      case (DoesNotCausePhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      // Effect on stability ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (IsALabileFormOf.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_PROTEIN_STABILITY, DECREASE, WRONG_BIOOBJECT + NL + NEED_CV_TERM + NL + "Term could be: effect on proteinty stability or abundance or degradation.", false);
      case (IsAMoreStableFormOF.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_PROTEIN_STABILITY, INCREASE, WRONG_BIOOBJECT + NL + NEED_CV_TERM, false);
      case (HasNoEffectOnStability.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_PROTEIN_STABILITY, NOT_CHANGED, WRONG_BIOOBJECT + NL + NEED_CV_TERM, false);

      // Effect on stability (NEGATIVE)
      //case (IsALabileFormOf.name, IS_NEGATIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_PROTEIN_STABILITY, AMBIGUOUS, WRONG_BIOOBJECT + NL +  NEED_CV_TERM, false);
      //case (IsAMoreStableFormOF.name, IS_NEGATIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_PROTEIN_STABILITY, AMBIGUOUS, WRONG_BIOOBJECT + NL + NEED_CV_TERM, false);
      //case (HasNoEffectOnStability.name, IS_NEGATIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_PROTEIN_STABILITY, AMBIGUOUS, WRONG_BIOOBJECT + NL + NEED_CV_TERM, false);

      // Effect on substract
      case (IsAPoorerSubstrateFor.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_SUBSTRACT, DECREASE, NEED_CV_TERM, false);
      //case (IsAPoorerSubstrateFor.name, IS_NEGATIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_SUBSTRACT, AMBIGUOUS, NEED_CV_TERM, false);

      case (IsADominantNegativeForm.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_NEGATIVE_FORM, INCREASE, NEED_CV_TERM + NL + "Needs a flag because it is orthognal", false);
      //case (IsADominantNegativeForm.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      case _ => return throw new Exception("Relation " + relation + " is not supported");

    }

  }

}