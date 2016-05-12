package org.nextprot.parser.bed.commons.constants
object BEDModifiers extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {
  }
  
  implicit def valueofModifiers(x: Value) = x.asInstanceOf[Val]

  val NOT_CHANGED = Val("no-change")
  val CHANGED = Val("change")
  val GAIN = Val("gain")
  val LOSS = Val("loss")
  val INCREASE = Val("increase")
  val DECREASE = Val("decrease")

}
