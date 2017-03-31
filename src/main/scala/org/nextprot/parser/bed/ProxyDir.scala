package org.nextprot.parser.bed

import java.lang.System.currentTimeMillis

import scala.collection.JavaConversions.seqAsJavaList

import org.nextprot.commons.statements.Statement
import org.nextprot.parser.bed.converter.BedStatementConverter
import java.util.HashSet
import org.nextprot.commons.statements.constants.NextProtSource
import org.nextprot.commons.statements.StatementField
import java.io.File

case class ProxyDir(directory: File, database: String, release: String)