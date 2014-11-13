package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.constants.NXQuality.NXQuality

class AnnotationResourceAssoc(
    val _resourceClass: String, val _resourceType: String, val _accession: String, 
    val _cvDatabaseName: String, val _qualifierType: String, val _isNegative: Boolean, 
    val _type: String, val _quality: NXQuality, val _dataSource: String,
    val _props: List[AnnotationResourceAssocProperty], val _expContext: ExperimentalContextSynonym) {
  def toXML =
    <com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc>
      <resource class={ _resourceClass }>
        <resourceType>{ _resourceType }</resourceType>
        <accession>{ _accession }</accession>
        <cvDatabase>
          <cvName>{ _cvDatabaseName }</cvName>
        </cvDatabase>
      </resource>
      <qualifierType>{ _qualifierType }</qualifierType>
      {
        if (_quality != null) {<qualityQualifier>{ _quality.toString() }</qualityQualifier>}
      }
      {
        if (_props != null && ! _props.isEmpty) {
          <annotationResourceAssocProperties>{ _props.map(_.toXML) } </annotationResourceAssocProperties>
        }
      }
      {
        if ( _expContext != null) {
      	  <experimentalContext>
            <contextSynonyms>
        		{ _expContext.toXML }
            </contextSynonyms>
          </experimentalContext>          
        }
      }
      <isNegativeEvidence>{ _isNegative }</isNegativeEvidence>
      <type>{ _type }</type>
      {
        if ( _dataSource != null) {
      <assignedBy>
        <cvName>{ _dataSource }</cvName>
      </assignedBy>
        }
      }
    </com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc>

}