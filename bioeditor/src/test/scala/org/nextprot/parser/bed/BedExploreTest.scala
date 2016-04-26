package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import spray.json.JsObject
import org.nextprot.parser.bed.utils.JSVariantObject
import org.nextprot.parser.bed.utils.JSAnnotationObject
import org.nextprot.parser.bed.utils.JSLinkObject
import org.nextprot.parser.bed.utils.JSNode
import scala.io.Source
import org.nextprot.parser.bed.utils.JSDescriptionObject
import org.nextprot.parser.bed.utils.JSImpactObject
import org.nextprot.parser.bed.utils.JSEffectObject

class BedExploreTest extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

   it should "group annotations together by subject and object" in {
     val annotations = BEDUtils.getBEDAnnotations(entryElem);
     //annotations.foreach(println);
     
     val vpAnnotations = annotations.filter(a => a.isVP);
     println("Total of annotations: " + vpAnnotations.length);
     
     val vpEvidences = vpAnnotations.flatMap(a => a._evidences);
     println("Total of evidences: " + vpEvidences.length);
     println("Total of evidences not matched: " + vpEvidences.filter(e=> e.getTermAttributeRelation._1 == "not-defined").size);
     
     val res = vpEvidences.
    		 filter(e=> e.getTermAttributeRelation._1 != "not-defined").
    		 groupBy(e => (e.getRealSubject, e.getTermAttributeRelation(), e.getRealObject));
     
     //println(res);
     
     val pw = new PrintWriter(new File("brca.tsv" ))
     
     var diagramCode = "";
     var iteration = 1;

     res.take(10).foreach(k => {

       val entityKey = k._1._1;
       val term = k._1._2._1;
       val termImpact = k._1._2._2;
       val goTerm = k._1._3;
       
       val evidences = k._2
       
       pw.write(List(entityKey, termImpact, term, goTerm, evidences.size).mkString("\t"));
       pw.write("\n");


     })
     //pw.close();

      val fileContent = Source.fromFile("doc/index-macro.html").getLines.mkString("\n");
      val newContent = fileContent.replace("MACRO", diagramCode);
      new PrintWriter("doc/index.html") { write(newContent); close }
     
     /*(a => (a._subject + "---" + a._object));

     println(annotationsGroupedBySubjectAndObject.size);

     val annotationsWithMoreThanOneRelationForSameObject = annotationsGroupedBySubjectAndObject.filter(_._2.size > 1);

     println(annotationsWithMoreThanOneRelationForSameObject.size);
     println(annotationsWithMoreThanOneRelationForSameObject);
     
     annotationsWithMoreThanOneRelationForSameObject.foreach(a => {
       println(a._1 + " contains " + a._2.size + " relations for " );
     });*/
     
  }
  
  

}