package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import com.google.gson.GsonBuilder
import org.nextprot.parser.nx.datamodel.NXPhenotypeAnnotation
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import collection.JavaConverters._
import org.nextprot.parser.nx.datamodel.NXPhenotypeAnnotation

class BedGenerateJson extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

   it should "group annotations together by subject and object" in {
    
     val annotations = BEDUtils.getBEDAnnotations(entryElem);
     val vpAnnotations = annotations.filter(a => a.isVP);
     println("Total of annotations: " + vpAnnotations.length);
     
     val vpEvidences = vpAnnotations.flatMap(a => a._evidences);
     println("Total of evidences: " + vpEvidences.length);
     println("Total of evidences not matched: " + vpEvidences.filter(e=> e.getTermAttributeRelation._1 == "not-defined").size);
     
     val res = vpEvidences.filter(e=> e.getTermAttributeRelation._1 != "not-defined").groupBy(e => (e.getRealSubject, e.getTermAttributeRelation(), e.getRealObject));
     	
     val gson = (new GsonBuilder()).setPrettyPrinting.create
     val annots = vpEvidences.map(e => NXPhenotypeAnnotation(e._subject, e.getTermAttributeRelation._1, e.getTermAttributeRelation._2, e._objectTerm.getApiCategory, e._objectTerm.name, e._bioObject));

     val pw = new PrintWriter(new File("/Users/dteixeira/Documents/workspace/workspace-java/nextprot-api/web/src/main/resources/brca1-phenotypes.json" ))
     pw.write(gson.toJson(annots.asJavaCollection));
     pw.close();

  }

}