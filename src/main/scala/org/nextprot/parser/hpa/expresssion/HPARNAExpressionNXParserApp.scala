package org.nextprot.parser.hpa.expression

import org.nextprot.parser.core.NXParserAppBase

object HPARNAExpressionNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.hpa.expression.HPARNAExpressionNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.core.impl.NXSimpleFileReducer")

  initialize

}
