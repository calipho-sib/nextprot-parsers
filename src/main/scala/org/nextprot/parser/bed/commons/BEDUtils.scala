package org.nextprot.parser.bed.commons

import scala.xml.NodeSeq

import org.nextprot.parser.bed.commons.BEDImpact._
import org.nextprot.parser.bed.commons.BEDRelationsString._
import org.nextprot.parser.bed.commons.NXCategory._
import org.nextprot.parser.bed.commons.NXTerminology._

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

  def getRelationInformation(relation: String, isNegative: Boolean, annotationType: BEDAnnotationType.Value): RelationInfo = {

    val NL = "\\n"; // new line
    val IS_NEGATIVE = true; //Constants to make pattern matching easier to understand (do not change it)
    val IS_POSITIVE = false;  //Constants to make pattern matching easier to understand (do not change it)
    val l_BP_MF_Cv = List(GoBiologicalProcessCv, GoMolecularFunctionCv);
    val l_BP_MF = List(GoBiologicalProcess, GoMolecularFunction);

    (relation, isNegative, annotationType) match {

      // Sub-cellular location ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      // Sub-cellular location
      case (IncreasesLocalization.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), INCREASE, false);
      case (DecreasesLocalization.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), DECREASE, false);
      case (HasNoImpactOnLocalizationTo.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), NO_IMPACT, false);
      case (GainsLocalisationTo.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(GoCellularComponent), List(GoCellularComponentCv), GAIN, false);

      // Effect on catalytic activity and cellular processes //////////////////////////////////////////////////////////////////////////////////////////////////

      // Effect on catalytic activity and cellular processes
      case (HasNoImpactOn.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, NO_IMPACT, false);
      case (Impacts.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, IMPACT, false);
      case (Increases.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, INCREASE, false);
      case (Decreases.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, DECREASE, false);
      case (GainsFunction.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(l_BP_MF, l_BP_MF_Cv, GAIN, false);

      // Effect on protein interaction //////////////////////////////////////////////////////////////////////////////////////
      case (HasNoImpactOnBindingTo.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(BinaryInteraction), List(), NO_IMPACT, noteForInteractions, true);
      case (IncreasesBindingTo.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(BinaryInteraction), List(), INCREASE, noteForInteractions, true);
      case (DecreasesBindingTo.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(BinaryInteraction), List(), DECREASE, noteForInteractions, true);
      case (GainsBindingTo.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(BinaryInteraction), List(), GAIN, noteForInteractions, true);

      // Effect on protein property //////////////////////////////////////////////////////////////////////////////////////
      case (HasNormalProteinProperty.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(ProteinProperty), List(), NO_IMPACT, false);
      case (IncreasesProteinProperty.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(ProteinProperty), List(), INCREASE, false);
      case (DecreasesProteinProperty.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfoSimple(List(ProteinProperty), List(), DECREASE, false);
      
      // Effect on phosphorylation ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      case (RemovesPTMSite.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(GenericPtm), List(), DECREASE, "Removes PTM should be moved to MP - not a VP", false);
      case (GainsPTMSite.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(GenericPtm), List(), GAIN, "there might be a terminology term accession=PTM-0135 terminology=uniprot-ptm-cv", false);

      // Effect on Mammalian Phenotype
      case (CausesPhenotype.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(MammalianPhenotype), List(MammalianPhenotypeCv), IMPACT, "", false);
      case (DoesNotCausePhenotype.name, IS_POSITIVE, BEDAnnotationType.VP) => return new RelationInfo(List(MammalianPhenotype), List(MammalianPhenotypeCv), NO_IMPACT, "", false);

      // Effect on VE (electro-physiology)
      case (HasNoImpactOn.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), NO_IMPACT, false);
      case (HasNoImpactOnTemperatureDependanceOf.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), NO_IMPACT_ON_TEMPERATURE_DEPENDENCE_OF, false);

      case (Decreases.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), DECREASE, false);
      case (Increases.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), INCREASE, false);

      //TODO Modification effect (find correct BEDImpact)
      case (Impacts.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), IMPACT, false);
      
      case (ImpactsOnTemperatureDependanceOf.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), IMPACT_ON_TEMPERATURE_DEPENDENCE_OF, false);
      case (Depolarizes.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), DEPOLARIZE, false);
      case (Hyperpolarizes.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), HYPERPOLARIZE, false);
      case (Hastens.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), HASTEN, false);
      case (Slows.name, IS_POSITIVE, BEDAnnotationType.VE) => return new RelationInfoSimple(List(ElectrophysiologicalParameter), List(ElectrophysiologicalParameterCv), SLOW, false);
      
      case _ => return throw new Exception("Relation " + relation + " is not supported");

    }

  }

}