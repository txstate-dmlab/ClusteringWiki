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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Collection of search results for an abstract search response set
 * 
 * @author David
 *
 */
@XStreamAlias("search")
public class AbsSearchResultCol extends BaseCWSearchResultCol 
	implements ICWSearchResultCol {
	
	/**
	 * Creates a collection of results from JSON response
	 * Note that firstPosition must be set before adding
	 * results as result ids depend on that value.
	 * @param res
	 */
	public AbsSearchResultCol(JSONObject res) {

		if(res == null) return;
		JSONObject search = null;
		
		try {
			search = res.getJSONObject("search");
			JSONArray errors = search.getJSONArray("errors");
			if(errors != null && errors.length() > 0){
				for(int i=0; i< errors.length(); i++){
					String error = errors.getString(i);
					addError("AbS API exception: " + error);
				}
				return;
			}
		} catch (JSONException e) {
			addError("AbS API exception: " + e.getMessage());
		}
		
		
		try {
			totalResults = search.getInt("totalResults");
			firstPosition = search.getInt("firstPosition");
			
			JSONArray j = search.getJSONArray("results");
			returnedCount = j.length();
			
			for(int i=0; i< j.length(); i++){
				ICWSearchResult r = new AbsSearchResult( j.getJSONObject(i) );
				r.setIndex(i);
				addResult( r );
			}
			
		} catch (JSONException e) {
			addError("Could not retrieve AbS results: " + e.getMessage());
		}
	}

	
}
