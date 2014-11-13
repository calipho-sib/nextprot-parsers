package org.nextprot.parser.hpa.utils

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_UNIPROT_MAPPING
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL

class HPAUtilsTest extends FlatSpec with Matchers {

  it should "return an nxexception for not finding any antibody containing subcellular location" in {

    val xml = <entry>
                <antibody>
                </antibody>
              </entry>;

    val thrown = intercept[NXException] {
      HPAUtils.getAntibodyIdListForSubcellular(xml);
    }

    assert(thrown.getNXExceptionType == CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL)

  }

  it should "return the antibody" in {

    val xml = <entry>
                <antibody id="HPA123">
                  <subcellularLocation>
                  </subcellularLocation>
                </antibody>
                <antibody id="HPA456">
                  <subcellularLocation>
                  </subcellularLocation>
                </antibody>
              </entry>;

    val antibodyIds = HPAUtils.getAntibodyIdListForSubcellular(xml);
    assert(antibodyIds.contains("HPA123"));
    assert(antibodyIds.contains("HPA456"));
    assert(!antibodyIds.contains("whatever"));

  }

}