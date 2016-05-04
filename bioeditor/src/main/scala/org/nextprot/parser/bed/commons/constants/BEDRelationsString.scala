package org.nextprot.parser.bed.commons.constants

/*
 * Enum based on this Sean's example: http://www.scala-lang.org/old/node/10031?page=1#comment-43299
 */
object BEDRelationsString extends Enumeration {

  protected case class Val(name: String) extends super.Val(nextId, name) {

  }

  implicit def valueToRelationsVal(x: Value) = x.asInstanceOf[Val]

  val IncreasesLocalization = Val("increases localization to")
  val DecreasesLocalization = Val("decreases localization to")
  val HasNormalLocalization = Val("has normal localization to")
  val LocalisesToANewCompartment = Val("localizes to a new compartment")

  val HasNormal = Val("has normal")
  val Impairs = Val("impairs")
  val Increases = Val("increases")
  val Decreases = Val("decreases")
  val Gains = Val("gains function")

  val HasNormalBinding = Val("has normal binding to")
  val IncreasesBindingTo = Val("increases binding to")
  val DecreasesBindingTo = Val("decreases binding to")
  val GainsBindingTo = Val("gains binding to")

  val IsALabileFormOf = Val("is a labile form of")
  val IsAMoreStableFormOF = Val("is a more stable form of")
  val HasNoEffectOnStability = Val("has no effect on stability of")

  val RemovesPTMSite = Val("removes PTM site")
  val GainsPTMSite = Val("gains PTM site")

  val CausesPhenotype = Val("causes phenotype")
  val DoesNotCausePhenotype = Val("does not cause phenotype")

  val IsAPoorerSubstrateFor = Val("is a poorer substrate for")
  val IsADominantNegativeForm = Val("is a dominant negative form")

}
