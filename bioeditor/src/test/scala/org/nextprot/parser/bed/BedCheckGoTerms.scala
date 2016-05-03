package org.nextprot.parser.bed

import java.io.File
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parser.bed.commons.constants.BEDEffects._
import org.nextprot.parser.bed.service.BEDAnnotationService

class BEDCheckGoTerms extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))
  val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
  val vpAnnotations = annotations.filter(a => a.isVP);
  val vpEvidences = vpAnnotations.flatMap(a => a._evidences);

  it should "check that all localisation are from go-cellular-component-cv " in {

    vpEvidences.filter(e => e.getTermAttributeRelation.effect.equals(EFFECT_ON_SUBCELLULAR_LOCALIZATION))
      .foreach(e => {
        if (!e._objectTerm.terminology.equals("go-cellular-component-cv")) {
          fail(e.toString());
        }
      })
  }

  it should "check that all localisation are from go-biological-process or go-mollecular-function " in {

    vpEvidences.filter(e => e.getTermAttributeRelation.effect.equals(EFFECT_ON_PROTEIN_ACTIVITY))
      .foreach(e => {
        if (!(e._objectTerm.terminology.equals("go-biological-process-cv") ||
          e._objectTerm.terminology.equals("go-molecular-function-cv"))) {
          fail(e.toString());
        }
      })
  }

}