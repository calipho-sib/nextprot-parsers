package org.nextprot.parser.bed.commons.constants
object BEDModifiers extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {
  }
  
  implicit def valueofModifiers(x: Value) = x.asInstanceOf[Val]

  val NOT_CHANGED = Val("not-changed")
  val CHANGED = Val("changed")
  val GAIN = Val("gains")
  val LOSS = Val("loss")
  val INCREASE = Val("increases")
  val DECREASE = Val("decreases")

}
