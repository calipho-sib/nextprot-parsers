package org.nextprot.parser.ensg

import org.nextprot.parser.core.NXParserAppBase

object UniprotENSGNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.ensg.UniprotENSGNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.ensg.NXStringFileReducer")

  initialize
}
