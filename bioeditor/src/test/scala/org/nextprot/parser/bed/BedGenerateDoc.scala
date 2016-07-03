package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parser.bed.utils.JSVariantObject
import org.nextprot.parser.bed.utils.JSAnnotationObject
import org.nextprot.parser.bed.utils.JSLinkObject
import org.nextprot.parser.bed.utils.JSNode
import scala.io.Source
import org.nextprot.parser.bed.utils.JSDescriptionObject
import org.nextprot.parser.bed.utils.JSImpactObject
import org.nextprot.parser.bed.utils.JSBioObject
import org.nextprot.parser.bed.commons.constants.BEDEffects
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.parser.bed.utils.JSTermObject
import org.nextprot.parser.bed.utils.JSSubjectComparedObject
import org.nextprot.parser.bed.utils.JSNoteObject
/*
class BEDGenerateDoc extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

  it should "group annotations together by subject and object" in {
    val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
    val vpAnnotations = annotations.filter(a => a.isVP);
    println("Total of annotations: " + vpAnnotations.length);

    val vpEvidences = vpAnnotations.flatMap(a => a._evidences).filter(e => e.isVP);
    println("Total of evidences: " + vpEvidences.length);
    println("Total of evidences not matched: " + vpEvidences.size);

    var diagramCode = "";
    var iteration = 1;

    List(false, true).foreach(negative => { //For positive and negative evidences

      BEDEffects.values.foreach(effect => { // For all effects

        vpEvidences.filter(
          e => (e.isNegative.equals(negative)) &&
            (e.getRelationInfo.getEffect.equals(effect))).
          groupBy(e => {

            val ri = e.getRelationInfo;
            val cat = e.getNXCategory;
            val term = ri.getAllowedTerminologies();
            val ef = ri.getEffect
            val impact = ri.getImpact
            val bioObject = ri.getBioObject

            (cat, term, ef, impact, bioObject)

          }).foreach(k => {

            val evidences = k._2
            val firstEvidence = evidences(0);
            val ri = firstEvidence.getRelationInfo;

            //This only takes 1st evidence
            val description = iteration + ") " + firstEvidence._subject + " <" + firstEvidence._relation + "> " + firstEvidence.getRealObject + { if (firstEvidence.isNegative) " NEGATIVE EVIDENCE" else "" } + " :" + firstEvidence._annotationAccession;

            val variantString = firstEvidence.getSubjectAllelsWithNote._1;
            val annotationString = "\\nannotationCategory:\\n" + firstEvidence.getNXCategory;

            //BOX For Terminology 
            val termString = if (firstEvidence.getNXTerminology != null) {
              "\\nterminology:" + firstEvidence.getNXTerminology.name +
                "\\naccession:" + firstEvidence._bedObjectCvTerm.accession +
                "\\ncvTerm:" + firstEvidence._bedObjectCvTerm.cvName;
            } else "";

            val impact = firstEvidence.getRelationInfo.getImpact;
            val effectString = firstEvidence.getRelationInfo.getEffect.name;

            val d1 = new JSDescriptionObject(iteration, description);

            val n1 = if (!ri.getDescription.isEmpty()) {
              new JSNoteObject(iteration, ri.getDescription);
            } else null;

            val v1 = new JSVariantObject(iteration, variantString);
            val a1 = new JSAnnotationObject(iteration, annotationString);
            val b1 = new JSBioObject(iteration, firstEvidence.getNXBioObject);
            val sc1 = new JSSubjectComparedObject(iteration, "BRCA1");

            val t1 = new JSTermObject(iteration, termString);

            val i1 = new JSImpactObject(iteration, /*"Impact: " +*/ impact.name /*+ "\\n(Effect:" + effectString + " )"*/);

            val l1 = new JSLinkObject(iteration, v1, a1, "");
            val l2 = new JSLinkObject(iteration, a1, i1, ":impact");
            val l3 = new JSLinkObject(iteration, a1, t1, ":term");
            val l4 = new JSLinkObject(iteration, sc1, a1, ":relativeTo");
            val l5 = new JSLinkObject(iteration, a1, b1, ":biologicalObject");

            val elements: List[JSNode] = List(d1, sc1, v1, a1, t1, b1, i1, l1, l2, l3, l4, l5, n1).filter(_ != null);

            elements.foreach(o => diagramCode ++= o.getTemplate)
            diagramCode ++= ("graph.addCells([" + elements.map(e => e.getId).mkString(",") + "]);");

            diagramCode ++= ("\nmarginHeight+=522;\n");
            iteration += 1;

          })

      })
    })

    val fileContent = Source.fromFile("doc/index-template.html").getLines.mkString("\n");
    val newContent = fileContent.replace("MACRO", diagramCode);
    new PrintWriter("doc/index2.html") { write(newContent); close }

  }

}*/