package org.nextprot.parsers.bed.commons
object BEDImpact extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {
  }
  
  implicit def valueofModifiers(x: Value) = x.asInstanceOf[Val]

  val NOT_CHANGED = Val("no-impact")
  val CHANGED = Val("impact")
  val INCREASE = Val("increase")
  val DECREASE = Val("decrease")
  val GAIN = Val("gain")
    
}
