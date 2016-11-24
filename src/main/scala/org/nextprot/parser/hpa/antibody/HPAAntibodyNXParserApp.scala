package org.nextprot.parser.hpa
import org.nextprot.parser.core.NXParserAppBase

object HPAAntibodyNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.hpa.antibody.HPAAntibodyNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.hpa.antibody.HPAAntibodyReducer")

  initialize

}