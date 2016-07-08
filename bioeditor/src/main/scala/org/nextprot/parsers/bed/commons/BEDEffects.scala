package org.nextprot.parsers.bed.commons

object BEDEffects extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {
  }
  
  implicit def valueofEffects(x: Value) = x.asInstanceOf[Val]

  val EFFECT_ON_SUBCELLULAR_LOCALIZATION = Val("effect on protein subcellular localization (VariO_0033) or more detailed")
  val EFFECT_ON_PROTEIN_ACTIVITY = Val("effect on protein activity (VariO:0053) or more detailed")
  val EFFECT_ON_PROTEIN_INTERACTION = Val("effect on protein interaction (VariO_0058)")
  val EFFECT_ON_PROTEIN_STABILITY = Val("effect on protein stability (VariO_0034)")
  
  val EFFECT_ON_PHOSPHORYLATION = Val("effect on protein post translational modification (VariO_0107)")
  val EFFECT_ON_MAMMALIAN_PHENOTYPE = Val("EFFECT-TO-BE-DEFINED")
  val EFFECT_ON_SUBSTRACT = Val("effect on protein specificity (VariO_0055) ????")
  val EFFECT_ON_NEGATIVE_FORM = Val("EFFECT-TO-BE-DEFINED")

}
