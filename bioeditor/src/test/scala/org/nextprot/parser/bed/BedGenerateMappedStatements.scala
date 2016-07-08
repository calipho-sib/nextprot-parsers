package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import scala.xml.NodeSeq
import org.nextprot.commons.statements.RawStatement
import org.nextprot.parsers.bed.commons.BEDImpact.valueofModifiers
import org.nextprot.parsers.bed.commons.NXCategory.valueToCategry
import org.nextprot.parsers.bed.model.BEDEvidence
import org.nextprot.parsers.bed.service.BEDAnnotationService
import org.nextprot.parsers.bed.service.BEDVariantService
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.commons.statements.StatementBuilder
import org.nextprot.commons.statements.StatementField._
import org.nextprot.commons.statements._

class BedGenerateMappedStatements extends FlatSpec with Matchers {

  // cp /Volumes/common/Calipho/caviar/xml/*.xml ~/Documents/bed/
  // cp /Volumes/common/Calipho/navmutpredict/xml/*.xml ~/Documents/bed/
  
  val location = "/Users/dteixeira/Documents/caviar/";
  val load = true;

  val genes = List("apc", "brca1", "brca2", "brip1", "epcam", "idh1", "mlh1", "mlh3",
    "msh2", "msh6", "mutyh", "pms2", "palb2", "scn1a", "scn2a", "scn3a",
    "scn4a", "scn5a", "scn8a", "scn9a", "scn10a", "scn11a");

  it should "do something" in {

  }
}