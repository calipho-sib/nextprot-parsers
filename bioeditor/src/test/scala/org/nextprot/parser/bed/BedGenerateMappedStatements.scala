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
import oracle.jdbc.internal.OraclePreparedStatement
import oracle.jdbc.OracleStatement
import org.nextprot.parser.bed.service.StatementLoader

class BedGenerateMappedStatements extends FlatSpec with Matchers {

  val location = "/Users/dteixeira/Documents/caviar/";

  val genes = List( 
    "apc", "brca1" ,"brca2","brip1", "epcam","idh1", "mlh1", "mlh3", 
    "msh2", "msh6", "mutyh", "pms2", "palb2", "scn1a", "scn2a", "scn3a", 
    "scn4a", "scn5a", "scn8a", "scn9a", "scn10a", "scn11a");

  it should "group annotations together by subject and object" in {
        
    val statements = scala.collection.mutable.Set[RawStatement]();

    genes.foreach(geneName => {
      
      val startTime = System.currentTimeMillis();

      println("Parsing " + geneName);

      BEDVariantService.reinitialize();

      val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/Documents/caviar/" + geneName + ".xml"))
      
      val nextprotAccession : String = (entryElem \ "@accession").text;

      val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
      val vpAnnotations = annotations.filter(a => a.isVP);
      val vpGoEvidences = vpAnnotations.flatMap(a => a._evidences).filter(e => (e.isVP && (e.isGO || e.isInteraction) && e.isSimple));

      //println(vpGoEvidences.size + "evidences");

      vpGoEvidences.foreach(vpgoe => {

        val variantStatement = getVariantDefinitionStatement(vpgoe, geneName, nextprotAccession);
        val normalStatement = getNormalStatement(vpgoe, geneName, nextprotAccession);

        statements += variantStatement;
        statements += normalStatement;
        statements += getVPStatement(vpgoe, variantStatement.getAnnot_hash(), normalStatement, geneName, nextprotAccession);
      });

    })
    
    println("Total of " + statements.size + " statements")

    StatementLoader.init;
    
    val startTime = System.currentTimeMillis();

    statements.grouped(1000).toList.par.foreach(batchStatements => {

          //println("Loading " + batchStatements.size + " statements for  timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));
	      StatementLoader.loadStatements(batchStatements.toList);
	      //println("Finished to load " + geneName + " statements for " + geneName + " timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));

    })
	println("Finished to load  timeElapsedSoFar: " + (System.currentTimeMillis() - startTime));
          
    StatementLoader.close;

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