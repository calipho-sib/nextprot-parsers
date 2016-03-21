package org.nextprot.parser.peptide.atlas.datamodel

import org.nextprot.parser.core.constants.NXQuality.NXQuality
import org.nextprot.parser.core.datamodel.TemplateModel

class Peptide(val _sequence: String, val id: String, val _dbrefs: List[DbXref], val _features: List[Feature]) extends TemplateModel {
  
  override def toXML =
    <peptide sequence={_sequence} >
    <evidence type="mass spectrometry evidence" assigned_by="PeptideAtlas human phosphoproteome"/>
    <dbReference type="PeptideAtlas" id={id}></dbReference>
    {_dbrefs.map(_.toXML)}
      {
        if(_features != null)_features.map(_.toXML)
      }
    </peptide>

   override def getQuality: NXQuality = org.nextprot.parser.core.constants.NXQuality.GOLD;
   
}