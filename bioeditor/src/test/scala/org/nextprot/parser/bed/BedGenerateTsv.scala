package org.nextprot.parser.bed

import org.nextprot.parser.bed.commons.constants.BEDRelationsString
import org.nextprot.parser.bed.utils.BEDUtils
import org.nextprot.parser.bed.utils.JSAnnotationObject
import org.nextprot.parser.bed.utils.JSBioObject
import org.nextprot.parser.bed.utils.JSDescriptionObject
import org.nextprot.parser.bed.utils.JSEffectObject
import org.nextprot.parser.bed.utils.JSImpactObject
import org.nextprot.parser.bed.utils.JSLinkObject
import org.nextprot.parser.bed.utils.JSNode
import org.nextprot.parser.bed.utils.JSVariantObject
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BEDGenerateTsv extends FlatSpec with Matchers {

  it should "generate the combination of all possible (even not real) combinations" in {

    println(List("Relation", "IsNegative", "Effect?", "Impact?").mkString("\t"));

    List(false, true).foreach(negative => { //For positive and negative evidences

      BEDRelationsString.values.foreach(r => {

        val (effect, impact, description) = try {
          val ri = BEDUtils.getRelationInformation(r.name, negative);
          (ri.getEffect, ri.getImpactString, ri.getDescription);

        } catch {
          case e: Exception => {
            ("", "", e.getMessage());
          }
        }

        println(List(r.name, negative, effect, impact).mkString("\t"));
      })
    })

    // new PrintWriter("doc/bed-relation-mapping.tsv") { write(newContent); close }

  }

}