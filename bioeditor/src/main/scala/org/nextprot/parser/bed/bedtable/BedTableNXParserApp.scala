package org.nextprot.parser.bed.bedtable
import org.nextprot.parser.core.NXParserAppBase

object BedTableNXParserApp extends NXParserAppBase {

  System.setProperty("files.directory", "src/test/resources/bed")
  System.setProperty("files.expression", "data.xml")

  System.setProperty("parser.impl", "org.nextprot.parser.bed.bedtable.BedTableNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.bed.bedtable.BedTableNXReducer")

  initialize

}