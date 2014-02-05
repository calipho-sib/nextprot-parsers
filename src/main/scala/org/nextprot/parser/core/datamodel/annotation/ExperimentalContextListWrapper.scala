package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._


class ExperimentalContextListWrapper(val eclist: List[ExperimentalContextWrapper]) extends TemplateModel {

     def toXML = {
    	 <object-stream>
    	 {
    		 eclist.map(_.toXML)  		 
    	 }
     	</object-stream>
     }
     
    override def getQuality: NXQuality = NXQuality.SILVER;

     
}