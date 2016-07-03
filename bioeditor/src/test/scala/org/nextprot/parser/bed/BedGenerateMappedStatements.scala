package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import scala.xml.NodeSeq
import org.nextprot.commons.statements.RawStatement
import org.nextprot.parser.bed.commons.constants.BEDImpact.valueofModifiers
import org.nextprot.parser.bed.commons.constants.NXCategory.valueToCategry
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.parser.bed.service.BEDVariantService
import org.nextprot.parser.bed.service.StatementLoader
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.commons.statements.StatementField._
import org.nextprot.commons.statements._

class BedGenerateMappedStatements extends FlatSpec with Matchers {

  val location = "/Users/dteixeira/Documents/caviar/";
  val load = true;

  val genes = List("apc", "brca1", "brca2", "brip1", "epcam", "idh1", "mlh1", "mlh3",
    "msh2", "msh6", "mutyh", "pms2", "palb2", "scn1a", "scn2a", "scn3a",
    "scn4a", "scn5a", "scn8a", "scn9a", "scn10a", "scn11a");

  it should "group annotations together by subject and object" in {

    val pw = new PrintWriter(new File("vd.tsv"));

    val statements = scala.collection.mutable.Set[RawStatement]();

    genes.foreach(geneName => {

      val startTime = System.currentTimeMillis();

      println("Parsing " + geneName);

      BEDVariantService.reinitialize();

      val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/Documents/bed/" + geneName + ".xml"))

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

    })

    println("Total of " + statements.size + " statements")

    if (load) StatementLoader.init;

    val startTime = System.currentTimeMillis();

    statements.grouped(1000).toList.par.foreach(batchStatements => {

      if (load) {
        println("Loading " + batchStatements.size + " statements for  timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));
        StatementLoader.loadStatements(batchStatements.toList);
      }

    })
    println("Finished to load  timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));

    /*statements.filter(s => s.getValue(ANNOTATION_CATEGORY).equals("variant")) foreach (s => {
      val isoname = s.getValue(ANNOT_NAME).toLowerCase();
      //println("yooo" + isoname.substring(isoname.indexOf("iso"), 1));
      pw.write( s.getValue(NEXTPROT_ACCESSION) + "\t" + s.getValue(ANNOT_NAME) + "\t" + s.getValue(ANNOT_DESCRIPTION) + "\n");
    });*/

    statements.foreach (s => {
      pw.write(s.getValue(NEXTPROT_ACCESSION) + "\t" + s.getValue(ANNOT_NAME) + "\t" + s.getValue(ANNOT_DESCRIPTION) + "\n");
    });

    pw.close();

    if (load) StatementLoader.close;

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
        val variantEntryAccession = if(variantIsoAccession != null && variantIsoAccession.length() > 3){
        	 variantIsoAccession.substring(0, variantIsoAccession.indexOf("-"));
        }else {
          println("Some problems occured with " + variant.variantAccession);
          null;
        };

        val vdStmtBuilder = StatementBuilder.createNew();
        addEntryInfo(geneName, entryAccession, vdStmtBuilder);

        val nextprot_accession = variant.variantSequenceVariationPositionOnIsoform;
        if(subject.toLowerCase().contains("iso")){
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
    	.addField(EXP_CONTEXT_PROPERTY_INTENSITY, evidence.intensity)
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