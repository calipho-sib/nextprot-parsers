package org.nextprot.parser.bed.service

import org.nextprot.parser.bed.datamodel.BEDAnnotation
import scala.xml.NodeSeq
import org.nextprot.parser.bed.datamodel.BEDCV
import org.nextprot.parser.bed.datamodel.BEDEvidence
import org.nextprot.parser.bed.commons.constants.NXCategory
import org.nextprot.parser.bed.commons.constants.NXCategory._
import org.nextprot.parser.bed.commons.constants.NXTerminology
import org.nextprot.parser.bed.commons.constants.NXTerminology._
import org.nextprot.parser.bed.utils.BEDUtils

object BEDAnnotationService {

  def getBEDEvidence(_annotationAccession: String, _subject: String, _relation: String, _objectTerm: BEDCV, _bioObject: String, xmlA: scala.xml.Node): List[BEDEvidence] = {
    return (xmlA \\ "evidence").map(e => {
      val allelsXml = e \\ "allelicCompositionVariantRef";
      val referencesXml = e \\ "reference";
      val isNegative = (e \\ "@isNegative").text.toBoolean;

      val references = referencesXml.map(r => {
        ((r \ "@database").text, (r \ "@accession").text)
      }).toList

      new BEDEvidence(_annotationAccession, _subject, _relation, _objectTerm, _bioObject, isNegative, allelsXml.map(n => n.text).toList, references);
    }).toList;
  }

  def getBEDAnnotations(entry: NodeSeq): List[BEDAnnotation] = {
    return (entry \\ "annotations" \\ "annotation").map(xmlA => {

      val _subject = (xmlA \ "subject" \ "molecularEntityRef").text
      val _relation = (xmlA \ "relationship" \ "cvName").text
      val _termXML = (xmlA \ "object" \ "term");
      val accession  = (_termXML\ "@accession").text;
      val category = (_termXML \ "@category").text;
      val cvName = (_termXML \ "cvName").text;
      
      val _objectTerm = new BEDCV(accession, category, cvName);
      val _bioObject = (xmlA \ "object" \ "molecularEntityRef").text

      val _accession = (xmlA \ "@accession").text

      val evidences = getBEDEvidence(_accession, _subject, _relation, _objectTerm, _bioObject, xmlA);
      new BEDAnnotation(_accession, _subject, _relation, _objectTerm, _bioObject, evidences);

    }).toList;
  }

}