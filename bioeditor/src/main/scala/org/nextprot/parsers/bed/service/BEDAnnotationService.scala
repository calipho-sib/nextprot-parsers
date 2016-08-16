package org.nextprot.parsers.bed.service

import org.nextprot.parsers.bed.model.BEDAnnotation
import scala.xml.NodeSeq
import org.nextprot.parsers.bed.model.BEDCV
import org.nextprot.parsers.bed.model.BEDEvidence
import org.nextprot.parsers.bed.commons.NXCategory
import org.nextprot.parsers.bed.commons.NXCategory._
import org.nextprot.parsers.bed.commons.NXTerminology
import org.nextprot.parsers.bed.commons.NXTerminology._
import org.nextprot.parsers.bed.commons.BEDUtils
import org.nextprot.parsers.bed.model.BEDVariant

object BEDAnnotationService {

  def getBEDEvidence(currentGene: String, _annotationAccession: String, _subject: String, _relation: String, _objectTerm: BEDCV, _bioObject: String, _bioObjectType: String, xmlA: scala.xml.Node): List[BEDEvidence] = {
    return (xmlA \\ "evidence").map(e => {

      val biologicalModelXml = e \ "biologicalModel";

      val allelsVD = (biologicalModelXml \\ "allelicCompositionVariantRef").map(n => n.text).toList;
      val allelsMGI = (biologicalModelXml \\ "allelicCompositionCv" \\ "cvName").map(n => n.text).toList;
      val allelsTXT = (biologicalModelXml \\ "allelicCompositionText").map(n => n.text).toList;


      val referencesXml = e \\ "reference";
      val experimentalPropertiesXml = e \\ "experimentalProperties" \\ "property";

      val intensity = if (!experimentalPropertiesXml.isEmpty) {
        val intensityProperties = experimentalPropertiesXml.filter(exp => (exp \ "@name").text.equals("intensity"));
        if (!intensityProperties.isEmpty)
          intensityProperties.toList.head.text;
        else null
      } else null

      val isNegative = (e \\ "@isNegative").text.toBoolean;
      val quality = (e \\ "@confidence").text;
      
      val subjectProteinOriginXml = (e \\ "proteinOrigin").filter { x => ((x \ "proteinRef").text).equalsIgnoreCase(currentGene) }
      val subjectProteinOriginSpecie = (subjectProteinOriginXml \ "species" \ "cvName").text; 

      val objectProteinOriginXml = (e \\ "proteinOrigin").filter { x => !((x \ "proteinRef").text).equalsIgnoreCase(currentGene) }
      val objectProteinOriginSpecie = (objectProteinOriginXml \ "species" \ "cvName").text; 

      val ecos = (e \\ "eco").map { e => ((e \ "@accession").text , (e \ "cvName").text)}.toList;
      
      val references = referencesXml.map(r => {
        ((r \ "@database").text, (r \ "@accession").text)
      }).toList

      
      new BEDEvidence(
          _annotationAccession, _subject, _relation, _objectTerm,
          _bioObject, _bioObjectType, intensity, isNegative, quality, 
          subjectProteinOriginSpecie, objectProteinOriginSpecie, ecos,
          allelsVD, allelsMGI, allelsTXT, references);
    }).toList;
  }

  def getBEDVPAnnotations(entry: NodeSeq): List[BEDAnnotation] = {

    val currentNextprotAccession: String = (entry \ "@accession").text;
    val currentNextprotGene: String = ((entry \ "molecularEntities" \\ "protein").filter(p => (p \ "@accession").text.equals(currentNextprotAccession)).head \ "@geneName").text;

    return (entry \\ "annotations" \\ "annotation").map(xmlA => {

      val _subject = (xmlA \ "subject" \ "molecularEntityRef").text
      val _relation = (xmlA \ "relationship" \ "cvName").text
      val _termXML = (xmlA \ "object" \ "term");
      val accession = (_termXML \ "@accession").text;
      val category = (_termXML \ "@category").text;
      val subCategory = (_termXML \ "@subCategory").text;
      val cvName = (_termXML \ "cvName").text;

      val _objectTerm = new BEDCV(accession, category, subCategory, cvName);
      val _bioObject = ((xmlA \ "object" \ "molecularEntityRef").text.split("-")).head //TODO Sprint (interactions) 2 just ignore variants and ignore phosphos https://calipho.isb-sib.ch/wiki/display/cal/Interactions+Specifications

      var break = false;

      val _bioObjectType = if (_bioObject == null) null
      else if (_bioObject.toUpperCase().startsWith("CHEBI:")) "chemical"
      else if (_bioObject.toUpperCase().startsWith("PG:")) { break = true; "proteinGroup" } //TODO Sprint (interactions) 2 just ignore Protein Groups https://calipho.isb-sib.ch/wiki/display/cal/Interactions+Specifications
      else if (_bioObject.contains("::")) { break = true; "complex" } //TODO Sprint (interactions) 2 just ignore Complexes https://calipho.isb-sib.ch/wiki/display/cal/Interactions+Specifications
      else "protein"

      val _accession = (xmlA \ "@accession").text

      if(!_accession.startsWith("CAVA-VP")){
        //println("skipping non vp" + _accession);
        null;
      } else if (break) {
        //println("skipping protein groups and complexes")
        null;
      } else if (!_subject.toLowerCase().startsWith(currentNextprotGene.toLowerCase())) {
        //println("skipping subjects with which are not directly related to the current entry" + _subject + " for entry " + currentNextprotGene)
        null;
      } else {

        val evidences = getBEDEvidence(currentNextprotGene, _accession, _subject, _relation, _objectTerm, _bioObject, _bioObjectType, xmlA);
        new BEDAnnotation(_accession, _subject, _relation, _objectTerm, _bioObject, evidences);

      }

    }).filter(_ != null).toList
  }

}