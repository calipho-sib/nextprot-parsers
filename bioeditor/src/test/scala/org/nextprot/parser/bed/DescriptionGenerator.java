package org.nextprot.parser.bed;

import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementField;

public class DescriptionGenerator {

	public static String getDescriptionForPhenotypeAnnotation(String impact, RawStatement statement){
		
		String category = statement.getValue(StatementField.ANNOTATION_CATEGORY).toLowerCase();
		
        if(category.equals("go-cellular-component")) {
        	return impact + "s localisation in " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        }else  if(category.equals("go-biological-process")) {
        	return impact + "s " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        }else  if(category.equals("go-molecular-function")) {
        	return impact + "s " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        } else if(category.equals("binary-interaction")) {
        	return impact + "s binding to " + statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
        }else  if(category.equals("small-molecule-interaction")) {
        	return impact + "s binding to " + statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
        }
        
        else throw new RuntimeException("Category " + category + " not defined");
		
	}
}
