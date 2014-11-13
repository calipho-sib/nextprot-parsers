package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.constants.NXQuality.NXQuality

class AnnotationResourceAssocProperty(val _name: String, val _value: String) {
  def toXML =
		  <com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssocProperty>
		  	<propertyName>{ _name }</propertyName>
		  	<propertyValue>{ _value }</propertyValue>
		  </com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssocProperty>
}