package org.nextprot.parser.bed.service

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.xml.NodeSeq

import org.nextprot.parser.bed.datamodel.BEDVariant

object BEDVariantService {

  def reinitialize (){
   variantsByUniqueName = new HashMap();
   variants = List();
  }
  
  var variantsByUniqueName: Map[String, BEDVariant] = new HashMap();
  var variants: List[BEDVariant] = List();

  def getBEDVariants(entry: NodeSeq): List[BEDVariant] = {

    if (variants.isEmpty) {
      init(entry);
    }

    return variants;

  }

  def getBEDVariantByUniqueName(entry: NodeSeq, variantUniqueName: String): BEDVariant = {

    if (variants.isEmpty) {
      init(entry);
    }

    return variantsByUniqueName.getOrElse(variantUniqueName, null);
  }

  def init(entry: NodeSeq) {

    variants = (entry \\ "variants" \\ "variant").map(xmlV => {

      val variantUniqueName = (xmlV \ "@uniqueName").text;
      val variantAccession = (xmlV \ "@accession").text;
      val variantSequenceVariationOrigin = (xmlV \ "sequenceVariation" \ "original").text;
      val variantSequenceVariationVariation = (xmlV \ "sequenceVariation" \ "variation").text;
      
      val variantSequenceVariationPositionLast = (xmlV \ "sequenceVariation" \ "position" \ "@last").text;
      val variantSequenceVariationPositionFirst = (xmlV \ "sequenceVariation" \ "position" \ "@first").text;
      val variantSequenceVariationPositionOnIsoform = (xmlV \ "sequenceVariation" \ "position" \ "@onIsoform").text;

      val identifierDatabase = (xmlV \ "identifier" \ "@database").text;
      val identifierAccession = (xmlV \ "identifier" \ "@accession").text;
      val identifierURL = (xmlV \ "identifier" \ "url").text;

      val originAccession = (xmlV \ "origin" \ "@accession").text;
      val originCategory = (xmlV \ "origin" \ "@category").text;
      val originCvName = (xmlV \ "origin" \ "cvName").text;

      val predictedConsequenceAccession = (xmlV \ "predictedConsequence" \ "@accession").text;
      val predictedConsequenceCategory = (xmlV \ "predictedConsequence" \ "@category").text;
      val predictedConsequenceCvName = (xmlV \ "predictedConsequence" \ "cvName").text;

      val variant = new BEDVariant(variantUniqueName,
        variantAccession,
        variantSequenceVariationOrigin,
        variantSequenceVariationVariation,
        variantSequenceVariationPositionLast,
        variantSequenceVariationPositionFirst,
        variantSequenceVariationPositionOnIsoform,
        identifierDatabase,
        identifierAccession,
        identifierURL,
        originAccession,
        originCategory,
        originCvName,
        predictedConsequenceAccession,
        predictedConsequenceCategory,
        predictedConsequenceCvName);

      variantsByUniqueName.put(variant.variantUniqueName, variant);
      variant;

    }).toList;

  }

}