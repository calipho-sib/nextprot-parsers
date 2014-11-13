package org.nextprot.parser.hpa.subcell
import org.nextprot.parser.core.NXParserAppBase

object HPASubcellNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.hpa.subcell.HPASubcellNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.core.impl.NXSimpleFileReducer")

  initialize

}