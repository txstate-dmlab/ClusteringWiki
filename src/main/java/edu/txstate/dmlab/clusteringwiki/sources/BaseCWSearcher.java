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

/**
 * Abstract base implementation of a ClusteringWiki searcher
 * Class extended by other searchers
 * 
 * @author David C. Anastasiu
 *
 */
public abstract class BaseCWSearcher implements ICWSearcher {
	
	/**
	 * Search data source key
	 */
	protected String key = "unknown";
	
	/**
	 * Search data source name/label
	 */
	protected String label = "Unknown Search API";
	

	/**
	 * Number of default results to use
	 * (To be overwritten by specific searcher implementations)
	 */
	protected int numResults = 100;
	

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String theKey) {
		key = theKey;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String theLabel) {
		label = theLabel;
	}
	
	/**
	 * @param numResults the numResults to set
	 */
	public void setNumResults(int theNumResults) {
		numResults = theNumResults;
	}

	/**
	 * @return the numResults
	 */
	public int getNumResults() {
		return numResults;
	}
	
	/**
	 * Default controller
	 */
	public BaseCWSearcher(){
		
	}


	/**
	 * Pass on the data source information to the result collection object
	 * @param col results collection obj
	 * @return ICWSearchResultCol results collection obj
	 */
	protected ICWSearchResultCol registerDataSourceInfo( ICWSearchResultCol col){
		if( col == null ) col = new AbsSearchResultCol(null);
		col.setDataSourceInfo(key, label);
		return col;
	}
	
	/*
	 * Method to be specifically implemented by concrete Searchers
	 * @see edu.txstate.dmlab.clusteringwiki.sources.ICWSearcher#search(java.lang.String, java.lang.String, int, int)
	 */
	public abstract ICWSearchResultCol search(String service, String query, int start, int count);
	
	/**
	 * Search given only a service key and the query
	 */
	public ICWSearchResultCol search(String service, String query) {
		return search(service, query, 1, getNumResults());
	}

	/**
	 * Search given only a service key, the query, and number of desired results
	 */
	public ICWSearchResultCol search(String service, String query, Integer numRes) {
		return search(service, query, 1, numRes.intValue());
	}

	/**
	 * Search given only a service key, the query, and the results start index
	 */
	public ICWSearchResultCol search(String service, String query, int start) {
		return search(service, query, start, numResults);
	}

	
}
