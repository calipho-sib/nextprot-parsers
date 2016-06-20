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
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.datamodel.BEDVariant

object BEDAnnotationService {

  def getBEDEvidence(subjectVariant: BEDVariant, _annotationAccession: String, _subject: String, _relation: String, _objectTerm: BEDCV, _bioObject: String, _bioObjectType: String, xmlA: scala.xml.Node): List[BEDEvidence] = {
    return (xmlA \\ "evidence").map(e => {
      val allelsXml = e \\ "allelicCompositionVariantRef";
      val referencesXml = e \\ "reference";
      val experimentalPropertiesXml = e \\ "experimentalProperties" \\ "property";
      
      val intensity = if(!experimentalPropertiesXml.isEmpty){
    	  val intensityProperties = experimentalPropertiesXml.filter(exp => (exp \ "@name").text.equals("intensity"));
    	  if(!intensityProperties.isEmpty)
    	    intensityProperties.toList.head.text;
    	  else null
      }else null
      
      val isNegative = (e \\ "@isNegative").text.toBoolean;

      val references = referencesXml.map(r => {
        ((r \ "@database").text, (r \ "@accession").text)
      }).toList

      new BEDEvidence(subjectVariant, _annotationAccession, _subject, _relation, _objectTerm, _bioObject, _bioObjectType, intensity, isNegative, allelsXml.map(n => n.text).toList, references);
    }).toList;
  }

  def getBEDAnnotations(entry: NodeSeq): List[BEDAnnotation] = {

  val currentNextprotAccession : String = (entry \ "@accession").text;
  val currentNextprotGene : String =  ((entry \ "molecularEntities" \\ "protein").filter(p => (p \ "@accession").text.equals(currentNextprotAccession)).head \ "@geneName").text;

    return (entry \\ "annotations" \\ "annotation").map(xmlA => {

      val _subject = (xmlA \ "subject" \ "molecularEntityRef").text
      val _relation = (xmlA \ "relationship" \ "cvName").text
      val _termXML = (xmlA \ "object" \ "term");
      val accession  = (_termXML\ "@accession").text;
      val category = (_termXML \ "@category").text;
      val cvName = (_termXML \ "cvName").text;
      
      val _objectTerm = new BEDCV(accession, category, cvName);
      val _bioObject = ((xmlA \ "object" \ "molecularEntityRef").text.split("-")).head //TODO Sprint (interactions) 2 just ignore variants and ignore phosphos https://calipho.isb-sib.ch/wiki/display/cal/Interactions+Specifications
      
      var break = false;

      val _bioObjectType = if(_bioObject == null) null
      else if(_bioObject.toUpperCase().startsWith("CHEBI:")) "chemical" 
      else if(_bioObject.toUpperCase().startsWith("PG:")) {break = true; "proteinGroup"} //TODO Sprint (interactions) 2 just ignore Protein Groups https://calipho.isb-sib.ch/wiki/display/cal/Interactions+Specifications
      else if(_bioObject.contains("::")) {break = true; "complex"} //TODO Sprint (interactions) 2 just ignore Complexes https://calipho.isb-sib.ch/wiki/display/cal/Interactions+Specifications
      else "protein"
        
      val _accession = (xmlA \ "@accession").text

      if(_subject.contains("+")){
        println("skipping multiple mutant")
        null;
      }else if(break){
        println("skipping protein groups and complexes")
        null;
      }else if(!_subject.toLowerCase().startsWith(currentNextprotGene.toLowerCase())){
        println("skipping subjects with which are not directly related to the current entry" + _subject + " for entry " + currentNextprotGene)
        null;
      }else {

        val variant = BEDVariantService.getBEDVariantByUniqueName(entry, _subject);
    	  val evidences = getBEDEvidence(variant,  _accession, _subject, _relation, _objectTerm, _bioObject, _bioObjectType, xmlA);
    	  new BEDAnnotation(variant, _accession, _subject, _relation, _objectTerm, _bioObject, evidences);

      }
      

    }).filter(_!=null).toList
  }

}