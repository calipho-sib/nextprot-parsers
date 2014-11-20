package org.nextprot.parser.hpa.utils

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_UNIPROT_MAPPING
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_RULE_FOR_PA_NOT_SUPPORTIVE

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
  
  it should "throws an error when protein array verification is not supportive" in {
   
    val thrown = intercept[NXException] {
    val xml =  <antibody id="HPA123">
    			<proteinArray technology="PA">
    				<verification type="validation">non-supportive</verification>
    			</proteinArray>
              </antibody>;
      HPAUtils.getProteinArray(xml)
    }
    assert(thrown.getNXExceptionType == CASE_NO_RULE_FOR_PA_NOT_SUPPORTIVE)
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