package org.nextprot.parser.bed

import java.io.File

import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BedExploreTest extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/beddata/data.xml"))

   it should "group annotations together by subject and object" in {
     val annotations = BEDUtils.getBEDAnnotations(entryElem);
     annotations.foreach(println);
     
     val annotationsGroupedBySubjectAndObject = annotations.groupBy(a => (a._subject + "---" + a._object));

     println(annotationsGroupedBySubjectAndObject.size);

     val annotationsWithMoreThanOneRelationForSameObject = annotationsGroupedBySubjectAndObject.filter(_._2.size > 1);

     println(annotationsWithMoreThanOneRelationForSameObject.size);
     println(annotationsWithMoreThanOneRelationForSameObject);
     
     annotationsWithMoreThanOneRelationForSameObject.foreach(a => {
       println(a._1 + " contains " + a._2.size + " relations for " );
     });
     
  }

}