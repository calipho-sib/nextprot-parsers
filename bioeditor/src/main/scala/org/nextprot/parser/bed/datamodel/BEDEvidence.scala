package org.nextprot.parser.bed.datamodel

import org.nextprot.parser.bed.utils.BEDUtils
import org.nextprot.parser.bed.utils.BEDUtils.RelationInfo
import org.nextprot.parser.bed.commons.constants.NXCategory
import org.nextprot.parser.bed.commons.constants.NXCategory._
import org.nextprot.parser.bed.commons.constants.NXTerminology
import org.nextprot.parser.bed.commons.constants.NXTerminology._
import org.apache.jena.ontology.Ontology
import org.nextprot.parser.bed.service.OntologyService

case class BEDEvidence(
  val _annotationAccession: String,
  val _subject: String,
  val _relation: String,
  val _bedObjectCvTerm: BEDCV,
  val _bioObject: String,
  val isNegative: Boolean,
  val vdAlleles: List[String],
  val references: List[(String, String)]) {

  val relationInfo = if (_annotationAccession.startsWith("CAVA-VP")) {
    BEDUtils.getRelationInformation(_relation, isNegative);
  } else null;

  def getNXCvTermAccession(): String = {
    return _bedObjectCvTerm.accession;
  }

  def getNXCvTermCvName(): String = {
    return _bedObjectCvTerm.cvName;
  }

  def getNXCategory(): NXCategory.Value = {

    if (_bedObjectCvTerm.category.equals("Gene Ontology")) {
      val subcategory = OntologyService.getGoSubCategoryFromAccession(_bedObjectCvTerm.accession);
      subcategory match {
        case GoMolecularFunctionCv.name => GoMolecularFunction;
        case GoBiologicalProcessCv.name => GoBiologicalProcess;
        case GoCellularComponentCv.name => GoCellularComponent;
        case _ => throw new Exception("not expecting category " + subcategory);
      }
    } else {
      val categories = BEDUtils.getRelationInformation(_relation, isNegative).getAllowedCategories();
      if (categories.size != 1) {
        throw new Exception("Expected one possible category for " + _relation + " " + isNegative + " found: " + categories + " term :" + _bedObjectCvTerm.category)
      } else categories(0);
    }
  }

  def getNXBioObject(): String = {
    if (relationInfo.getBioObject) {
      return _bioObject;
    } else return "";
  }

  def getNXTerminology(): NXTerminology.Value = {

    if (_bedObjectCvTerm.category.equals("Gene Ontology")) {
      val subcategory = OntologyService.getGoSubCategoryFromAccession(_bedObjectCvTerm.accession);
      subcategory match {
        case GoMolecularFunctionCv.name => GoMolecularFunctionCv;
        case GoBiologicalProcessCv.name => GoBiologicalProcessCv;
        case GoCellularComponentCv.name => GoCellularComponentCv;
        case _ => throw new Exception("not expecting terminology " + subcategory);
      }
    } else {
      val terminologies = BEDUtils.getRelationInformation(_relation, isNegative).getAllowedTerminologies();
      if (terminologies.size == 1) {
        terminologies(0);
      } else if (terminologies.size > 1) {
        throw new Exception("Expected one possible terminology for " + _relation + " " + isNegative + " found: " + terminologies)
      } else {
        null; // Not terminology
      };
    }
  }

  def isVP(): Boolean = {
    return _annotationAccession.contains("CAVA-VP");
  }

  def isGO(): Boolean = {
    return _bedObjectCvTerm.category.equals("Gene Ontology");
  }

    
  def getReferences: List[(String, String)] = {
    return references;
  }

  def getRealObject(): String = {
    return (if (_bedObjectCvTerm != null) { _bedObjectCvTerm.cvName } else "") + _bioObject;
  }

  def getRelationInfo(): RelationInfo = {
    return BEDUtils.getRelationInformation(_relation, isNegative);
  }

  def getRealSubject(): String = {
    if (vdAlleles.size > 1) {
      return vdAlleles.sortWith(_ > _).mkString(" + "); //TODO add allels
    } else {
      return _subject;
    }
  }

}
