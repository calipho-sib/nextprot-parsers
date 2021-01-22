package org.nextprot.parser.core.datamodel.antibody

import org.nextprot.parser.core.datamodel.biosequence.BioSequenceList

case class AntibodyEntryWrapperList(val antibodyList: List[AntibodyEntryWrapper])

class AntibodyEntryWrapper(val _quality: String, val _dbxref: String, val _version: String,
                           val _bioSequenceList: BioSequenceList, val _propertyList: AntibodyIdentifierPropertyList,
                           val _annots: HPAAntibodyAnnotationListWrapper, val _uniprotIds: List[String],
                           val _ensgAc: String) {

  def toXML =
    <com.genebio.nextprot.dataloader.expression.AntibodyEntryWrapper>
      <wrappedBean>
        <qualityQualifier>{_quality}</qualityQualifier>
        <identifierType>ANTIBODY</identifierType>
        <dbXref>
          <resourceType>DATABASE</resourceType>
          <accession>{scala.xml.PCData(_dbxref)}</accession>
          <version>{ _version }</version>
          <cvDatabase>
            <cvName>HPA</cvName>
          </cvDatabase>
        </dbXref>
        { _bioSequenceList.toXML }
        { _propertyList.toXML }
      </wrappedBean>
      <uniprotIds>
        {
        _uniprotIds.map(id => {<string>{id}</string>} )
        }
      </uniprotIds>
      <ensgAccessionCode>{ _ensgAc }</ensgAccessionCode>
      {
      if (_annots != null) {
        { _annots.toXML }
      }
      }
    </com.genebio.nextprot.dataloader.expression.AntibodyEntryWrapper>
}
