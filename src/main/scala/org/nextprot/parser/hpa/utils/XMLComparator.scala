package org.nextprot.parser.hpa.utils

import scala.xml.Node
import java.io.File

object XMLComparator {

  def compareXMLWithFile(node1: Node, file: File): Boolean = {

    val n1 = node1.toString.replaceAll("[\n\r\t ]", "")
    val expect = scala.io.Source.fromFile(file, "utf-8").getLines.mkString.replaceAll("[\n\r\t ]", "")
    return n1.equals(expect);

  }

}

