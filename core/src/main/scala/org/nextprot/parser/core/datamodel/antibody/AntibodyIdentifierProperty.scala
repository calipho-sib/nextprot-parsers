package org.nextprot.parser.core.datamodel.antibody

class AntibodyIdentifierProperty(val _propertyname: String, val _propertyvalue: String) {

  def toXML =
    <com.genebio.nextprot.datamodel.identifier.IdentifierProperty>
      <cvPropertyName>
        <cvName>{ _propertyname }</cvName>
      </cvPropertyName>
      <propertyValue>{ scala.xml.PCData(_propertyvalue) }</propertyValue>
      <sequenceIdentifier class="com.genebio.nextprot.datamodel.identifier.AntibodyIdentifier" reference="../../.."/>
    </com.genebio.nextprot.datamodel.identifier.IdentifierProperty>

}

class AntibodyIdentifierPropertyList(val _antibodyIdentifierPropertyList: List[AntibodyIdentifierProperty]) {
  def toXML = <identifierProperties>
                {
                  _antibodyIdentifierPropertyList.map(_.toXML)
                }
              </identifierProperties>;
}
