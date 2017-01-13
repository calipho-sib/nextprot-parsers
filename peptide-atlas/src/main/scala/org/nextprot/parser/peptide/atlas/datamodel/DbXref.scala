package org.nextprot.parser.peptide.atlas.datamodel

case class DbXref(val _mData: String, val _quality : String, val _pmid : String) {
  // This is a case class in order to implement the 'distinct' method for list of instances
  // dbrefs at the peptide level are created with _quality=null
  // _pmid may be a MDATA id instead in one case
  
  def toXML = 
    <dbReference type={if(_pmid==null) "" else if(_pmid.startsWith("MDATA")) "neXtProtSubmission" else "PubMed"} id={if(_pmid != null) {_pmid}  else null} quality={if(_quality != null) {_quality} else null}>
			<document id={_mData}> </document>
		</dbReference>
      
  def toMinimalString = {
    _mData + " " + _pmid + " " + _quality
  }     

}

