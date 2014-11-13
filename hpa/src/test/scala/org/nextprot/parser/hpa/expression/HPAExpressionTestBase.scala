package org.nextprot.parser.hpa.expression

import java.io.File
import org.scalatest._
import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.NXParserApp
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.subcell.rules.APEQualityRule
import org.nextprot.parser.hpa.subcell.rules.AntibodyValidationRule
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue._
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue._
import org.nextprot.parser.core.datamodel.TemplateModel
import scala.xml.PrettyPrinter
import org.nextprot.parser.hpa.expcontext.HPAExpcontextConfig
import java.io.FileWriter

abstract class HPAExpressionTestBase extends FlatSpec with Matchers {

  val teSection = "tissueExpression"
  System.setProperty("hpa.tissue.mapping.file", "src/test/resources/NextProt_tissues.from-db.txt")

  val defaultDirectory = "src/test/resources/hpa/20140121";
  //val defaultDirectory = "/Users/pmichel/data/hpa/20140121";

  def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

  def getFilesForParsing(): List[File] = {
    getFilesForParsing(defaultDirectory)
  }
  
  def getFilesForParsing(someDirectory: String): List[File] = {
    val regularExpression = "^ENSG.*.xml$"
    println("Looking for files like: " + regularExpression + " in directory: " + someDirectory + " ...");
    return filesAt(new File(someDirectory)).filter(f => regularExpression.r.findFirstIn(f.getName).isDefined).toList;
  }


}

