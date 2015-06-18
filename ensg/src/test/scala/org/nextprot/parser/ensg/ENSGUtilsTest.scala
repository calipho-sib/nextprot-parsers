package org.nextprot.parser.ensg

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.xml.NodeSeq

/**
 * Created by fnikitin on 18/06/15.
 */
class ENSGUtilsTest extends FlatSpec with Matchers {

  val xml =
    <entry created="2007-02-20" dataset="Swiss-Prot" modified="2015-04-29" version="91">
      <accession>A0AVT1</accession>
      <accession>A6N8M7</accession>
      <accession>B2RAV3</accession>
      <accession>Q4W5K0</accession>
      <accession>Q6UV21</accession>
      <accession>Q86T78</accession>
      <accession>Q86TC7</accession>
      <accession>Q8N5T3</accession>
      <accession>Q8N9E4</accession>
      <accession>Q9H3T7</accession>
      <accession>Q9NVC9</accession>
      <dbReference id="MINT-1195700" type="MINT"/>
      <dbReference id="A0AVT1" type="BindingDB"/>
      <dbReference id="CHEMBL2321622" type="ChEMBL"/>
      <dbReference id="A0AVT1" type="PhosphoSite"/>
      <dbReference id="UPA00143" type="UniPathway"/>
      <dbReference id="UBA6" type="BioMuta"/>
      <dbReference id="A0AVT1" type="MaxQB"/>
      <dbReference id="A0AVT1" type="PaxDb"/>
      <dbReference id="A0AVT1" type="PRIDE"/>
      <dbReference id="55236" type="DNASU"/>
      <dbReference id="ENST00000322244" type="Ensembl">
        <molecule id="A0AVT1-1"/>
        <property type="protein sequence ID" value="ENSP00000313454"/>
        <property type="gene ID" value="ENSG00000033178"/>
      </dbReference>
      <dbReference id="ENST00000420827" type="Ensembl">
        <molecule id="A0AVT1-3"/>
        <property type="protein sequence ID" value="ENSP00000399234"/>
        <property type="gene ID" value="ENSG00000033178"/>
      </dbReference>
      <dbReference id="55236" type="GeneID"/>
      <dbReference id="hsa:55236" type="KEGG"/>
      <dbReference id="uc003hdg.4" type="UCSC">
        <molecule id="A0AVT1-1"/>
        <property type="organism name" value="human"/>
      </dbReference>
    </entry>

  it should "return the correct Ensembl Reference XML Nodes" in {

    val ensemblXrefs:NodeSeq = ENSGUtils.getEnsemblReferenceNodeSeq(xml)
    val ensemblIds:Seq[String] = ensemblXrefs.map(e => (e \ "@id").text)

    assert(ensemblIds.contains("ENST00000322244"))
    assert(ensemblIds.contains("ENST00000420827"))
  }

  it should "return the correct Ensembl Reference id Nodes" in {

    val ensemblIdNodes = ENSGUtils.getEnsemblIdNodeSeq(xml)

    assertResult("<property type=\"gene ID\" value=\"ENSG00000033178\"/><property type=\"gene ID\" value=\"ENSG00000033178\"/>")(ensemblIdNodes.toString())
  }

  it should "return the correct gene ids" in {

    val geneIds = ENSGUtils.getGeneIds(xml)

    assertResult("ENSG00000033178 ENSG00000033178")(geneIds)
  }
}
