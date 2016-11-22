package org.nextprot.parser.hpa.antibody

import org.scalatest._
import scala.xml.PrettyPrinter
import java.io.File
import java.io.FileWriter
import org.nextprot.parser.hpa.utils.XMLComparator

class AntibodyEntry2016Test extends HPAAntibodyTestBase {

  val hpadir = "src/test/resources/hpa/"
  val infile = "input-antibody-2016.xml"
  val outfile = "output-antibody-2016.xml"

  "The HPAAntibodyNXParser " should " parse successfully a whole Antibody HPA file" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + infile);

    //println(wrapper.antibodyList(0).toXML) 
    val writer = new FileWriter(new File(hpadir + outfile))
    writer.write(wrapper.antibodyList(0).toXML.toString)
    //writer.write(scala.xml.Utility.trim(wrapper.toXML).toString)
    writer.close()
    assert(wrapper != null)
  }

  "The antibody sequence " should " start with PDWSEPEEPENQTVNIQ" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + infile);
    val seq = (wrapper.antibodyList(0).toXML \ "wrappedBean" \ "bioSequences" \ "com.genebio.nextprot.datamodel.identifier.BioSequence" \ "bioSequence").text
    assert(seq.startsWith("PDWSEPEEPENQTVNIQ"))
  }

  "The description for the first tissue expression assay " should " be CDATA and of type 'tissue'" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir +infile);
    val node = (wrapper.antibodyList(0).toXML \ "summaryAnnotations" \ "wrappedBean" \ "com.genebio.nextprot.dataloader.dto.RawAnnotation")
    val rawdesc = (node \ "description").toString()
    assert(rawdesc.contains("![CDATA[tissue:"))

  }

  "The HPAAntibodyNXParser " should " produce 1 antibody entries when parsing ENSG00000000457.xml" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir +infile);
    val abd_cnt = wrapper.antibodyList.length
    assert(abd_cnt == 1)

  }

}
