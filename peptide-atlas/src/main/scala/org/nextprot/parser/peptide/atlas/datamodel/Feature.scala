package org.nextprot.parser.peptide.atlas.datamodel

import org.nextprot.parser.core.constants.NXQuality.NXQuality

class Feature(val _position: Integer, val _description: String, val _dbrefs: List[DbXref]) {
  
  def toXML = 
    <feature type="modified residue" description={_description} quality={getTopQuality}>
			<location>
				<position position={_position.toString()} />
			</location> 
			<evidence type="mass spectrometry evidence" assigned_by="PeptideAtlas human phosphoproteome"/>
    {_dbrefs.map(_.toXML)}
		</feature>
  
     def getTopQuality: String = {
    _dbrefs.foreach { dbref => if(dbref._quality == "GOLD") return("GOLD")} 
    "SILVER"
    }
}

