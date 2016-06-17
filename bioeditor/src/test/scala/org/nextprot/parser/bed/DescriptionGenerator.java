package org.nextprot.parser.bed;

import org.nextprot.commons.statements.RawStatement;

public class DescriptionGenerator {

	public static String getDescriptionForPhenotypeAnnotation(String impact, RawStatement rawStatement){
		
		String category = rawStatement.getAnnotation_category().toLowerCase();
		
        if(category.equals("go-cellular-component")) {
        	return impact + "s localisation in " + rawStatement.getAnnot_cv_term_name();

        }else  if(category.equals("go-biological-process")) {
        
        	return impact + "s " + rawStatement.getAnnot_cv_term_name();
        }else  if(category.equals("go-molecular-function")) {

        	return impact + "s " + rawStatement.getAnnot_cv_term_name();
        }
        else  if(category.equals("binary-interaction")) {
        	return impact + "s binding to " + rawStatement.getBiological_object_accession();

        }else  if(category.equals("small-molecule-interaction")) {
        	return impact + "s binding to " + rawStatement.getBiological_object_accession();
        }
        
        else throw new RuntimeException("Category " + category + " not defined");
		
	}
}
