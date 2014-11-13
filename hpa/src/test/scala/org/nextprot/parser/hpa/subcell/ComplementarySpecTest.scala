package org.nextprot.parser.hpa.subcell

import org.scalatest._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue._
import org.nextprot.parser.hpa.HPAQuality

class ComplementarySpecTest extends HPASubcellTestBase {

  // HPA_PARS_SPEC_C3
  "When the reliabiliy is not available for APE experiment, we " should " select the correct antibody given a specific rule" in {

    val antibody1 = <antibody><subcellularLocation><subAssay><data><level type="intensity">weak</level></data><data><level type="intensity">weak</level></data></subAssay></subcellularLocation></antibody>
    val antibody2 = <antibody><subcellularLocation><subAssay><data><level type="intensity">strong</level></data><data><level type="intensity">weak</level></data></subAssay></subcellularLocation></antibody>
    val antibody3 = <antibody><subcellularLocation><subAssay><data><level type="intensity">negative</level></data><data><level type="intensity">weak</level></data></subAssay></subcellularLocation></antibody>

    val entry = <entry>
                  <subcellularLocation technology="IF" type="APE">
                    <verification type="reliability">n/a</verification>
                  </subcellularLocation>
                  { antibody1 }
                  { antibody2 }
                  { antibody3 }
                </entry>

    val antibody = HPAQuality.selectAPEAntibodyForNotAvailablereliability(entry, slSection, false);
    assert(antibody == antibody2)
  }

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
