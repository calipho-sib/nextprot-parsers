package org.nextprot.parser.core.constants

import scala.language.implicitConversions

object EvidenceCode extends Enumeration { 

	protected case class Val(val code: String,val name: String) extends super.Val {
		// just as an example to show how add a method to Val extension
		override def toString(): String = code + " - " + name 
	}
	implicit def valueToEvidenceCodeVal(x: Value) = x.asInstanceOf[Val] 

	val Antibodymapping = Val("ECO:0001054","heterologous protein expression evidence") 
	val ImmunoLocalization = Val("ECO:0000087","immunolocalization evidence") 
	val ImmunoHistochemistry = Val("ECO:0001055","immunohistochemistry evidence") 
	val ImmunoCytoChemistry = Val("ECO:0001053","immunocytochemistry evidence") 
	val RnaSeq = Val("ECO:0000295","RNA-seq evidence") 
} 





