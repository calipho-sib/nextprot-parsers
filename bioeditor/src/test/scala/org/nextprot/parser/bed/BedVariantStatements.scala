package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.commons.statements.RawStatement
import scala.collection.mutable.SortedSet
import org.nextprot.parser.bed.service.BEDVariantService

class BEDGenerateVariant extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

  it should "group annotations together by subject and object" in {

    val variants = BEDVariantService.getBEDVariants(entryElem);

    variants.foreach(variant => {
      println(variant);
    })
  }

}