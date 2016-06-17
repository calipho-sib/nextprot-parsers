package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import org.nextprot.commons.statements.RawStatement
import org.nextprot.parser.bed.commons.constants.BEDImpact.valueofModifiers
import org.nextprot.parser.bed.commons.constants.NXCategory.valueToCategry
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.parser.bed.service.BEDVariantService
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.sql.DriverManager
import java.sql.PreparedStatement
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION
import java.sql.Statement

class BedGenerateMappedStatements extends FlatSpec with Matchers {

  val location = "/Users/dteixeira/Documents/caviar/";

  val genes = Map(
    /*"apc" -> "NX_P25054",
    "brca1" -> "NX_P38398",
    "brca2" -> "NX_P51587",*/
    "brip1" -> "NX_Q9BX63"
    /*"epcam" -> "NX_P16422",
    "idh1" -> "NX_O75874",
    "mlh1" -> "NX_P40692",
    "mlh3" -> "NX_Q9UHC1",
    "msh2" -> "NX_P43246",
    "msh6" -> "NX_P52701", 
    "mutyh" -> "NX_Q9UIF7",
    "pms2" -> "NX_P54278", 
    "palb2" -> "NX_Q86YC2",
    "scn1a" -> "NX_P35498",
    "scn2a" -> "NX_Q99250",
    "scn3a" -> "NX_Q9NY46",
    "scn4a" -> "NX_P35499",
    "scn5a" -> "NX_Q14524",
    "scn8a" -> "NX_Q9UQD0",
    "scn9a" -> "NX_Q15858",
    "scn10a" -> "NX_Q9Y5Y9",
    "scn11a" -> "NX_Q9UI33" */);

  it should "group annotations together by subject and object" in {

    val statements = scala.collection.mutable.Set[RawStatement]();
    val conn = DriverManager.getConnection("jdbc:oracle:thin:nxbed/juventus@//fou.isb-sib.ch:1526/SIBTEST3");
    val statement = conn.createStatement();
     statement.addBatch("delete from mapped_statements");

    genes.keySet.foreach(geneName => {
      
      val startTime = System.currentTimeMillis();

      println("Parsing " + geneName);

      BEDVariantService.reinitialize();

      val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/Documents/caviar/" + geneName + ".xml"))

      val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
      val vpAnnotations = annotations.filter(a => a.isVP);
      val vpGoEvidences = vpAnnotations.flatMap(a => a._evidences).filter(e => (e.isVP && (e.isGO || e.isInteraction) && e.isSimple));

      //println(vpGoEvidences.size + "evidences");

      val entryAccession = genes.getOrElse(geneName, "");

      vpGoEvidences.foreach(vpgoe => {

        val variantStatement = getVariantDefinitionStatement(vpgoe, geneName, entryAccession);
        val normalStatement = getNormalStatement(vpgoe, geneName, entryAccession);

        statements += variantStatement;
        statements += normalStatement;
        statements += getVPStatement(vpgoe, variantStatement.getAnnot_hash(), normalStatement, geneName, entryAccession);
      });

      println("Loading " + statements.size + " statements for " + geneName + " timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));
      loadStatements(statement, statements.toList);
      println("Finished to load " + geneName + " statements for " + geneName + " timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));
      
    })

    
    conn.close();

  }

  def loadStatements(statement: Statement, statements: List[RawStatement]) = {

    val columnNames = RawStatement.getFieldNames(null).map(f => { "" + f + "" }).mkString(",");
    val bindVariableNames = RawStatement.getFieldNames(null).map(f => { ":" + f + "" }).mkString(",");

    statements.foreach(s => {
      val fieldValues = RawStatement.getFieldValues(s).map(v => {
        if(v!=null) {
          "'" + v.replaceAll("'", "''") + "'" //This done because of single quotes in the text
          } else null
      }).mkString(",");
      val sqlStatement = "INSERT INTO mapped_statements (" + columnNames + ") VALUES ( " + fieldValues + ")";
      statement.addBatch(sqlStatement);
    });
    statement.executeBatch();
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

  def getDescription(impact: String, normalStatement: RawStatement): String = {
    return DescriptionGenerator.getDescriptionForPhenotypeAnnotation(impact, normalStatement);
  }

  def getVPStatement(evidence: BEDEvidence,
    subjectAnnotationHash: String,
    normalStatement: RawStatement,
    geneName: String, entryAccession: String): RawStatement = {

    val vpStatement = new RawStatement();
    addEntryInfo(geneName, entryAccession, vpStatement);

    vpStatement.setAnnotation_category("phenotype"); //TODO how should this be named?

    vpStatement.setAnnot_cv_term_terminology("impact-cv"); //TODO how should this be named?
    vpStatement.setAnnot_cv_term_name(evidence.getRelationInfo.getImpact().name);
    vpStatement.setExp_context_property_intensity(evidence.intensity);

    vpStatement.setEvidence_source_accession(evidence._annotationAccession);
    vpStatement.setAnnot_description(getDescription(evidence.getRelationInfo.getImpact().name, normalStatement))
    vpStatement.setBiological_object_annot_hash(normalStatement.getAnnot_hash());
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
    normalStatement.setBiological_object_accession(evidence._bioObject);
    normalStatement.setBiological_object_type(evidence._bioObjectType);

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