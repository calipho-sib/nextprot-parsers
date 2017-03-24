package org.nextprot.parser.bed

import java.lang.System.currentTimeMillis

import scala.collection.JavaConversions.seqAsJavaList

import org.nextprot.commons.statements.Statement
import org.nextprot.parser.bed.converter.BedStatementConverter
import java.util.HashSet
import org.nextprot.commons.statements.constants.NextProtSource
import org.nextprot.commons.statements.StatementField

/**
 * This app is used to test the parsing of the statements and errors.
 * Web statements should be use in production (on kant) instead
 */
object ParseLocallyApp extends App {

  val location = "/Users/dteixeira/Documents/nxflat-proxy/";
  val load = false;

  val statements = scala.collection.mutable.Set[Statement]();

  val beforeParsing = currentTimeMillis();

  BedStatementConverter.addProxyDir(location);
  BEDConstants.GENE_LIST./*filter { g => g.equals("brca1") }.*/foreach { g =>

    val date = "2017-03-24";
    
    val statementsForGene = BedStatementConverter.convert("bioeditor", date, g)._1;
    println("Found " + statementsForGene.size + " for gene " + g);
    statements ++= statementsForGene;
    
  }


  //statementLoaderService.loadRawStatementsForSource(new HashSet(statements.toList), NextProtSource.BioEditor);

  println("Parsed in " + (currentTimeMillis() - beforeParsing) + " ms for " + statements.size + " statements");

}