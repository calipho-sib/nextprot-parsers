package org.nextprot.parsers.bed.converter;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;

public class DescriptionGenerator {

	public static String getDescriptionForPhenotypeAnnotation(String impact, Statement statement){
		
		String impactString = impact + "s";
		String category = statement.getValue(StatementField.ANNOTATION_CATEGORY).toLowerCase();

		if(impactString.equals("no-impacts")){
			impactString = "has no impact on";
		}
        if(category.equals("go-cellular-component")) {
        	return impactString + " localisation in " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        }else  if(category.equals("go-biological-process")) {
        	return impactString + " " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        }else  if(category.equals("protein-property")) {
        	return impactString + " " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        }else  if(category.equals("go-molecular-function")) {
        	return impactString + " " + statement.getValue(StatementField.ANNOT_CV_TERM_NAME);
        } else if(category.equals("binary-interaction")) {
        	return impactString + " binding to " + statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
        }else  if(category.equals("small-molecule-interaction")) {
        	return impactString + " binding to " + statement.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION);
        }else throw new RuntimeException("Category " + category + " not defined");
		
	}
}
