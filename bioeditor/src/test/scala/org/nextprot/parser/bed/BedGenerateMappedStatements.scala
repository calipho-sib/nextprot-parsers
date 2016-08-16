package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import scala.xml.NodeSeq
import org.nextprot.commons.statements.Statement
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
import org.nextprot.parsers.bed.converter.BedServiceStatementConverter
import scala.collection.JavaConverters._

class BedGenerateMappedStatements extends FlatSpec with Matchers {

  it should "return more than 1000 statements for brca1" in {

    val statements = BedServiceStatementConverter.convert("brca1");
    assert(statements.length > 1000)
  }
}