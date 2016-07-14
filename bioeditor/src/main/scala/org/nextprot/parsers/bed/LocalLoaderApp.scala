package org.nextprot.parsers.bed

import java.lang.System.currentTimeMillis

import scala.collection.JavaConversions.seqAsJavaList

import org.nextprot.commons.statements.RawStatement
import org.nextprot.commons.statements.service.impl.OracleStatementLoaderServiceImpl
import org.nextprot.parsers.bed.converter.BedServiceStatementConverter
import org.nextprot.commons.statements.StatementField

object LocalLoaderApp extends App {

  val location = "/Users/dteixeira/Documents/bed/";
  val load = true;

  val statements = scala.collection.mutable.Set[RawStatement]();

  BedServiceStatementConverter.setProxyDir(location);
  BEDConstants.GENE_LIST.foreach { g =>

    val statementsForGene = BedServiceStatementConverter.convert(g);
    println("Found " + statementsForGene.size + " for gene " + "scn11a");
    statements ++= statementsForGene;
    
  }

  val statementLoaderService = new OracleStatementLoaderServiceImpl();
  statementLoaderService.deleteAll();
  val beforeLoad = currentTimeMillis();

  statementLoaderService.load(statements.toList);

  println("Done in " + (currentTimeMillis() - beforeLoad) + " ms for " + statements.size);

}