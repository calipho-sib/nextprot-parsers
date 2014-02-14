package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality.NXQuality
import org.nextprot.parser.core.constants.NXQuality

class ExpressionHPAAnnotationsWrapper(
    val _quality: NXQuality, val _ensgAc: String, 
    val _uniprotIds:List[String], val _antibodyIds:List[String], val _integrationLevel:String,
    val _summaryAnnotation: RawAnnotation, val _exprAnnotations:List[RawAnnotation]) extends TemplateModel {
	
    override def toXML =
    
		  <com.genebio.nextprot.dataloader.expression.HPAAnnotationsWrapper>
		  	<wrappedBean>
		  		<identifierType>GENE</identifierType>
		  	</wrappedBean>
		  	<ensgAccessionCode>{ _ensgAc }</ensgAccessionCode>
		  	<uniprotIds>
  				{
  					_uniprotIds.map(id => {<string>{id}</string>} )
  				}
		  	</uniprotIds>
		  	<antibodyIds>
  				{
  					_antibodyIds.map(id => {<string>{id}</string>} )
  				}
		  	</antibodyIds>
		  	<integrationLevel>{ _integrationLevel }</integrationLevel>
		  	<quality>{ _quality.toString() }</quality>
		  	<summaryAnnotations>
		  		<wrappedBean>
		  			{ _summaryAnnotation.toXML }
		  		</wrappedBean>
		  		<preComputedFeatures>false</preComputedFeatures>
		  	</summaryAnnotations>
		  	<expressionAnnotations>
		  	    <wrappedBean>
		  		{ 
		  		  if (_exprAnnotations != null && !_exprAnnotations.isEmpty) {
		  		    { _exprAnnotations.map(_.toXML) }
		  		  }
		  		}
        		</wrappedBean>
		  		<preComputedFeatures>false</preComputedFeatures>
		  	</expressionAnnotations>
		  </com.genebio.nextprot.dataloader.expression.HPAAnnotationsWrapper>
    
  override def getQuality: NXQuality = _quality;
}