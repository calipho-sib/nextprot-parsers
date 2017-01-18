package org.nextprot.parser.bed

import java.lang.System.currentTimeMillis

import scala.collection.JavaConversions.seqAsJavaList

import org.nextprot.commons.statements.Statement
import org.nextprot.parser.bed.converter.BedStatementConverter
import java.util.HashSet
import org.nextprot.commons.statements.constants.NextProtSource

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
  BEDConstants.GENE_LIST.foreach { g =>

    //val date = "2016-08-22";
    val date = "2017-01-13";
    
    val statementsForGene = BedStatementConverter.convert("bioeditor", date, g)._1;
    println("Found " + statementsForGene.size + " for gene " + "scn11a");
    statements ++= statementsForGene;
    
  }


  //statementLoaderService.loadRawStatementsForSource(new HashSet(statements.toList), NextProtSource.BioEditor);

  println("Parsed in " + (currentTimeMillis() - beforeParsing) + " ms for " + statements.size + " statements");

}