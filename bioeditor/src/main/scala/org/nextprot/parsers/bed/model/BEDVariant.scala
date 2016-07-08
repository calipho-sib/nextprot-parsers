package org.nextprot.parsers.bed.model

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

}
