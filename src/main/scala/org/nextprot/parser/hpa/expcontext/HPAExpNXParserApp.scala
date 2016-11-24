package org.nextprot.parser.hpa.expcontext

import org.nextprot.parser.core.NXParserAppBase

object HPAExpcontextNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.hpa.expcontext.HPAExpcontextNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.hpa.expcontext.HPAExpContextReducer")

  initialize

}
