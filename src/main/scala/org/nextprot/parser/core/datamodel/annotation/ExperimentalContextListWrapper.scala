package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._


class ExperimentalContextListWrapper(val eclist: List[ExperimentalContextWrapper], val mappingsNotFound:List[String]) extends TemplateModel {

     def toXML = {
       
       
    	 <object-stream>
		{
			if (mappingsNotFound != null && ! mappingsNotFound.isEmpty) {
				{ mappingsNotFound.map(scala.xml.Comment(_)) }
		    }
		}

    	 {
    		 eclist.map(_.toXML)  		 
    	 }
     	</object-stream>
     }
     
     
    override def getQuality: NXQuality = NXQuality.SILVER;

     
}