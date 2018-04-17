package org.nextprot.parser.hpa.antibody


import scala.xml.PrettyPrinter
import java.io.File
import java.io.FileWriter
import org.nextprot.parser.hpa.utils.XMLComparator

class FullAntibodyEntryTest extends HPAAntibodyTestBase {

  val hpadir = "src/test/resources/hpa/"

  "The HPAAntibodyNXParser " should " parse successfully a whole Antibody HPA file" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + "ENSG_FOR_ONE_HPA_ANTIBODY.xml");

    val writer = new FileWriter(new File(hpadir + "ENSG_FOR_ONE_HPA_ANTIBODY_OUTPUT.xml"))
    writer.write(wrapper.antibodyList(0).toXML.toString)
    writer.close()
    assert(wrapper != null)
  }

  "The antibody sequence " should " start with MSLRGSLSRLL" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + "ENSG_FOR_ONE_HPA_ANTIBODY.xml");
    val seq = (wrapper.antibodyList(0).toXML \ "wrappedBean" \ "bioSequences" \ "com.genebio.nextprot.datamodel.identifier.BioSequence" \ "bioSequence").text
    assert(seq.startsWith("MSLRGSLSRLL"))
  }

  "The description for the first tissue expression assay " should " be CDATA and of type 'tissue'" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + "ENSG00000087460.xml");
    val node = (wrapper.antibodyList(0).toXML \ "summaryAnnotations" \ "wrappedBean" \ "com.genebio.nextprot.dataloader.dto.RawAnnotation")
    val rawdesc = (node \ "description").toString()
    assert(rawdesc.contains("![CDATA[tissue:"))

  }

  "The HPAAntibodyNXParser " should " produce 4 antibody entries when parsing ENSG00000087460.xml" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + "ENSG00000087460.xml");
    val abd_cnt = wrapper.antibodyList.length
    assert(abd_cnt == 4)

  }

  "Each antibody entry " should " contain 4 Uniprot ids when parsing ENSG00000087460.xml" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + "ENSG00000087460.xml");
    val id_cnt = (wrapper.antibodyList(0).toXML \ "uniprotIds" \ "string").length
    assert(id_cnt == 4)

  }

 "The HPAAntibodyNXParser " should " parse successfully input-antibody-2018.xml" in {

     val infile = "input-antibody-2018.xml"
     val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir +infile);
    val writer = new FileWriter(new File(hpadir + "output-of" + infile))
    writer.write(wrapper.antibodyList(0).toXML.toString)
    writer.close()
    assert(wrapper != null)
  }
 
  
  "The HPAAntibodyNXParser " should " find IH verification value in input-antibody-2018.xml" in {

    val infile = "input-antibody-2018.xml"
    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir +infile);
    val prop = (wrapper.antibodyList(0).toXML \ "wrappedBean" \"identifierProperties" \ "com.genebio.nextprot.datamodel.identifier.IdentifierProperty" );
    
    // this value was not extracted properly in previous versions
    val name = (prop(0) \ "cvPropertyName" \ "cvName").text
    val value = (prop(0) \ "propertyValue").text
    //Console.err.println("value:" + value);
    //Console.err.println("name :" + name);
    assert(name == "immunohistochemistry validation")
    assert(value == "approved")
  } 
  
  "The HPAAntibodyNXParser " should "produce exactly this output for ENSG00000081181.xml" in {

    val hpaParser = new HPAAntibodyNXParser();
    val wrapper = hpaParser.parse(hpadir + "ENSG_FOR_ONE_HPA_ANTIBODY.xml");

    //val prettyPrinter = new PrettyPrinter(1000, 4)
    //val output = prettyPrinter.format(wrapper.toXML).replaceAll("[\n\r\t ]", "")
    //val expect = scala.io.Source.fromFile(hpadir + "expected_output_for_one_hpa_antibody.xml", "utf-8").getLines.mkString.replaceAll("[\n\r\t ]", "")
    //val writer = new FileWriter(new File(hpadir + "expected_output_for_one_hpa_antibody_nospaces.xml"))
    //riter.write(scala.io.Source.fromFile(hpadir + "expected_output_for_one_hpa_antibody.xml", "utf-8").mkString.replaceAll("[ ]", ""))
    //writer.write(scala.xml.Utility.trim(scala.xml.Source.fromFile(hpadir + "expected_output_for_one_hpa_antibody.xml", "utf-8")))
    //writer.close()
    //assert(output==expect)
    //val actual = wrapper.toXML
    //val expect = scala.xml.XML.load(scala.xml.Source.fromFile(hpadir + "expected_output_for_one_hpa_antibody.xml"))
    //assertEqual(actual, expect)
    //assert(actual == expect, actual.child diff expect.child mkString(", "))
    //val act =  XML.loadFile(hpadir + "ENSG_FOR_ONE_HPA_ANTIBODY_OUTPUT.xml")
    val x = wrapper.antibodyList(0).toXML
    //    val exp = XML.loadFile(hpadir + "expected_output_for_one_hpa_antibody.xml") 
    ///    val act = exp 

//    assert(XMLComparator.compareXMLWithFile(x, new File(hpadir + "expected_output_for_one_hpa_antibody.xml")), true);
    /*    comparison.compare(act, exp) match {
      case NoDiff => println("Documents are similar.")
      case diff   => println(diff)
    }*/
    //assert(comparison(act, exp) == NoDiff)
  }

  /* private def assertEqual(actual: xml.Node, expected: xml.Node) {

    def recurse(actual: xml.Node, expected: xml.Node) {
        // depth-first checks, to get specific failures
        for ((actualChild, expectedChild) <- actual.child zip expected.child) {
            recurse(actualChild, expectedChild)
        }
        actual should be (expected)
        //assert(actual == expected, actual.child diff expected.child mkString(", "))
        
    }

    recurse(scala.xml.Utility.trim(actual), scala.xml.Utility.trim(expected))

}  */
}
