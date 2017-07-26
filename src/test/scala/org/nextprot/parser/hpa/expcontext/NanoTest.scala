package org.nextprot.parser.hpa.expcontext

import org.scalatest._
import org.nextprot.parser.core.NXParser
import java.io.File
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import scala.util.matching.Regex
import scala.xml.PrettyPrinter
import java.io.FileWriter

class NanoTest extends HPAExpcontextTestBase {

  ignore should "this test succeed" in {

    myloop(tutu)
    assert(true)
    println("fin")
  }
  
  def myloop(somefun:String => String) = {
    List("a","b","c").foreach(x => println(somefun(x)))
    print("end")
  }
  
  def toto(x:String): String = {
    return "toto:"+x
  }
  
  def tutu(x:String): String = {
    return "tutu:" + x
  }
  
  
}
