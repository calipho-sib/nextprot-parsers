package org.nextprot.parser.core.datamodel.annotation

import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._

class ExperimentalContextWrapper(val _tissue: String, val _syns: List[ExperimentalContextSynonym])  {

	def toXML = {
	    <com.genebio.nextprot.dataloader.context.ExperimentalContextWrapper>
			<mdataAccession>MDATA_0005</mdataAccession>
			<wrappedBean>
				<tissue>
					<cvName>{ _tissue }</cvName>
					<cvTermCategory>NEXTPROT_TISSUE</cvTermCategory>
				</tissue>
				<detectionMethod>
					<cvName>Spatial pattern of protein expression evidence</cvName>
					<cvTermCategory>ECO</cvTermCategory>
				</detectionMethod>
		{
			if (_syns != null && ! _syns.isEmpty) {
				<contextSynonyms>
					{ _syns.map(_.toXML) }
				</contextSynonyms>
		    }
		}
			</wrappedBean>
    	</com.genebio.nextprot.dataloader.context.ExperimentalContextWrapper>
		
		
	}
		
}