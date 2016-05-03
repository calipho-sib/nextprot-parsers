package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parser.bed.service.BEDAnnotationService

class BedPublicationListTest extends FlatSpec with Matchers {

    val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

   it should "get a list of all publications" in {
     
      val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
      val publications = annotations.flatMap(a => a._evidences).flatMap(e => e.getReferences).toSeq;
      println(publications.size)
      publications.groupBy(p => p._1).foreach(g => println (g._1 + " : " + g._2.size))
      
      println(publications.take(10));
      //Output 
      //PubMed : 6095
      //Cosmic : 70
      //List((PubMed,20085797), (PubMed,10866324), (PubMed,10866324), (PubMed,10866324), (PubMed,14990569), (PubMed,17334399), (PubMed,10724175), (PubMed,10403822), (PubMed,15133502), (PubMed,15133502))

  }

}