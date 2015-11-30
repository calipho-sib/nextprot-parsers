package org.nextprot.parser.peptide.atlas.datamodel

class Feature(val _position: Integer) {
  
  def toXML = 
    <feature>
      <position>{_position}</position>
    </feature>
  
}