package org.nextprot.parser.peptide.atlas

import org.nextprot.parser.core.NXParserAppBase

object PeptideAtlasPhosphoNXParserApp extends NXParserAppBase {
  
  System.setProperty("parser.impl", "org.nextprot.parser.peptide.atlas.PeptideAtlasPhosphoNXParser")
  System.setProperty("reducer.impl", "org.nextprot.parser.peptide.atlas.NXPeptidePrintReducer")

  initialize
}
