package org.nextprot.parser.hpa.subcell

import org.scalatest._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.HPAQuality

class ComplementarySpecTest extends HPASubcellTestBase {

  it should " calculate the correct score for the antibody" in {

    val antibody1 = <antibody>
                      <subcellularLocation>
                        <subAssay>
                          <data>
                            <level type="intensity">strong</level>
                          </data><data><level type="intensity">weak</level></data>
                        </subAssay>
                      </subcellularLocation>
                    </antibody>
    val score = HPAQuality.getScoreForAntibody(antibody1, slSection);
    assert(score == 6)
  }

}
