package org.nextprot.parsers.bed.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeneNameService {

	private static final String endpoint = "http://dev-api.nextprot.org/entry-accessions/gene/";

	public static String getNXAccessionForGeneName(String geneName) throws Exception {

		String urlString = endpoint + geneName + ".json";

		URL url = new URL(urlString);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        
		String content = sb.toString();
		
		ObjectMapper mapper = new ObjectMapper();
		String[] geneNames = mapper.readValue(content, String[].class);

		if(geneNames.length != 1){
			throw new RuntimeException("Found " + geneNames.length + " gene names instead of 1");
		}
		
		return geneNames[0];

	}
}