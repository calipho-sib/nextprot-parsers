package org.nextprot.parsers.bed.commons

import scala.xml.NodeSeq

import org.nextprot.parsers.bed.commons.BEDImpact._
import org.nextprot.parsers.bed.commons.BEDRelationsString._
import org.nextprot.parsers.bed.commons.NXCategory._
import org.nextprot.parsers.bed.commons.NXTerminology._

object BEDUtils {

  def camelToDashes(name: String) = "[A-Z\\d]".r.replaceAllIn(name, { m =>
    "-" + m.group(0).toLowerCase()
  })

  def getEntryAccession(entry: NodeSeq): String = {
    return (entry \ "nxprotein" \ "@accession").text;
  }

  class RelationInfo(categories: List[NXCategory.Value], terminology: List[NXTerminology.Value], impact: BEDImpact.Value, description: String, bioObject: Boolean) {

    def getAllowedTerminologies(): List[NXTerminology.Value] = { return terminology }
    def getBioObject(): Boolean = { return bioObject }
    def getAllowedCategories(): List[NXCategory.Value] = { return categories }
    def getImpact(): BEDImpact.Value = { return impact }
    def getDescription(): String = { return description }

    def getUnicity = {
      (terminology, bioObject, categories, impact)
    }

  }

  class RelationInfoSimple(category: List[NXCategory.Value], terminoloy: List[NXTerminology.Value], impact: BEDImpact.Value, bioObject: Boolean) extends RelationInfo(category, terminoloy, impact, "", bioObject);

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
      case (IncreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), INCREASE, false);
      case (DecreasesLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), DECREASE, false);
      case (HasNormalLocalization.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), NOT_CHANGED, false);
      case (LocalisesToANewCompartment.name, IS_POSITIVE) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), GAIN, false);

      // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////

      // Effect on catalytic activity and cellular processes
      case (HasNormal.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, NOT_CHANGED, false);
      case (Impairs.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, CHANGED, false);
      case (Increases.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, INCREASE, false);
      case (Decreases.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, DECREASE, false);
      case (Gains.name, IS_POSITIVE) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, GAIN, false);

      // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
      case (HasNormalBinding.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), NOT_CHANGED, noteForInteractions, true);
      case (IncreasesBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), INCREASE, noteForInteractions, true);
      case (DecreasesBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), DECREASE, noteForInteractions, true);
      case (GainsBindingTo.name, IS_POSITIVE) => return new RelationInfo(List(BinaryInteraction), List(), GAIN, noteForInteractions, true);

      // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      case (RemovesPTMSite.name, IS_POSITIVE) => return new RelationInfo(List(GenericPtm), List(), LOSS, "Removes PTM should be moved to MP - not a VP", false);
      case (GainsPTMSite.name, IS_POSITIVE) => return new RelationInfo(List(GenericPtm), List(), GAIN, "there might be a terminology term accession=PTM-0135 terminology=uniprot-ptm-cv", false);

      // Effect on Mammalian Phenotype
      case (CausesPhenotype.name, IS_POSITIVE) => return new RelationInfo(List(MammalianPhenotype), List(MammalianPhenotypeCv), GAIN, "Note that for phenotypes we have always gains or not changed", false);
      case (DoesNotCausePhenotype.name, IS_POSITIVE) => return new RelationInfo(List(MammalianPhenotype), List(MammalianPhenotypeCv), NOT_CHANGED, "Note that for phenotypes we have always gains or not changed", false);
      case (DoesNotCausePhenotype.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      // Effect on stability ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (IsALabileFormOf.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), DECREASE, WRONG_BIOOBJECT + NL + NEED_CV_TERM + NL + "Term could be: effect on proteinty stability or abundance or degradation.", false);
      case (IsAMoreStableFormOF.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), INCREASE, WRONG_BIOOBJECT + NL + NEED_CV_TERM, false);
      case (HasNoEffectOnStability.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), NOT_CHANGED, WRONG_BIOOBJECT + NL + NEED_CV_TERM, false);

      // Effect on substract
      case (IsAPoorerSubstrateFor.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), DECREASE, NEED_CV_TERM, false);
      //case (IsAPoorerSubstrateFor.name, IS_NEGATIVE) => return new RelationInfo(List(VarioProteinProperty), List(), EFFECT_ON_SUBSTRACT, AMBIGUOUS, NEED_CV_TERM, false);

      case (IsADominantNegativeForm.name, IS_POSITIVE) => return new RelationInfo(List(VarioProteinProperty), List(), INCREASE, NEED_CV_TERM + NL + "Needs a flag because it is orthognal", false);
      //case (IsADominantNegativeForm.name, IS_NEGATIVE) => throw new Exception("NOT SUPPORTED");

      case _ => return throw new Exception("Relation " + relation + " is not supported");

    }

  }

}