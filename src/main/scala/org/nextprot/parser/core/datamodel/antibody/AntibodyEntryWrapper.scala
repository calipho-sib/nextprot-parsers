package org.nextprot.parser.core.datamodel.antibody

import org.nextprot.parser.core.datamodel.biosequence.BioSequenceList
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality.NXQuality
import org.nextprot.parser.core.constants.NXQuality
//import org.nextprot.parser.core.datamodel.annotation.AnnotationListWrapper

class AntibodyEntryWrapper(val _dbxref: String, val _version: String, val _bioSequenceList: BioSequenceList, val _propertyList: AntibodyIdentifierPropertyList, val _annots: HPAAntibodyAnnotationListWrapper, val _accession: String) {

  def toXML =
    <com.genebio.nextprot.dataloader.expression.AntibodyEntryWrapper>
      <wrappedBean>
        <identifierType>ANTIBODY</identifierType>
        <dbXref>
          <resourceType>DATABASE</resourceType>
          <accession>{scala.xml.PCData(_dbxref)}</accession>
          <version>{ _version } </version>
          <cvDatabase>
            <cvName>HPA</cvName>
          </cvDatabase>
        </dbXref>
        { _bioSequenceList.toXML }
        { _propertyList.toXML }
      </wrappedBean>
     <uniprotIds>
      <string>{ _accession}</string>
     </uniprotIds>
     { _annots.toXML }
    </com.genebio.nextprot.dataloader.expression.AntibodyEntryWrapper>
}

class AntibodyEntryWrapperList(val _quality: NXQuality, val _antibodyList: List[AntibodyEntryWrapper]) extends TemplateModel {

  override def toXML = <h> { _antibodyList.map(_.toXML) } </h>;

  override def getQuality: NXQuality = _quality;

}