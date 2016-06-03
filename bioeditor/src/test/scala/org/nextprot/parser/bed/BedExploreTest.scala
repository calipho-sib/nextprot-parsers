package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parser.bed.service.BEDAnnotationService

class BedExploreTest extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

   it should "group annotations together by subject and object" in {

    val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
     val vpAnnotations = annotations.filter(a => a.isVP);
     println("Total of annotations: " + vpAnnotations.length);
     
     val vpEvidences = vpAnnotations.flatMap(a => a._evidences);
     println("Total of evidences: " + vpEvidences.length);
     println("Total of evidences not matched: " + vpEvidences.size);
     
     val res = vpEvidences.groupBy(e => (e.getRealSubject, e.getRelationInfo(), e.getRealObject));
     
     //println(res);
     
     val pw = new PrintWriter(new File("brca.tsv" ))
     res.take(10).foreach(k => {
       
       val entityKey = k._1._1;
       val term = k._1._2.getEffect;
       val impact = k._1._2.getImpact;
       val goTerm = k._1._3;
       
       val evidences = k._2
       
       pw.write(List(entityKey, term, impact, goTerm, evidences.size).mkString("\t"));
       pw.write("\n");
       
       //CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})
//CREATE (Keanu:Person {name:'Keanu Reeves', born:1964})
       println("CREATE (V" + entityKey.hashCode().toHexString + ":Variant {title:'" + entityKey + "'})")
       println("CREATE (O" + goTerm.hashCode().toHexString + ":Object {title:'" + goTerm + "'})")
       
     })
     pw.close();
     
     /**
      * 
      * 
      * 
     
     CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})
CREATE (Keanu:Person {name:'Keanu Reeves', born:1964})
CREATE (Carrie:Person {name:'Carrie-Anne Moss', born:1967})
CREATE (Laurence:Person {name:'Laurence Fishburne', born:1961})
CREATE (Hugo:Person {name:'Hugo Weaving', born:1960})
CREATE (AndyW:Person {name:'Andy Wachowski', born:1967})
CREATE (LanaW:Person {name:'Lana Wachowski', born:1965})
CREATE (JoelS:Person {name:'Joel Silver', born:1952})
CREATE
  (Keanu)-[:ACTED_IN {roles:['Neo']}]->(TheMatrix),
  (Carrie)-[:ACTED_IN {roles:['Trinity']}]->(TheMatrix),
  (Laurence)-[:ACTED_IN {roles:['Morpheus']}]->(TheMatrix),
  (Hugo)-[:ACTED_IN {roles:['Agent Smith']}]->(TheMatrix),
  (AndyW)-[:DIRECTED]->(TheMatrix),
  (LanaW)-[:DIRECTED]->(TheMatrix),
  (JoelS)-[:PRODUCED]->(TheMatrix)
  ;
     
     
      * 
      */
     
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