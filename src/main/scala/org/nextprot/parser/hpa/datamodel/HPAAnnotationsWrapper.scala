package org.nextprot.parser.hpa.datamodel

import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality.NXQuality
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation

sealed trait HPAType;
object SubcellularType extends HPAType;
object ExpressionType extends HPAType;

class ExpHPAAnnotationsWrapper(
  override val _quality: NXQuality,
  override val _ensgAc: String,
  override val _uniprotIds: List[String],
  override val _antibodyIds: List[String],
  override val _integrationLevel: String,
  override val _rowAnnotations: List[RawAnnotation],
  override val _summaryAnnotation: RawAnnotation,
  override val _datasource: String,
  override val _annotationTag : String)
  extends HPAAnnotationsWrapper(_quality, _ensgAc, _uniprotIds, _antibodyIds, 
      _integrationLevel, _rowAnnotations, _summaryAnnotation, ExpressionType, _datasource, _annotationTag) {
}

class SubcellularHPAAnnotationsWrapper(
  override val _quality: NXQuality,
  override val _ensgAc: String,
  override val _uniprotIds: List[String],
  override val _antibodyIds: List[String],
  override val _integrationLevel: String,
  override val _rowAnnotations: List[RawAnnotation],
  override val _datasource: String,
  override val _annotationTag : String)
  extends HPAAnnotationsWrapper(_quality, _ensgAc, _uniprotIds, _antibodyIds, 
      _integrationLevel, _rowAnnotations, null, SubcellularType, _datasource, _annotationTag) {

}

sealed abstract class HPAAnnotationsWrapper(
  val _quality: NXQuality,
  val _ensgAc: String,
  val _uniprotIds: List[String],
  val _antibodyIds: List[String],
  val _integrationLevel: String,
  val _rowAnnotations: List[RawAnnotation],
  val _summaryAnnotation: RawAnnotation,
  val _hpaType: HPAType,
  val _datasource: String,
  val _annotationTag : String) extends TemplateModel {

  override def toXML =
    <com.genebio.nextprot.dataloader.expression.HPAAnnotationsWrapper>
      <annotationTag>{ _annotationTag }</annotationTag>
      <ensgAccessionCode>{ _ensgAc }</ensgAccessionCode>
      <uniprotIds>
        {
          _uniprotIds.map(id => { <string>{ id }</string> })
        }
      </uniprotIds>
      <antibodyIds>
        {
          _antibodyIds.map(id => { <string>{ id }</string> })
        }
      </antibodyIds>
      <integrationLevel>{ _integrationLevel }</integrationLevel>
      <quality>{ _quality.toString() }</quality>
      {
        _hpaType match {
          case ExpressionType => {
            <summaryAnnotations>
        	  <datasource>{ _datasource }</datasource>
              <wrappedBean>
                { _summaryAnnotation.toXML }
              </wrappedBean>
              <preComputedFeatures>false</preComputedFeatures>
            </summaryAnnotations>
          }
          case SubcellularType => {}
        }
      }
      {
        _hpaType match {
          case SubcellularType => {
            <subcellAnnotations>
        	  <datasource>{ _datasource }</datasource>
              { wrappedBeanXML }
            </subcellAnnotations>
          }
          case ExpressionType => {
            <expressionAnnotations>
        	  <datasource>{ _datasource }</datasource>
        	  { wrappedBeanXML }
              <preComputedFeatures>false</preComputedFeatures>
            </expressionAnnotations>
          }
        }
      }
    </com.genebio.nextprot.dataloader.expression.HPAAnnotationsWrapper>

  private def wrappedBeanXML = <wrappedBean>
                                 {
                                   if (_rowAnnotations != null && !_rowAnnotations.isEmpty) {
                                     { _rowAnnotations.map(_.toXML) }
                                   }
                                 }
                               </wrappedBean>

  override def getQuality: NXQuality = _quality;

}

class ExpHPARNAAnnotationsWrapper(
  val _quality: NXQuality,
  val _ensgAc: String,
  val _uniprotIds: List[String],
  val _summaryAnnotations: List[RawAnnotation],
  val _rowAnnotations: List[RawAnnotation])
  extends TemplateModel
  {
  def toXML =

    <com.genebio.nextprot.dataloader.expression.HPAAnnotationsWrapper>
      <annotationTag>RNASeq</annotationTag>
      <ensgAccessionCode>{ _ensgAc }</ensgAccessionCode>
      <uniprotIds>
        {
          _uniprotIds.map(id => { <string>{ id }</string> })
        }
      </uniprotIds>
      <quality>{ _quality.toString() }</quality>
      <expressionAnnotations>
        <datasource>Human protein atlas RNA-seq</datasource>
        { exprWrappedBeanXML }
        <preComputedFeatures>false</preComputedFeatures>
      </expressionAnnotations>
      <summaryAnnotations>
        <datasource>Human protein atlas RNA-seq</datasource>
        <preComputedFeatures>false</preComputedFeatures>
        { summaryExprWrappedBeanXML }
      </summaryAnnotations>

    </com.genebio.nextprot.dataloader.expression.HPAAnnotationsWrapper>

  private def exprWrappedBeanXML = <wrappedBean>
                                 {
                                   if (_rowAnnotations != null && _rowAnnotations.nonEmpty) {
                                     { _rowAnnotations.map(_.toXML) }
                                   }
                                 }
                               </wrappedBean>

    private def summaryExprWrappedBeanXML = <wrappedBean>
                                {
                                if (_summaryAnnotations != null && _summaryAnnotations.nonEmpty) {
                                  { _summaryAnnotations.map(_.toXML) }
                                }
                                }
                              </wrappedBean>

    override def getQuality: NXQuality = _quality;
}

