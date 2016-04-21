package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter

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
     
     val res = vpEvidences.filter(e=> e.getTermAttributeRelation._1 != "not-defined").groupBy(e => (e.getRealSubject, e.getTermAttributeRelation(), e._object));
     
     //println(res);
     
     val pw = new PrintWriter(new File("brca.tsv" ))
     res.foreach(k => {
       pw.write(k._1._1 + "\t" + k._1._2._1 + "\t" + k._1._2._2 + "\t" + k._1._3 + "\t" + k._2.size + "\n");
     })
     pw.close();
     
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