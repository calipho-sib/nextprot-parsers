package org.nextprot.parser.ensg

import scala.xml.NodeSeq

/**
 * Created by fnikitin on 18/06/15.
 */
object ENSGUtils {

  def getEnsemblReferenceNodeSeq(entryElem: NodeSeq): NodeSeq =
    (entryElem \ "dbReference").filter(el => (el \ "@type").text == "Ensembl")

  def getEnsemblIdNodeSeq(entryElem: NodeSeq): NodeSeq =
    (getEnsemblReferenceNodeSeq(entryElem) \ "property").filter(el => (el \ "@type").text == "gene ID")

  def getGeneIds(entryElem: NodeSeq, sep: String): String =
    (ENSGUtils.getEnsemblIdNodeSeq(entryElem) \\ "@value").map(_.text).mkString(sep)
}
