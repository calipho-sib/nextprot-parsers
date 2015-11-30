package org.nextprot.parser.peptide.atlas.datamodel

import org.nextprot.parser.core.constants.NXQuality.NXQuality
import org.nextprot.parser.core.datamodel.TemplateModel

class Peptide(val _quality: NXQuality, val _features: List[Feature]) extends TemplateModel {
  override def toXML =
    <peptide>
      <dbReference></dbReference>
      {
        _features.map(_.toXML)
      }
    </peptide>

  override def getQuality: NXQuality = _quality;
}