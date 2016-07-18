package org.nextprot.parsers.bed.converter

import java.io.File

import scala.collection.JavaConversions.setAsJavaSet
import scala.xml.NodeSeq

import org.nextprot.commons.statements.RawStatement
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.commons.statements.StatementField._
import org.nextprot.parsers.bed.BEDConstants
import org.nextprot.parsers.bed.commons.BEDImpact.valueofModifiers
import org.nextprot.parsers.bed.commons.NXCategory.valueToCategry
import org.nextprot.parsers.bed.model.BEDEvidence
import org.nextprot.parsers.bed.service.BEDAnnotationService
import org.nextprot.parsers.bed.service.BEDVariantService

object BedServiceStatementConverter {

  // cp /Volumes/common/Calipho/caviar/xml/*.xml ~/Documents/bed/
  // cp /Volumes/common/Calipho/navmutpredict/xml/*.xml ~/Documents/bed/

  var location = "/share/sib/common/Calipho/caviar/xml/";
  val load = true;

  def setProxyDir(directory: String) {
    location = directory;
  }

  def convertAll(): List[RawStatement] = {
    BEDConstants.GENE_LIST.flatMap { convert(_) }.toSet.toList;
  }

  def convert(geneName: String): List[RawStatement] = {

    val statements = scala.collection.mutable.Set[RawStatement]();

    val startTime = System.currentTimeMillis();

    println("Parsiiiing " + geneName);

    BEDVariantService.reinitialize();

    val f1 = new File(location + geneName + ".xml");
    val f2 = new File("/share/sib/common/Calipho/navmutpredict/xml/" + geneName + ".xml");

    val f = if (f1.exists()) { f1; } else { f2; }

    val entryElem = scala.xml.XML.loadFile(f);

    val nextprotAccession: String = (entryElem \ "@accession").text;

    val annotations = BEDAnnotationService.getBEDVPAnnotations(entryElem);
    //Take GO and interactions but ignore is negative
    val vpGoEvidences = annotations.flatMap(a => a._evidences).
      filter(e => ((e.isGO || e.isInteraction || e.isProteinProperty) && !e.isNegative));

    vpGoEvidences.foreach(vpgoe => {

      val subjectVariants = getVariantDefinitionStatement(entryElem, vpgoe, geneName, nextprotAccession);
      val normalStatement = getNormalStatement(vpgoe, geneName, nextprotAccession);

      statements ++= subjectVariants;
      statements += normalStatement;
      statements += getVPStatement(vpgoe, subjectVariants.toSet, normalStatement, geneName, nextprotAccession);
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
      val vdStmtBuilder = StatementBuilder.createNew();
      vdStmtBuilder.addDebugNote(note)
      if (variant == null) {
        val newNote = "Some problems occured with " + variant.variantAccession + " when looking for evidence " + evidence._annotationAccession;
        vdStmtBuilder.addDebugNote(newNote).build();
      } else {

        vdStmtBuilder.addDebugNote(note);

        val variantIsoAccession = variant.variantSequenceVariationPositionOnIsoform;
        val variantEntryAccession = if (variantIsoAccession != null && variantIsoAccession.length() > 3) {
          variantIsoAccession.substring(0, variantIsoAccession.indexOf("-"));
        } else {
          val note = "Some problems occured with " + variant.variantAccession + " when looking for evidence " + evidence._annotationAccession;
          vdStmtBuilder.addDebugNote(note);
          null;
        };

        val vGene = if (variant.variantUniqueName != null && variant.variantUniqueName.length() > 3) {
          variant.variantUniqueName.substring(0, variant.variantUniqueName.indexOf("-"))
        } else {
          val warning = "Yooo problems occured with " + variant.identifierAccession + " when looking for evidence " + evidence._annotationAccession;
          vdStmtBuilder.addDebugNote(warning);
          null;
        };

        addEntryInfo(vGene, variantEntryAccession, vdStmtBuilder);

        val nextprot_accession = variant.variantSequenceVariationPositionOnIsoform;
        if (subject.toLowerCase().contains("iso")) {
          vdStmtBuilder.addDebugNote(subject);
        }

        vdStmtBuilder.addField(NEXTPROT_ACCESSION, variantEntryAccession);
        vdStmtBuilder.addField(ANNOT_ISO_UNAME, subject);

        vdStmtBuilder.addVariantInfo(variant.variantSequenceVariationPositionFirst, variant.variantSequenceVariationPositionLast, variant.variantSequenceVariationOrigin, variant.variantSequenceVariationVariation);
        vdStmtBuilder.addSourceInfo(variant.identifierAccession, "BioEditor");

        vdStmtBuilder.addDebugNote(note);

        vdStmtBuilder.build();

      }

    }).filter(_ != null).toList

  }

  def getDescription(impact: String, normalStatement: RawStatement): String = {
    return DescriptionGenerator.getDescriptionForPhenotypeAnnotation(impact, normalStatement);
  }

  def getVPStatement(evidence: BEDEvidence,
                     subjectVDS: Set[RawStatement],
                     normalStatement: RawStatement,
                     geneName: String, entryAccession: String): RawStatement = {

    val vpStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, vpStmtBuilder);

    //Add subject and object
    vpStmtBuilder.addAnnotationSubject(subjectVDS).addAnnotationObject(normalStatement)

    vpStmtBuilder.addField(ANNOTATION_CATEGORY, "phenotype") //TODO how should this be named?
      .addField(ANNOT_CV_TERM_TERMINOLOGY, "impact-cv") //TODO how should this be named?
      .addField(ANNOT_CV_TERM_NAME, evidence.getRelationInfo.getImpact().name)
      .addField(STATEMENT_QUALITY, evidence._quality)
      .addField(EXP_CONTEXT_PROPERTY_INTENSITY, evidence.intensity)
      .addField(EXP_CTX_PRPTY_PROTEIN_ORIGIN, evidence.proteinOriginSpecie)
      .addField(ANNOT_SOURCE_ACCESSION, evidence._annotationAccession)
      .addField(ANNOT_DESCRIPTION, getDescription(evidence.getRelationInfo.getImpact().name, normalStatement));
    
    


    return vpStmtBuilder.build();

  }

  def getNormalStatement(evidence: BEDEvidence, geneName: String, entryAccession: String): RawStatement = {
    val normalStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, normalStmtBuilder);

    normalStmtBuilder.addField(ANNOTATION_CATEGORY, evidence.getNXCategory().name)
      .addCvTerm(evidence._bedObjectCvTerm.accession, evidence._bedObjectCvTerm.cvName, evidence._bedObjectCvTerm.category) //TODO rename category to terminology...
      .addField(BIOLOGICAL_OBJECT_ACCESSION, evidence._bioObject)
      .addField(BIOLOGICAL_OBJECT_TYPE, evidence._bioObjectType)

      //DO NOT ADD accession because otherwise it creates N normal annotations  normalStatement.setAnnot_source_accession(evidence._annotationAccession);
      //TODO To be checked
      .addSourceInfo("N/A", "BioEditor")

    return normalStmtBuilder.build();

  }

  def addEntryInfo(geneName: String, entryAccession: String, statementBuilder: StatementBuilder) = {
    statementBuilder.addField(ENTRY_ACCESSION, entryAccession)
      .addField(GENE_NAME, geneName)
      .addField(ISOFORM_ACCESSION, entryAccession + "-1"); //TODO change this for all isoforms
  }

}