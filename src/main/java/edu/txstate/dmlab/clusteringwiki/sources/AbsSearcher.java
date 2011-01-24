package edu.txstate.dmlab.clusteringwiki.sources;

/**
 *  ClusteringWiki - personalized and collaborative clustering of search results
 *  Copyright (C) 2010  Texas State University-San Marcos
 *  
 *  Contact: http://dmlab.cs.txstate.edu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import edu.txstate.dmlab.clusteringwiki.util.URLUtils;

/**
 * Abstract searcher implementation
 * 
 * @author David C. Anastasiu
 *
 */
public class AbsSearcher extends BaseCWSearcher implements ICWSearcher {

	/**
	 * API URL path without domain if localDomain is true
	 */
	private static final String baseUrlPath = "AbstractSearch/rest/search/";
	
	@Autowired(required=true) 
	private HttpServletRequest httpRequest; 
	
	/**
	 * Search data source key
	 */
	protected String key = "abs";
	
	/**
	 * Search data source name/label
	 */
	protected String label = "AbS Search API";
	
	/**
	 * Default constructor
	 */
	public AbsSearcher(){
		
	}

	/**
	 * Practical implementation of the search method
	 */
	public ICWSearchResultCol search(String service, String query, int start, int count) {

		AbsSearchResultCol col = null;
		
		if(baseUrlPath == null || baseUrlPath.length() < 10){
			if(col == null)
	    		col = new AbsSearchResultCol(null);
	    	col.addError("Error calling AbS Search Service: invalid baseUrlPath.");
	        return col;
		}
		
		//query specifics
		if(start < 1) start = 1;
		if(count < 0) count = getNumResults();
		
		//build service query
		String url = null;
		try {
			url = buildRequestUrl(service, query, start, count);
		} catch (UnsupportedEncodingException e) {
			if(col == null)
	    		col = new AbsSearchResultCol(null);
	    	col.addError("Error calling AbS Search Service: invalid query string.");
	        return col;
		}
		
		//execute query
		try {

			String doc = URLUtils.getWebPage(url);
			JSONObject res = new JSONObject(doc);

			col = new AbsSearchResultCol(res);
			col.setDataSourceInfo(key, label);
			col.setFirstPosition(start);

	    }
		catch (JSONException e) {
			if(col == null)
	    		col = new AbsSearchResultCol(null);
	    	col.addError("Error calling AbS Search Service: " +
		              e.toString());
	        return col;
		}
		catch (MalformedURLException e) {
			if(col == null)
	    		col = new AbsSearchResultCol(null);
	    	col.addError("Error calling AbS Search Service: " +
		              e.toString());
	        return col;
		}
		catch (IOException e) {
	        // Most likely a network exception of some sort.
	    	if(col == null)
	    		col = new AbsSearchResultCol(null);
	    	col.addError("Error calling AbS Search Service: " +
		              e.toString());
	        return col;
	    }
	    
	    return col;
	}
	
	/**
	 * Build the request URL string in the correct format expected 
	 * by the AbS Search API
	 */
	private String buildRequestUrl(String service, String query, int start, int count) 
		throws UnsupportedEncodingException {
		//build base URL
		String url = "";
		
		if(!baseUrlPath.toLowerCase().startsWith("http")){
			String q[] = httpRequest.getRequestURL().toString().split("\\/");
			if(baseUrlPath.startsWith("/") || baseUrlPath.startsWith("\\"))
				url = q[0] + "//" + q[2] + "/" + baseUrlPath.substring(1);
			else
				url = q[0] + "//" + q[2] + "/" + baseUrlPath;
		} else {
			url = baseUrlPath;
		}
		
		if(baseUrlPath.endsWith("/") || baseUrlPath.endsWith("\\"))
			url = url.substring(0, url.length() - 1);
		
		//add query specifics
		url += "/" + service + "/" + java.net.URLEncoder.encode(query, "UTF-8") + 
			"/" + count + "/" + start + ".json";
		
		return url;
	}

	
}
