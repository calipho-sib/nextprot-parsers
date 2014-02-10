package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.constants.NXQuality._

class RawAnnotation(val _datasource: String, val _cvTermAcc: String, val _cvTermCategory: String, val _isPropagableByDefault: Boolean, val _type: String, val _description: String, val _quality: NXQuality, val _assocs: List[AnnotationResourceAssoc]) {
  def toXML =
    <com.genebio.nextprot.dataloader.dto.RawAnnotation>
      <datasource>{ _datasource }</datasource>
      <isPropagableByDefault>{ _isPropagableByDefault }</isPropagableByDefault>{
        if (_cvTermAcc != null) {
          <cvTermAcc>{ _cvTermAcc }</cvTermAcc>
          <cvTermCategory>{ _cvTermCategory }</cvTermCategory>
        }
      }
      <type>{ _type }</type>
      <description>{ scala.xml.PCData(_description.replace("\n", "")) }</description>

      {
        if (_quality != null)  {
          <quality>{ _quality.toString() }</quality>
          }
      }
      
      {
        if (_assocs != null) {
          <resourceAssocs>
            {
              _assocs.map(_.toXML)
            }
          </resourceAssocs>
        }
      }
    </com.genebio.nextprot.dataloader.dto.RawAnnotation>
}