package org.nextprot.parser.peptide.atlas

import org.nextprot.parser.core.NXParserAppBase

object UniprotENSGNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.peptide.atlas.PeptideAtlasPhosphoNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.peptide.atlas.NXStringFileReducer")

  initialize
}
