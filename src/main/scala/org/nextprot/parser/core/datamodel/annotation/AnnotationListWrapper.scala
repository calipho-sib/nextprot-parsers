package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.datamodel.TemplateModel

class AnnotationListWrapper (val _datasource: String, val _accession: String, val _rowAnnotations: List[RawAnnotation]) extends TemplateModel{
  override def toXML =
    <com.genebio.nextprot.dataloader.swissprot.AnnotationListWrapper>
      <annotationCategory>GENERAL_ANNOTATION</annotationCategory>
      <ac>{ _accession }</ac>
      <datasource>{ _datasource }</datasource>
      <wrappedBean>{
        if (_rowAnnotations != null && !_rowAnnotations.isEmpty) {
          {
            _rowAnnotations.map(_.toXML)
          }
        }
      }</wrappedBean>
    </com.genebio.nextprot.dataloader.swissprot.AnnotationListWrapper>
}