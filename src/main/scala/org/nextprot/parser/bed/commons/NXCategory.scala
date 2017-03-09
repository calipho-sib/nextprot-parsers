package org.nextprot.parser.bed.commons

import scala.language.implicitConversions

/*
 * Enum based on this Sean's example: http://www.scala-lang.org/old/node/10031?page=1#comment-43299
 */
object NXCategory extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {

  }

  implicit def valueToCategry(x: Value) = x.asInstanceOf[Val]

  val GoCellularComponent = Val("go-cellular-component")
  val GoBiologicalProcess = Val("go-biological-process")
  val GoMolecularFunction = Val("go-molecular-function")
  val BinaryInteraction = Val("binary-interaction")
  val ProteinProperty = Val("protein-property")
  val SmallMoleculeInteraction = Val("small-molecule-interaction")
  val GenericPtm = Val("generic-ptm")
  val MammalianPhenotype = Val("mammalian-phenotype")
  val ElectrophysiologicalParameter = Val("electrophysiological-parameter")

}
