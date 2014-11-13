package org.nextprot.parser.core.datamodel.antibody

import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation

class HPAAntibodyAnnotationListWrapper (val _HPAaccession: String, val _rowAnnotations: List[RawAnnotation]){
def toXML =
   <summaryAnnotations>
     <ac>{ _HPAaccession }</ac>
     <preComputedFeatures>false</preComputedFeatures>
     <wrappedBean>{
        if (_rowAnnotations != null && !_rowAnnotations.isEmpty) {
          {
            _rowAnnotations.map(_.toXML)
          }
        }
      }
     </wrappedBean>
   </summaryAnnotations>
}