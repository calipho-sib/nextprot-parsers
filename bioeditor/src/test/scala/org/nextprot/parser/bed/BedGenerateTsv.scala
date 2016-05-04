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
import java.io.PrintWriter
import java.io.File

class BEDGenerateTsv extends FlatSpec with Matchers {

  it should "generate the combination of all possible (even not real) combinations" in {

	 val pw = new PrintWriter(new File("relation-mapping.tsv"));

    println(List("BioEditor relation", "BioEditor IsNegative", "Annotation Category", "Modifier?", "Terminology", "Effect (vario?) ambiguous?", "description").mkString("\t"));

    List(false, true).foreach(negative => { //For positive and negative evidences

      BEDRelationsString.values.foreach(r => {

        val (category, impact, terminology, effect,  description) = try {
          val ri = BEDUtils.getRelationInformation(r.name, negative);
          (ri.getCategory.mkString(" or "), ri.getImpactString, ri.getTerminology.mkString(" or "), ri.getEffect, ri.getDescription);

        } catch {
          case e: Exception => {
            ("", "", "", "", e.getMessage());
          }
        }

        val line = List(r.name, negative, category, impact, terminology, effect, description).mkString("\t");
        println(line);
        pw.write(line + "\n");

      })
    })
    
    pw.close();

  }

}