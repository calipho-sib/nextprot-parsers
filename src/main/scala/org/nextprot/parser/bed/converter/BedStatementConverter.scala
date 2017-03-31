package org.nextprot.parser.bed.converter

import java.io.File

import scala.collection.JavaConversions.setAsJavaSet
import scala.xml.NodeSeq

import org.nextprot.commons.statements.Statement
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.commons.statements.StatementField._
import org.nextprot.parser.bed.commons.BEDImpact.valueofModifiers
import org.nextprot.parser.bed.commons.NXCategory.valueToCategry
import org.nextprot.parser.bed.model.BEDEvidence
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.parser.bed.service.BEDVariantService
import org.nextprot.parser.bed.commons.BEDImpact
import org.nextprot.commons.constants.QualityQualifier
import org.nextprot.parser.bed.ProxyDir
import org.nextprot.commons.statements.StatementField
import org.nextprot.parser.bed.commons.NXCategory
import org.apache.jena.ext.com.google.common.base.Optional

object BedStatementConverter {

  val proxyLocations = scala.collection.mutable.SortedSet[String]();
  proxyLocations.add("/share/sib/common/Calipho/nxflat-proxy/");

  val load = true;

  def addProxyDir(directory: String) {
    proxyLocations.add(directory);
  }

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def getProxyDir(database: String, release: String): ProxyDir = {

    val locations = proxyLocations.map { l => new File(List(l, database, release).mkString("/")) }.filter(_.exists()).toList;
    if (locations.isEmpty) {
      throw new RuntimeException("Could not find any proxy with valid entries")
    }

    return ProxyDir(locations(0), database, release)
  }

  def convertAll(proxyDir: ProxyDir): (List[Statement], String) = {

    val debugNotes = new StringBuffer();

    val geneNames = recursiveListFiles(proxyDir.directory)
      .filter { f => f.getName().toLowerCase().endsWith(".xml") && !f.getName().startsWith(".") }
      .map { f => f.getName().toLowerCase().replaceAll(".xml", "") }.toList;

    val statements = geneNames.flatMap { geneName =>

      val result = convert(proxyDir, geneName);
      val statements = result._1;
      debugNotes.append(result._2);

      statements

    }.toSet.toList;
    (statements, debugNotes.toString());

  }
  

  def convert(proxyDir: ProxyDir, geneName: String): (List[Statement], String) = {

    val debugNotes = new StringBuffer();

    val statements = scala.collection.mutable.Set[Statement]();

    val startTime = System.currentTimeMillis();

    BEDVariantService.reinitialize();

    val entryElem = scala.xml.XML.loadFile(proxyDir.directory + "/" + geneName + ".xml");

    val nextprotAccession: String = (entryElem \ "@accession").text;

    val annotations = BEDAnnotationService.getBEDVPAndVeAnnotations(entryElem);
    //Take GO and interactions but ignore is negative
    val vpGoEvidences = annotations.flatMap(a => a._evidences).
      filter(e => ((e.isVE || e.isGO || e.isBinaryInteraction || e.isProteinProperty || e.isMammalianPhenotype) && !e.isNegative && !e.isRegulation));

    vpGoEvidences.foreach(vpgoe => {

      val subjectVariants = getVariantDefinitionStatement(debugNotes, entryElem, vpgoe, geneName, nextprotAccession);

      statements ++= subjectVariants;

      val normalStatement = getNormalStatement(vpgoe, geneName, nextprotAccession);
      statements += normalStatement;

      //Used for example for binary interactions when A interacts with B, we can infer that B interacts with A
      val optionalAdditionalNormalStatement = inferAdditionalNormalStatement(normalStatement)
      if(optionalAdditionalNormalStatement.isDefined)
        statements += optionalAdditionalNormalStatement.get
        
      statements += getVPStatement(vpgoe, subjectVariants.toSet, normalStatement, geneName, nextprotAccession);

    });

    println("Finished converting for " + geneName + " " + statements.size + " statements with " + debugNotes.length() + " bytes of warning notes")
        
    return (statements.toList, debugNotes.toString());

  }
  
    def inferAdditionalNormalStatement(normalStatement: Statement): Option[Statement] = {
      
      //If we have a binary interaction A -> B, then we want to make B -> A, so we have a symetric relationship A <--> B
      if(normalStatement.getValue(StatementField.ANNOTATION_CATEGORY).equals(NXCategory.BinaryInteraction.name)) {
        val proteinBAccession = normalStatement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION)
        val proteinBGeneName = normalStatement.getValue(StatementField.BIOLOGICAL_OBJECT_NAME)

        val proteinAAccession = normalStatement.getValue(StatementField.ENTRY_ACCESSION)
        val proteinAAGeneName= normalStatement.getValue(StatementField.GENE_NAME)
      
        val statementB = StatementBuilder.createNew()
        .addMap(normalStatement)
        .addField(StatementField.ENTRY_ACCESSION, proteinBAccession)
        .addField(StatementField.GENE_NAME, proteinBGeneName)
        .addField(StatementField.BIOLOGICAL_OBJECT_ACCESSION, proteinAAccession)
        .addField(StatementField.BIOLOGICAL_OBJECT_NAME, proteinAAGeneName)
        .build()
        
        println(statementB)
        
        return Some(statementB);
        
      }else None
      
    }


  def getVariantDefinitionStatement(debugNotes: StringBuffer, entryXML: NodeSeq, vpEvidence: BEDEvidence, geneName: String, entryAccession: String): List[Statement] = {

    val subjectsWithNote = vpEvidence.getSubjectAllelsWithNote;

    val note = subjectsWithNote._2;
    val subjects = subjectsWithNote._1;
    subjects.map(subject => {

      val variant = BEDVariantService.getBEDVariantByUniqueName(entryXML, subject);

      //May be from a different genes in case of multiple mutants
      val vdStmtBuilder = StatementBuilder.createNew();
      addDebugNote(debugNotes, note)
      if (variant == null) {
        val newNote = "Some problems occured with variant " + subject + " when looking for evidence " + vpEvidence._annotationAccession;
        throw new RuntimeException(newNote)
        addDebugNote(debugNotes, newNote)
        null;
      } else {

        addDebugNote(debugNotes, note);

        val variantIsoAccession = variant.variantSequenceVariationPositionOnIsoform;
        val variantEntryAccession = if (variantIsoAccession != null && variantIsoAccession.length() > 3) {
          variantIsoAccession.substring(0, variantIsoAccession.indexOf("-"));
        } else {
          val note = "Some problems occured with " + variant.variantAccession + " when looking for evidence " + vpEvidence._annotationAccession;
          addDebugNote(debugNotes, note);
          null;
        };

        val vGene = if (variant.variantUniqueName != null && variant.variantUniqueName.length() > 3) {
          variant.variantUniqueName.substring(0, variant.variantUniqueName.indexOf("-"))
        } else {
          val warning = "Yooo problems occured with " + variant.identifierAccession + " when looking for evidence " + vpEvidence._annotationAccession;
          addDebugNote(debugNotes, warning);
          null;
        };

        addEntryInfo(vGene, variantEntryAccession, vdStmtBuilder);

        val nextprot_accession = variant.variantSequenceVariationPositionOnIsoform;

        vdStmtBuilder.addField(NEXTPROT_ACCESSION, variantEntryAccession);
        vdStmtBuilder.addField(ANNOTATION_NAME, subject);

        vdStmtBuilder.addVariantInfo(variant.getNextprotAnnotationCategory, variant.variantSequenceVariationPositionFirst, variant.variantSequenceVariationPositionLast, variant.variantSequenceVariationOrigin, variant.variantSequenceVariationVariation);
        vdStmtBuilder.addSourceInfo("N/A", "BioEditor");

        //According to specs qualtiy of the variant must always be GOLD https://issues.isb-sib.ch/browse/BIOEDITOR-399?jql=text%20~%20%22quality%20bed%22
        vdStmtBuilder.addQuality(QualityQualifier.GOLD);
        vdStmtBuilder.addField(EVIDENCE_CODE, variant.getEcoCode);

        //https://issues.isb-sib.ch/browse/NEXTPROT-1196 (we used to have a complicated rule BIOEDITOR-471)
        vdStmtBuilder.addField(REFERENCE_DATABASE, vpEvidence.getReferenceDatabase);
        vdStmtBuilder.addField(REFERENCE_ACCESSION, vpEvidence.getReferenceAccession);

        addDebugNote(debugNotes, note);

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
      .addField(ANNOTATION_OBJECT_SPECIES, evidence.objectProteinOrigin) //TODO should find out which one is which
      .addField(REFERENCE_DATABASE, evidence.getReferenceDatabase)
      .addField(REFERENCE_ACCESSION, evidence.getReferenceAccession)
      .addField(EVIDENCE_CODE, evidence.getEvidenceCode)
      .addField(EVIDENCE_NOTE, evidence.getEvidenceNote)
      .addSourceInfo("N/A", "BioEditor");

    return vpStmtBuilder.build();

  }

  def getNormalStatement(vpEvidence: BEDEvidence, geneName: String, entryAccession: String): Statement = {
    val normalStmtBuilder = StatementBuilder.createNew();
    addEntryInfo(geneName, entryAccession, normalStmtBuilder);

    normalStmtBuilder.addField(ANNOTATION_CATEGORY, vpEvidence.getNXCategory().name);

    if (vpEvidence.getNXBioObject() != null) {
      normalStmtBuilder.addField(BIOLOGICAL_OBJECT_ACCESSION, vpEvidence.getNXBioObject)
        .addField(BIOLOGICAL_OBJECT_NAME, vpEvidence._bioObject)
        .addField(BIOLOGICAL_OBJECT_TYPE, vpEvidence._bioObjectType)
    }

    if (vpEvidence._bedObjectCvTerm.accession != null && (!vpEvidence._bedObjectCvTerm.accession.isEmpty())) {
      normalStmtBuilder.addCvTerm(vpEvidence._bedObjectCvTerm.accession, vpEvidence._bedObjectCvTerm.cvName, vpEvidence._bedObjectCvTerm.category) //TODO rename category to terminology...
    }

    //DO NOT ADD accession because otherwise it creates N normal annotations  normalStatement.setAnnot_source_accession(evidence._annotationAccession);
    normalStmtBuilder.addSourceInfo("N/A", "BioEditor")

    if (BEDImpact.GAIN.equals(vpEvidence.getRelationInfo().getImpact())) {
      normalStmtBuilder.addField(IS_NEGATIVE, "true");
    }

    //according to specs the normal statements should contain the same eco and references as the VP
    normalStmtBuilder.addField(EVIDENCE_CODE, vpEvidence.getEvidenceCode());

    normalStmtBuilder.addField(REFERENCE_DATABASE, vpEvidence.getReferenceDatabase);
    normalStmtBuilder.addField(REFERENCE_ACCESSION, vpEvidence.getReferenceAccession);

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

  def addDebugNote(debugNotes: StringBuffer, note: String) = {
    if (note != null && note.length() > 0) {
      println(note)
      debugNotes.append(note + "\n");
    }
  }

}