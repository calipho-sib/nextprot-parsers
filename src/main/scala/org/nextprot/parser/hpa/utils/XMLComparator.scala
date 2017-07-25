package org.nextprot.parser.hpa.utils

import scala.xml.Node
import java.io.File

object XMLComparator {

  def compareXMLWithFile(node1: Node, file: File): Boolean = {

    val f1 = node1.toString.replaceAll("[\t\r\n ]", "")
    val f2 = scala.io.Source.fromFile(file, "utf-8").getLines.mkString.replaceAll("[\t\r\n ]", "")
    println("--- produced ---")
    println(f1);
    println("--- expected ---")
    println(f2);
    println("------------ ---")
    
    return f1.equals(f2);

  }

}

