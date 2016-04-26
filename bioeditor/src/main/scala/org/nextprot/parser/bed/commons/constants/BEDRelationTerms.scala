package org.nextprot.parser.bed.commons.constants

object BEDRelationTerms {
/*  
  val EFFECT_ON_SUBCELLULAR_LOCALIZATION = "subcellular localization"; //"effect on protein subcellular localization"; // (VariO_0033)
  val EFFECT_ON_PROTEIN_ACTIVITY = "protein activity"; //"effect on protein activity"; // (VariO:0053)
  val EFFECT_ON_PROTEIN_INTERACTION = "binding"; //"effect on protein interaction"; // (VariO_0058)
  val EFFECT_ON_PROTEIN_STABILITY = "effect on protein stability"; // (VariO_0034)*/

  val EFFECT_ON_SUBCELLULAR_LOCALIZATION = "effect on protein subcellular localization"; // (VariO_0033)
  val EFFECT_ON_PROTEIN_ACTIVITY = "effect on protein activity"; // (VariO:0053)
  val EFFECT_ON_PROTEIN_INTERACTION = "effect on protein interaction"; // (VariO_0058)
  val EFFECT_ON_PROTEIN_STABILITY = "effect on protein stability"; // (VariO_0034)
  val EFFECT_ON_PHOSPHORYLATION = "effect on phosphorylation Vario???";
  val EFFECT_ON_MAMMALIAN_PHENOTYPE = "effect on mammalian phenotype???";
  
  val ALL_EFFECTS = List(EFFECT_ON_SUBCELLULAR_LOCALIZATION,
		  		 		 EFFECT_ON_PROTEIN_ACTIVITY, 
		  		 		 EFFECT_ON_PROTEIN_INTERACTION, 
		  		 		 EFFECT_ON_PROTEIN_STABILITY, 
		  		 		 EFFECT_ON_PHOSPHORYLATION, 
		  		 		 EFFECT_ON_MAMMALIAN_PHENOTYPE)

}