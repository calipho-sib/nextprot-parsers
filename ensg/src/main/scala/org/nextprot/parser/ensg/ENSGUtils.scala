package org.nextprot.parser.ensg

import scala.xml.NodeSeq

/**
 * Created by fnikitin on 18/06/15.
 */
object ENSGUtils {

  def getEnsemblReferenceNodeSeq(entryElem: NodeSeq): NodeSeq = {
    return (entryElem \ "dbReference").filter(el => (el \ "@type").text == "Ensembl")
  }

  def getEnsemblIdNodeSeq(entryElem: NodeSeq): NodeSeq = {
    return (getEnsemblReferenceNodeSeq(entryElem) \ "property").filter(el => (el \ "@type").text == "gene ID")
  }

  def getGeneIds(entryElem: NodeSeq, sep: String): String = {
    return (ENSGUtils.getEnsemblIdNodeSeq(entryElem) \\ "@value").map(_.text).mkString(sep)
  }
}
