package org.nextprot.parser.nx.datamodel

import org.nextprot.parser.bed.commons.constants.BEDEffects
import org.nextprot.parser.bed.commons.constants.BEDImpact

case class NXPhenotypeAnnotation(subject: String, effect: BEDEffects.Value, impact: String, category: String, cvName: String, bioObject : String ) {

}