package org.nextprot.parser.bed.model

case class BEDVariant(
  val variantUniqueName: String,
  val variantAccession: String,
  val variantSequenceVariationOrigin: String,
  val variantSequenceVariationVariation: String,
  val variantSequenceVariationPositionLast: String,
  val variantSequenceVariationPositionFirst: String,
  val variantSequenceVariationPositionOnIsoform: String,
  val identifierDatabase: String,
  val identifierAccession: String,
  val identifierURL: String,
  val originAccession: String,
  val originCategory: String,
  val originCvName: String,
  val predictedConsequenceAccession: String,
  val predictedConsequenceCategory: String,
  val predictedConsequenceCvName: String) {
  
  
  def getNextprotAnnotationCategory : String = {
    
      if(originCvName.contains("germline") || originCvName.contains("somatic")){
        return "variant";
      }else if (originCvName.contains("mutated")){
        return "mutagenesis";
      }else return null;
  }

  def getEcoCode : String = {
    
      //According to specs: https://issues.isb-sib.ch/browse/BIOEDITOR-464
      if(originCvName.contains("germline") || originCvName.contains("somatic")){
        return "ECO:0000219"; //  nucleotide sequencing assay evidence
      }else if (originCvName.contains("mutated")){
        return "ECO:0000269"; //experimental evidence used in manual assertion
      }else return null;
  }
  
}
