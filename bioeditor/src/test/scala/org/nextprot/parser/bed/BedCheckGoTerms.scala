package org.nextprot.parser.bed

import java.io.File
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parser.bed.commons.constants.BEDEffects._
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.parser.bed.commons.constants.NXCategory

class BEDCheckGoTerms extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))
  val annotations = BEDAnnotationService.getBEDVPAnnotations(entryElem);
  val vpEvidences = annotations.flatMap(a => a._evidences);

  it should "check that all localisation are from go-cellular-component-cv " in {

    vpEvidences.filter(e => e.getRelationInfo.getEffect.equals(EFFECT_ON_SUBCELLULAR_LOCALIZATION))
      .foreach(e => {
        if (!e.getNXCategory.equals(NXCategory.GoCellularComponent)) {
          fail(e.toString());
        }
      })
  }

  it should "check that all localisation are from go-biological-process or go-mollecular-function " in {

    vpEvidences.filter(e => e.getRelationInfo.getEffect.equals(EFFECT_ON_PROTEIN_ACTIVITY))
      .foreach(e => {
        if (!(e.getNXCategory.equals(NXCategory.GoBiologicalProcess) ||
          e.getNXCategory.equals(NXCategory.GoMolecularFunction))) {
          fail(e.toString());
        }
      })
  }

}