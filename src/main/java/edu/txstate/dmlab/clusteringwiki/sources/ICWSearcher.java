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
 * Abstraction of a search engine
 * 
 * @author David C. Anastasiu
 *
 */
public interface ICWSearcher {

	/**
	 * @return the key
	 */
	public String getKey();

	/**
	 * @param key the key to set
	 */
	public void setKey(String key);

	/**
	 * @return the label
	 */
	public String getLabel();

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label);
	
	/**
	 * Execute search given string query with other defaults
	 * @param service
	 * @param query
	 * @return collection of results
	 */
	public ICWSearchResultCol search(String service, String query);
	
	/**
	 * Execute search given string query and num of results expected
	 * @param service
	 * @param query
	 * @param numResults
	 * @return collection of results
	 */
	public ICWSearchResultCol search(String service, String query, Integer numResults);
	
	/**
	 * Execute search given string query and result to start with
	 * @param service
	 * @param query
	 * @param start
	 * @return collection of results
	 */
	public ICWSearchResultCol search(String service, String query, int start);
	
	/**
	 * Execute search given string query, num results expected, as well as
	 * result num to start with
	 * @param service
	 * @param query
	 * @param numResults
	 * @param start
	 * @return collection of results
	 */
	public ICWSearchResultCol search(String service, String query, int start, int numResults);
	
}
