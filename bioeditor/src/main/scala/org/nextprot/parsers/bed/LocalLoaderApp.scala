package org.nextprot.parsers.bed

import java.lang.System.currentTimeMillis

import scala.collection.JavaConversions.seqAsJavaList

import org.nextprot.commons.statements.Statement
import org.nextprot.commons.statements.service.impl.OracleStatementLoaderServiceImpl
import org.nextprot.parsers.bed.converter.BedServiceStatementConverter
import java.util.HashSet
import org.nextprot.commons.statements.constants.NextProtSource

object LocalLoaderApp extends App {

  val location = "/Users/dteixeira/Documents/bed/";
  val load = false;

  val statements = scala.collection.mutable.Set[Statement]();

  BedServiceStatementConverter.addProxyDir(location);
  BEDConstants.GENE_LIST.foreach { g =>

    val statementsForGene = BedServiceStatementConverter.convert(g);
    println("Found " + statementsForGene.size + " for gene " + "scn11a");
    statements ++= statementsForGene;
    
  }

  val statementLoaderService = new OracleStatementLoaderServiceImpl();
  val beforeLoad = currentTimeMillis();

  statementLoaderService.loadRawStatementsForSource(new HashSet(statements.toList), NextProtSource.BioEditor);

  println("Done in " + (currentTimeMillis() - beforeLoad) + " ms for " + statements.size);

}