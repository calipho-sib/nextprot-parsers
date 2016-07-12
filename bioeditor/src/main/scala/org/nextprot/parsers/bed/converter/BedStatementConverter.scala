package org.nextprot.parsers.bed.converter

import java.io.File
import java.io.PrintWriter
import scala.xml.NodeSeq
import org.nextprot.parsers.bed.commons.BEDImpact.valueofModifiers
import org.nextprot.parsers.bed.commons.NXCategory.valueToCategry
import org.nextprot.parsers.bed.service.BEDAnnotationService
import org.nextprot.parsers.bed.service.BEDVariantService
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.commons.statements.StatementField._
import org.nextprot.commons.statements._
import org.nextprot.commons.statements.RawStatement
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.parsers.bed.model.BEDEvidence

object BedServiceStatementConverter {

  // cp /Volumes/common/Calipho/caviar/xml/*.xml ~/Documents/bed/
  // cp /Volumes/common/Calipho/navmutpredict/xml/*.xml ~/Documents/bed/

  val location = "/Users/dteixeira/Documents/caviar/";
  val load = true;

  def convert(geneName: String): List[RawStatement] = {

    println("Starting for gene" + geneName);

    val statements = scala.collection.mutable.Set[RawStatement]();

    val startTime = System.currentTimeMillis();

    println("Parsing " + geneName);

    BEDVariantService.reinitialize();
    
    val f1 = new File("/share/sib/common/Calipho/caviar/xml/" + geneName + ".xml");
    val f2 = new File("/share/sib/common/Calipho/navmutpredict/xml/" + geneName + ".xml");
    
    val f = if(f1.exists()){ f1; }else { f2; }
    
    val entryElem = scala.xml.XML.loadFile(f);

    val nextprotAccession: String = (entryElem \ "@accession").text;

    val annotations = BEDAnnotationService.getBEDVPAnnotations(entryElem);
    //Take GO and interactions but ignore is negative
    val vpGoEvidences = annotations.flatMap(a => a._evidences).
      filter(e => ((e.isGO || e.isInteraction) && !e.isNegative && e.isSimple));

    vpGoEvidences.foreach(vpgoe => {

      val subjectVariants = getVariantDefinitionStatement(entryElem, vpgoe, geneName, nextprotAccession);
      val normalStatement = getNormalStatement(vpgoe, geneName, nextprotAccession);

      statements ++= subjectVariants;
      statements += normalStatement;
      statements += getVPStatement(vpgoe, subjectVariants, normalStatement, geneName, nextprotAccession);
    });

    return statements.toList;

  }

  def getVariantDefinitionStatement(entryXML: NodeSeq, evidence: BEDEvidence, geneName: String, entryAccession: String): List[RawStatement] = {

    val subjectsWithNote = evidence.getSubjectAllelsWithNote;

    val note = subjectsWithNote._2;
    val subjects = subjectsWithNote._1;
    subjects.map(subject => {

      val variant = BEDVariantService.getBEDVariantByUniqueName(entryXML, subject);

      //May be from a different genes in case of multiple mutants
      if (variant == null) {
        println(subject + " not found in entry" + geneName);
        null
      } else {

        val variantIsoAccession = variant.variantSequenceVariationPositionOnIsoform;
        val variantEntryAccession = if (variantIsoAccession != null && variantIsoAccession.length() > 3) {
          variantIsoAccession.substring(0, variantIsoAccession.indexOf("-"));
        } else {
          println("Some problems occured with " + variant.variantAccession + " when looking for evidence " + evidence._annotationAccession);
          null;
        };

        val vdStmtBuilder = StatementBuilder.createNew();

        val vGene = if (variant.variantUniqueName != null && variant.variantUniqueName.length() > 3) {
          variant.variantUniqueName.substring(0, variant.variantUniqueName.indexOf("-"))
        } else {
          println("Yooo problems occured with " + variant.identifierAccession + " when looking for evidence " + evidence._annotationAccession);
          null;
        };

        addEntryInfo(vGene, variantEntryAccession, vdStmtBuilder);

        val nextprot_accession = variant.variantSequenceVariationPositionOnIsoform;
        if (subject.toLowerCase().contains("iso")) {
          println(subject);
        }

        vdStmtBuilder.addField(NEXTPROT_ACCESSION, variantEntryAccession);

        vdStmtBuilder.addField(ANNOTATION_CATEGORY, "variant"); //What about mutagenesis????
        vdStmtBuilder.addField(ANNOT_NAME, subject);

        vdStmtBuilder.addField(ANNOT_LOC_BEGIN_CANONICAL_REF, variant.variantSequenceVariationPositionFirst);
        vdStmtBuilder.addField(ANNOT_LOC_END_CANONICAL_REF, variant.variantSequenceVariationPositionLast);

        vdStmtBuilder.addField(VARIANT_ORIGINAL_AMINO_ACID, variant.variantSequenceVariationOrigin);
        vdStmtBuilder.addField(VARIANT_VARIATION_AMINO_ACID, variant.variantSequenceVariationVariation);

        vdStmtBuilder.addField(ANNOT_SOURCE_ACCESSION, variant.identifierAccession);
        vdStmtBuilder.addField(ANNOT_SOURCE_DATABASE, "BioEditor");

        vdStmtBuilder.addField(ANNOT_DESCRIPTION, note);

        vdStmtBuilder.build();

      }

    }).filter(_ != null).toList

  }

  def getDescription(impact: String, normalStatement: RawStatement): String = {
    return DescriptionGenerator.getDescriptionForPhenotypeAnnotation(impact, normalStatement);
  }

  def getVPStatement(evidence: BEDEvidence,
                     subjectVDS: List[RawStatement],
                     normalStatement: RawStatement,
                     geneName: String, entryAccession: String): RawStatement = {

    val vpStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, vpStmtBuilder);

    vpStmtBuilder.addField(ANNOTATION_CATEGORY, "phenotype") //TODO how should this be named?
      .addField(ANNOT_CV_TERM_TERMINOLOGY, "impact-cv") //TODO how should this be named?
      .addField(ANNOT_CV_TERM_NAME, evidence.getRelationInfo.getImpact().name)
      .addField(STATEMENT_QUALITY, evidence._quality)
      .addField(EXP_CONTEXT_PROPERTY_INTENSITY, evidence.intensity)
      .addField(EXP_CTX_PRPTY_PROTEIN_ORIGIN, evidence.proteinOriginSpecie)
      .addField(ANNOT_SOURCE_ACCESSION, evidence._annotationAccession)
      .addField(ANNOT_DESCRIPTION, getDescription(evidence.getRelationInfo.getImpact().name, normalStatement))
      .addField(BIOLOGICAL_OBJECT_ANNOT_HASH, normalStatement.getAnnot_hash())
      .addField(BIOLOGICAL_SUBJECT_ANNOT_HASH, subjectVDS.map(v => v.getAnnot_hash()).mkString(","))
      .addField(BIOLOGICAL_SUBJECT_ANNOT_NAME, subjectVDS.map(v => v.getValue(ANNOT_NAME)).mkString(","));

    return vpStmtBuilder.build();

  }

  def getNormalStatement(evidence: BEDEvidence, geneName: String, entryAccession: String): RawStatement = {
    val normalStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, normalStmtBuilder);

    normalStmtBuilder.addField(ANNOTATION_CATEGORY, evidence.getNXCategory().name)
      .addField(ANNOT_CV_TERM_TERMINOLOGY, evidence._bedObjectCvTerm.category) //TODO rename category to terminoloy...
      .addField(ANNOT_CV_TERM_ACCESSION, evidence._bedObjectCvTerm.accession)
      .addField(ANNOT_CV_TERM_NAME, evidence._bedObjectCvTerm.cvName)
      .addField(BIOLOGICAL_OBJECT_ACCESSION, evidence._bioObject)
      .addField(BIOLOGICAL_OBJECT_TYPE, evidence._bioObjectType);

    //DO NOT ADD accession because otherwise it creates N normal annotations  normalStatement.setAnnot_source_accession(evidence._annotationAccession);
    addDatabaseSourceInfo(normalStmtBuilder);

    return normalStmtBuilder.build();

  }

  def addEntryInfo(geneName: String, entryAccession: String, statementBuilder: StatementBuilder) = {
    statementBuilder.addField(ENTRY_ACCESSION, entryAccession)
      .addField(GENE_NAME, geneName)
      .addField(ISOFORM_ACCESSION, entryAccession + "-1"); //TODO change this for all isoforms
  }

  def addDatabaseSourceInfo(statementBuilder: StatementBuilder) = {
    statementBuilder.addField(ANNOT_SOURCE_DATABASE, "BioEditor");
  }

}