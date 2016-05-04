package org.nextprot.parser.bed.commons.constants

/*
 * Enum based on this Sean's example: http://www.scala-lang.org/old/node/10031?page=1#comment-43299
 */
object BEDTerminology extends Enumeration {
  
  // USED FOR CV TERMS!!!!!!!!!

  protected case class Val(name: String) extends super.Val(nextId, name) {

  }
  
  // Annotation Categories:
  // https://github.com/calipho-sib/nextprot-api/blob/develop/commons/src/main/java/org/nextprot/api/commons/constants/AnnotationCategory.java

  implicit def valueToTerminology(x: Value) = x.asInstanceOf[Val]

  val GoCellularComponentCv = Val("go-cellular-component-cv")
  val GoBiologicalProcessCv = Val("go-biological-process-cv")
  val GoMolecularFunctionCv = Val("go-molecular-function-cv")
  val MammalianPhenotypeCv = Val("mammalian-phenotype-cv")

}
