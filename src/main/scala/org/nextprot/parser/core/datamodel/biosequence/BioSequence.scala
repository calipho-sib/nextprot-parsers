package org.nextprot.parser.core.datamodel.biosequence

class BioSequenceList(val _sequences: List[BioSequence]) {
  
  
  def toXML = 
      if (_sequences(0)._sequence != "") {
      <bioSequences>
  		{_sequences.map(_.toXML)}
      </bioSequences> }
}

class BioSequence (val _sequence: String, val _sequenceType: String) {
  
  def toXML =
     <com.genebio.nextprot.datamodel.identifier.BioSequence>
          <bioSequence>{_sequence}</bioSequence>
          <bioSequenceType>{_sequenceType}</bioSequenceType>
          <sequenceIdentifier class="com.genebio.nextprot.datamodel.identifier.AntibodyIdentifier" reference="../../.."/>
     </com.genebio.nextprot.datamodel.identifier.BioSequence>
    
}