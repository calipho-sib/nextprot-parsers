package org.nextprot.parsers.bed.commons
object BEDImpact extends Enumeration {

  protected case class Val(name: String, accession: String) extends super.Val(nextId, name) {
  }
  
  implicit def valueofModifiers(x: Value) = x.asInstanceOf[Val]

  val NO_IMPACT = Val("no impact", "ME:0000003")
  val IMPACT = Val("impact", "ME:0000002")
  val INCREASE = Val("increase", "ME:0000005")
  val DECREASE = Val("decrease", "ME:0000004")
  val GAIN = Val("gain", "ME:0000006")
    
}
