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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *  Search result for an abstract search response
 *  
 * @author David C. Anastasiu
 *
 */
@XStreamAlias("result")
public class AbsSearchResult extends BaseCWSearchResult 
	implements ICWSearchResult {


	/**
	 * Generate a search result from its JSON AbS Result value
	 * @param j JSONObject containing result data
	 */
	public AbsSearchResult(JSONObject j) {

		try {
			cacheSize = j.getInt("cacheSize");
			snippet = j.getString("snippet");
			snippet = snippet.replaceAll("[^\\p{ASCII}]", "");
			url = j.getString("url");
			clickUrl = j.getString("clickUrl");
			modificationDate = j.getInt("modificationDate");
			cacheUrl = j.getString("cacheUrl");
			title = j.getString("title");
			title = title.replaceAll("[^\\p{ASCII}]", "");
			mimeType = j.getString("mimeType");
			trecId = j.getString("trecId");
			relevant = j.getInt("relevant");
		} catch (JSONException e) { /* do nothing */ }
		
	}
	
	/**
	 * Implementation of Abstract method
	 * @return the document
	 */
	@JsonIgnore
	public String getDocument(){
		if(document != null)
			return document;
		document = getWebDocument();
		return document;
	}
}
