package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import org.nextprot.commons.statements.RawStatement
import org.nextprot.parser.bed.commons.constants.BEDImpact.valueofModifiers
import org.nextprot.parser.bed.commons.constants.NXCategory.valueToCategry
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.bed.service.BEDVariantService

class BEDGenerateEvidences extends FlatSpec with Matchers {

  val location = "/Users/dteixeira/Documents/caviar/";

  val genes = Map("brca1" -> "NX_P38398",
    "brca2" -> "NX_P51587",
    "apc" -> "NX_P25054",
    "brip1" -> "NX_Q9BX63",
    "epcam" -> "NX_P16422",
    "idh1" -> "NX_O75874",
    "mlh1" -> "NX_P40692",
    //"msh2" -> "NX_P43246",
    //"msh6" -> "NX_P52701",
    "mutyh" -> "NX_Q9UIF7",
    "palb2" -> "NX_Q86YC2");

  it should "group annotations together by subject and object" in {

    val statements = scala.collection.mutable.Set[RawStatement]();

    
    genes.keySet.foreach(geneName => {
      
      println(geneName);
      
      BEDVariantService.reinitialize();

      val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/Documents/caviar/" + geneName + ".xml"))

      val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
      val vpAnnotations = annotations.filter(a => a.isVP);
      val vpGoEvidences = vpAnnotations.flatMap(a => a._evidences).filter(e => (e.isVP && e.isGO && e.isSimple));

      //println(vpGoEvidences.size + "evidences");

      val entryAccession = genes.getOrElse(geneName, "");

      vpGoEvidences.foreach(vpgoe => {
        val variantStatement = getVariantDefinitionStatement(vpgoe, geneName, entryAccession);
        val normalStatement = getNormalStatement(vpgoe, geneName, entryAccession);

        statements += variantStatement;
        statements += normalStatement;
        statements += getVPStatement(vpgoe, variantStatement.getAnnot_hash(), normalStatement.getAnnot_hash(), geneName, entryAccession);
      });

    })

    println(statements.size);
    val pw = new PrintWriter(new File("/Users/dteixeira/Documents/file.tsv"))
    statements.foreach(s => {
      pw.write(s.getSeparatedValues("\t") + "\n");
    })
    pw.close();

  }

  def getVariantDefinitionStatement(evidence: BEDEvidence, geneName: String, entryAccession: String): RawStatement = {
    val vdStatement = new RawStatement();
    addEntryInfo(geneName, entryAccession, vdStatement);

    val variant: BEDVariant = evidence.variant;

    vdStatement.setAnnotation_category("variant"); //What about mutagenesis????
    vdStatement.setAnnot_name(variant.variantUniqueName);

    vdStatement.setAnnot_loc_begin_canonical_ref(variant.variantSequenceVariationPositionFirst);
    vdStatement.setAnnot_loc_end_canonical_ref(variant.variantSequenceVariationPositionLast);

    vdStatement.setVariant_original_amino_acid(variant.variantSequenceVariationOrigin);
    vdStatement.setVariant_variation_amino_acid(variant.variantSequenceVariationVariation);

    vdStatement.setAnnot_source_accession(variant.identifierAccession);
    vdStatement.setAnnot_source_database("BioEditor");

    //println(vdStatement.getSeparatedValues("\t"))
    return vdStatement;

  }

  def getVPStatement(evidence: BEDEvidence,
    subjectAnnotationHash: String,
    normalAnnotationHash: String,
    geneName: String, entryAccession: String): RawStatement = {

    val vpStatement = new RawStatement();
    addEntryInfo(geneName, entryAccession, vpStatement);

    vpStatement.setAnnotation_category("phenotype"); //TODO how should this be named?

    vpStatement.setAnnot_cv_term_terminology("impact-cv"); //TODO how should this be named?
    vpStatement.setAnnot_cv_term_name(evidence.getRelationInfo.getImpact().name);
    vpStatement.setEvidence_source_accession(evidence._annotationAccession);

    vpStatement.setBiological_object_annot_hash(normalAnnotationHash);
    vpStatement.setBiological_subject_annot_hash(subjectAnnotationHash);

    return vpStatement;

  }

  def getNormalStatement(evidence: BEDEvidence, geneName: String, entryAccession: String): RawStatement = {
    val normalStatement = new RawStatement();
    addEntryInfo(geneName, entryAccession, normalStatement);

    normalStatement.setAnnotation_category(evidence.getNXCategory().name)
    normalStatement.setAnnot_cv_term_terminology(evidence._bedObjectCvTerm.category) //TODO rename category to terminoloy...
    normalStatement.setAnnot_cv_term_accession(evidence._bedObjectCvTerm.accession)
    normalStatement.setAnnot_cv_term_name(evidence._bedObjectCvTerm.cvName)

    //DO NOT ADD accession because otherwise it creates N normal annotations  normalStatement.setAnnot_source_accession(evidence._annotationAccession);
    addDatabaseSourceInfo(normalStatement);

    return normalStatement;

  }

  def addEntryInfo(geneName: String, entryAccession: String, rawStatement: RawStatement) = {
    rawStatement.setEntry_accession(entryAccession);
    rawStatement.setGene_name(geneName);
    rawStatement.setIsoform_accession(entryAccession + "-1"); //TODO change this for all isoforms
  }

  def addDatabaseSourceInfo(rawStatement: RawStatement) = {
    rawStatement.setAnnot_source_database("BioEditor");
  }

}