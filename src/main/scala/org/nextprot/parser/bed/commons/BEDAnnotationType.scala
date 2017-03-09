package org.nextprot.parser.bed.commons

import scala.language.implicitConversions

object BEDAnnotationType extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {}
  implicit def valueToRelationsVal(x: Value) = x.asInstanceOf[Val]

  val VP = Val("VP")
  val VE = Val("VE")
 
}
