package org.nextprot.parsers.bed.service

import scala.io.Source

import org.apache.jena.query.QueryFactory
import org.apache.jena.sparql.engine.http.QueryEngineHTTP
import org.nextprot.parsers.bed.commons.BEDUtils
import org.nextprot.parsers.bed.commons.Memoize.memoize

object GeneNameServiceCached {

  val getNXAccessionForGeneName = memoize(GeneNameService.getNXAccessionForGeneName _)

}