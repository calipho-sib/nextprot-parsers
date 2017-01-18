package org.nextprot.parser.bed

import java.lang.System.currentTimeMillis

import scala.collection.JavaConversions.seqAsJavaList

import org.nextprot.commons.statements.Statement
import org.nextprot.parser.bed.converter.BedStatementConverter
import java.util.HashSet
import org.nextprot.commons.statements.constants.NextProtSource

object LocalLoaderApp extends App {

  val location = "/Users/dteixeira/Documents/bed/";
  val load = false;

  val statements = scala.collection.mutable.Set[Statement]();

  BedStatementConverter.addProxyDir(location);
  BEDConstants.GENE_LIST.foreach { g =>

    val statementsForGene = BedStatementConverter.convert(g)._1;
    println("Found " + statementsForGene.size + " for gene " + "scn11a");
    statements ++= statementsForGene;
    
  }

  val beforeLoad = currentTimeMillis();

  //statementLoaderService.loadRawStatementsForSource(new HashSet(statements.toList), NextProtSource.BioEditor);

  println("Done in " + (currentTimeMillis() - beforeLoad) + " ms for " + statements.size);

}