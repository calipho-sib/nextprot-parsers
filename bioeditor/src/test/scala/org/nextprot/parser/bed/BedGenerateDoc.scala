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
import org.nextprot.parser.bed.utils.JSEffectObject
import org.nextprot.parser.bed.utils.JSBioObject
import org.nextprot.parser.bed.commons.constants.BEDRelationTerms

class BEDGenerateDoc extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

  it should "group annotations together by subject and object" in {
    val annotations = BEDUtils.getBEDAnnotations(entryElem);
    //annotations.foreach(println);

    val vpAnnotations = annotations.filter(a => a.isVP);
    println("Total of annotations: " + vpAnnotations.length);

    val vpEvidences = vpAnnotations.flatMap(a => a._evidences);
    println("Total of evidences: " + vpEvidences.length);
    println("Total of evidences not matched: " + vpEvidences.filter(e => e.getTermAttributeRelation._1 == "not-defined").size);

    var diagramCode = "";
    var iteration = 1;

    List(false, true).foreach(negative => { //For positive and negative evidences

      BEDRelationTerms.ALL_EFFECTS.foreach(effect => { // For all effects

        vpEvidences.filter(e => (e.isNegative.equals(negative)) && (e.getTermAttributeRelation._1.equals(effect))).groupBy(e => e.getTermAttributeRelation).foreach(k => {

          //val entityKey = k._1._1;
          println(k._1);
          val term = k._1._1;
          val termImpact = k._1._2;
          //val goTerm = k._1._3;

          val evidences = k._2
          val firstEvidence = evidences(0);

          //This only takes 1st evidence
          val description = iteration + ") " + firstEvidence._subject + " " + firstEvidence._relation + " " + firstEvidence.getRealObject + { if (firstEvidence.isNegative) " NEGATIVE EVIDENCE" } + " " + firstEvidence._annotationAccesion;

          val variantString = firstEvidence.getRealSubject;
          val annotationString = "\\ncategory:" + firstEvidence._objectTerm.category + "\\ncvName:" + firstEvidence._objectTerm.name;

          val d1 = new JSDescriptionObject(iteration, description);

          val v1 = new JSVariantObject(iteration, variantString);
          val a1 = new JSAnnotationObject(iteration, annotationString);
          val b1 = new JSBioObject(iteration, firstEvidence._bioObject);

          val i1 = new JSImpactObject(iteration, termImpact);
          val e1 = new JSEffectObject(iteration, term);

          val l1 = new JSLinkObject(iteration, v1, a1, "");
          val l2 = new JSLinkObject(iteration, a1, i1, "impact");
          val l3 = new JSLinkObject(iteration, a1, e1, "effect");
          val l4 = new JSLinkObject(iteration, a1, b1, "biological-object");

          val elements: List[JSNode] = List(d1, v1, a1, b1, e1, i1, l1, l2, l3, l4);

          elements.foreach(o => diagramCode ++= o.getTemplate)
          diagramCode ++= ("graph.addCells([" + elements.map(e => e.getId).mkString(",") + "]);");

          diagramCode ++= ("\nmarginHeight+=495;\n");
          iteration += 1;

        })

      })
    })

    val fileContent = Source.fromFile("doc/index-template.html").getLines.mkString("\n");
    val newContent = fileContent.replace("MACRO", diagramCode);
    new PrintWriter("doc/index.html") { write(newContent); close }

  }

}