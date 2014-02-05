package org.nextprot.parser.core.datamodel.annotation

class ExperimentalContextSynonym(val _synonymName: String) {

   def toXML =
       <com.genebio.nextprot.datamodel.resource.ExperimentalContextSynonym>
		   <synonymName>
   			{scala.xml.PCData(_synonymName)}
		   </synonymName>
           <synonymType>CONTEXT_SIGNATURE</synonymType>
           <isMain>false</isMain>
       </com.genebio.nextprot.datamodel.resource.ExperimentalContextSynonym>
  
}