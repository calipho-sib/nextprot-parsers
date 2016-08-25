package org.nextprot.parsers.bed.converter

import java.io.File

import scala.collection.JavaConversions.setAsJavaSet
import scala.xml.NodeSeq

import org.nextprot.commons.statements.Statement
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.commons.statements.StatementField._
import org.nextprot.parsers.bed.BEDConstants
import org.nextprot.parsers.bed.commons.BEDImpact.valueofModifiers
import org.nextprot.parsers.bed.commons.NXCategory.valueToCategry
import org.nextprot.parsers.bed.model.BEDEvidence
import org.nextprot.parsers.bed.service.BEDAnnotationService
import org.nextprot.parsers.bed.service.BEDVariantService
import org.nextprot.parsers.bed.commons.BEDImpact
import org.nextprot.commons.constants.QualityQualifier

object BedServiceStatementConverter {

  // cp /Volumes/common/Calipho/caviar/xml/*.xml ~/Documents/bed/
  // cp /Volumes/common/Calipho/navmutpredict/xml/*.xml ~/Documents/bed/

  val proxyLocations = scala.collection.mutable.SortedSet[String]();
  proxyLocations.add("/share/sib/common/Calipho/bed-data/2016-08-22/");
  //proxyLocations.add("/share/sib/common/Calipho/navmutpredict/xml/");
  
  val load = true;

  def addProxyDir(directory: String) {
    proxyLocations.add(directory);
  }

  def convertAll(): List[Statement] = {
    BEDConstants.GENE_LIST.flatMap { convert(_) }.toSet.toList;
  }

  def convert(geneName: String): List[Statement] = {

    val statements = scala.collection.mutable.Set[Statement]();

    val startTime = System.currentTimeMillis();

    BEDVariantService.reinitialize();

    val fileExistence = proxyLocations.filter { pl => new File(pl + geneName + ".xml").exists()};
    if (fileExistence.isEmpty)
      throw new RuntimeException("Can't find file " + geneName + ".xml in following locations: " + proxyLocations.mkString("\n"));

    val f = new File(fileExistence.toList(0) + geneName + ".xml");

    val entryElem = scala.xml.XML.loadFile(f);

    val nextprotAccession: String = (entryElem \ "@accession").text;

    val annotations = BEDAnnotationService.getBEDVPAnnotations(entryElem);
    //Take GO and interactions but ignore is negative
    val vpGoEvidences = annotations.flatMap(a => a._evidences).
      filter(e => ((e.isGO || e.isBinaryInteraction || e.isProteinProperty || e.isMammalianPhenotype) && !e.isNegative));

    vpGoEvidences.foreach(vpgoe => {

      val subjectVariants = getVariantDefinitionStatement(entryElem, vpgoe, geneName, nextprotAccession);

      statements ++= subjectVariants;
      
      val normalStatement = getNormalStatement(vpgoe, geneName, nextprotAccession);
      statements += normalStatement;
      statements += getVPStatement(vpgoe, subjectVariants.toSet, normalStatement, geneName, nextprotAccession);
      
    });

    return statements.toList;

  }

  def getVariantDefinitionStatement(entryXML: NodeSeq, evidence: BEDEvidence, geneName: String, entryAccession: String): List[Statement] = {

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

        vdStmtBuilder.addField(NEXTPROT_ACCESSION, variantEntryAccession);
        vdStmtBuilder.addField(ANNOTATION_NAME, subject);

        vdStmtBuilder.addVariantInfo(variant.getNextprotAnnotationCategory, variant.variantSequenceVariationPositionFirst, variant.variantSequenceVariationPositionLast, variant.variantSequenceVariationOrigin, variant.variantSequenceVariationVariation);
        vdStmtBuilder.addSourceInfo(variant.identifierAccession, "BioEditor");

        //According to specs qualtiy of the variant must always be GOLD https://issues.isb-sib.ch/browse/BIOEDITOR-399?jql=text%20~%20%22quality%20bed%22
        vdStmtBuilder.addQuality(QualityQualifier.GOLD);
        vdStmtBuilder.addField(EVIDENCE_CODE, variant.getEcoCode);

        //https://issues.isb-sib.ch/browse/BIOEDITOR-471
        if(variant.getNextprotAnnotationCategory.equals("mutagenesis")){

          if(variant.identifierAccession != null || variant.identifierAccession.isEmpty()){

            vdStmtBuilder.addField(REFERENCE_ACCESSION, variant.identifierAccession);
            vdStmtBuilder.addField(REFERENCE_DATABASE, variant.identifierDatabase);

          
          }else {
            
            vdStmtBuilder.addField(REFERENCE_ACCESSION, evidence.getPubmedId());
            vdStmtBuilder.addField(REFERENCE_DATABASE, "PubMed");

          }
          
        }else if(variant.getNextprotAnnotationCategory.equals("variant")){
          
          
          if(variant.identifierAccession == null || variant.identifierAccession.isEmpty()) {
            vdStmtBuilder.addField(DEBUG_NOTE, "Publication not found for " + variant.variantAccession);
          }else {
            vdStmtBuilder.addField(REFERENCE_ACCESSION, variant.identifierAccession);
            vdStmtBuilder.addField(REFERENCE_DATABASE, variant.identifierDatabase);
          }
          
        }else throw new RuntimeException("Variant " + variant.getNextprotAnnotationCategory + " is not expected at this point");
        

        vdStmtBuilder.addDebugNote(note);

        vdStmtBuilder.build();

      }

    }).filter(_ != null).toList

  }

  def getDescription(impact: String, normalStatement: Statement): String = {
    return DescriptionGenerator.getDescriptionForPhenotypeAnnotation(impact, normalStatement);
  }

  def getVPStatement(evidence: BEDEvidence,
                     subjectVDS: Set[Statement],
                     normalStatement: Statement,
                     geneName: String, entryAccession: String): Statement = {

    val vpStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, vpStmtBuilder);

    //Add subject and object
    vpStmtBuilder.addSubjects(subjectVDS)
    
      vpStmtBuilder.addField(ANNOTATION_CATEGORY, "phenotypic-variation")
      .addCvTerm(evidence.getRelationInfo.getImpact().accession, evidence.getRelationInfo.getImpact().name, "modification-effect-cv")
      .addField(ANNOT_DESCRIPTION, getDescription(evidence.getRelationInfo.getImpact().name, normalStatement))
      .addObject(normalStatement)
      
      vpStmtBuilder
      .addQuality(QualityQualifier.valueOf(evidence._quality))
      .addField(EVIDENCE_INTENSITY, evidence.intensity)
      .addField(ANNOTATION_SUBJECT_SPECIES, evidence.subjectProteinOrigin) //TODO should find out which one is which
      .addField(ANNOTATION_OBJECT_SPECIES, evidence.objectProteinOrigin)//TODO should find out which one is which
      .addField(ANNOT_SOURCE_ACCESSION, evidence._annotationAccession)
      .addField(REFERENCE_ACCESSION, "PubMed")
      .addField(REFERENCE_DATABASE, evidence.getPubmedId())
      .addField(EVIDENCE_CODE, evidence.getEvidenceCode)
      .addField(EVIDENCE_NOTE, evidence.getEvidenceNote)
      
    return vpStmtBuilder.build();

  }

  def getNormalStatement(vpEvidence: BEDEvidence, geneName: String, entryAccession: String): Statement = {
    val normalStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, normalStmtBuilder);

    normalStmtBuilder.addField(ANNOTATION_CATEGORY, vpEvidence.getNXCategory().name)
      .addCvTerm(vpEvidence._bedObjectCvTerm.accession, vpEvidence._bedObjectCvTerm.cvName, vpEvidence._bedObjectCvTerm.category) //TODO rename category to terminology...
      .addField(BIOLOGICAL_OBJECT_ACCESSION, vpEvidence.getNXBioObject)
      .addField(BIOLOGICAL_OBJECT_NAME, vpEvidence._bioObject)
      .addField(BIOLOGICAL_OBJECT_TYPE, vpEvidence._bioObjectType)

      //DO NOT ADD accession because otherwise it creates N normal annotations  normalStatement.setAnnot_source_accession(evidence._annotationAccession);
      //TODO To be checked
      .addSourceInfo("N/A", "BioEditor")

    if(BEDImpact.GAIN.equals(vpEvidence.getRelationInfo().getImpact())){
      normalStmtBuilder.addField(IS_NEGATIVE, "true");
    }

    //according to specs the normal statements should contain the same eco and references as the VP
    normalStmtBuilder.addField(EVIDENCE_CODE, vpEvidence.getEvidenceCode());

    normalStmtBuilder.addField(REFERENCE_DATABASE, "PubMed");
    normalStmtBuilder.addField(REFERENCE_DATABASE, vpEvidence.getPubmedId());
  
    normalStmtBuilder.addQuality(QualityQualifier.valueOf(vpEvidence._quality))
    return normalStmtBuilder.build();

  }

  def addEntryInfo(geneName: String, entryAccession: String, statementBuilder: StatementBuilder) = {
    statementBuilder.addField(ENTRY_ACCESSION, entryAccession)
      .addField(GENE_NAME, geneName.toUpperCase())
      .addField(ENTRY_ACCESSION, entryAccession.toUpperCase())
      .addField(ASSIGMENT_METHOD, "curated")
      .addField(ASSIGNED_BY, "NextProt")
      .addField(RESOURCE_TYPE, "publication")
      
  }

}