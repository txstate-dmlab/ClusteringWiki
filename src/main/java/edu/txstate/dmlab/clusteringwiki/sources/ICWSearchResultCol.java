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
 * Defines public interface for an ordered collection of abstract
 * search results from any data source or provider
 */
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ordered collection of Abstract Search Results
 * 
 * @author David C. Anastasiu
 *
 */
public interface ICWSearchResultCol {

	
	/**
	 * @return the clusterKey
	 */
	public String getClusterKey();

	/**
	 * @param clusterKey the clusterKey to set
	 */
	public void setClusterKey(String clusterKey);

	
	/**
	 * Provide identification information for the data source that
	 * produced this collection of results
	 * 
	 * @param key Key for the currently used data source
	 * @param label Label for the currently used data source
	 */
	public void setDataSourceInfo(String key, String label);
	
	/**
	 * Unique key within the abstract search web service assigned to 
	 * the data source which produced this result
	 * 
	 * @return Key for the currently used data source
	 */
	public String getDataSourceKey();
	
	/**
	 * @param dataSourceKey the dataSourceKey to set
	 */
	public void setDataSourceKey(String dataSourceKey);
	
	/**
	 * Label for the currently used data source. (ex: NY Data Set)
	 * 
	 * @return Label for the currently used data source
	 */
	public String getDataSourceName();
	
	/**
	 * @param dataSourceName the dataSourceName to set
	 */
	public void setDataSourceName(String dataSourceName);
	
	/**
     * The number of total query matches in the data source.
     *
     * @return Total of query matches.
     */
	public int getTotalResults();

	/**
	 * @param totalResults the totalResults to set
	 */
	public void setTotalResults(int totalResults);
	
    /**
     * The number of returned query matches.
     * <i>Could be lower than requested, but not higher.</i>
     *
     * @return Number of returned query matches.
     */
	public int getReturnedCount();

	/**
	 * @param returnedCount the returnedCount to set
	 */
	public void setReturnedCount(int returnedCount);
	
    /**
     * The position of the first returned item in the overall search result set.
     *
     * @return Position of the first returned item in the overall search result set.
     */
	public int getFirstPosition();

	/**
	 * @param firstPosition the firstPosition to set
	 */
	public void setFirstPosition(int firstPosition);
	
	/**
	 * @return the errors
	 */
	public List<String> getErrors();


	/**
	 * clear all current errors
	 */
	public void clearErrors();
	
	/**
	 * Add an error to the errors list
	 * @param error the error to be added
	 */
	public void addError(String error);
	
    /**
     * Ordered list of retrieved results.
     *
     * @return Ordered list of results.
     */
	public ICWSearchResult[] getResults();
	
	/**
	 * Method should also add unique ids to the results
	 * @param results the results to set
	 */
	public void setResults(ICWSearchResult[] results);
	
	/**
	 * Add a result
	 * Method should also add unique ids to the results
	 * @param result result to be added
	 */
	public void addResult(ICWSearchResult result);
	
	/**
	 * Get a given document from the collection by referencing
	 * its id value
	 * @param id Document id (ICWSearchResult.id) for the result
	 * @return ICWSearchResult the result sought for in the collection
	 */
	public ICWSearchResult getResultByDocumentId(int id);
	
	/**
	 * Get a document from the collection by referencing its collection
	 * index
	 * @param id Collection doc id / index
	 * @return ICWSearchResult the result sought for in the collection
	 */
	public ICWSearchResult getResultByCollectionId(int id);
	
	/**
	 * Get the internal index of a document with given id
	 * @param id Document id
	 * @return int Collection Index
	 */
	public int getDocumentIndex(int id);
	
	/**
	 * Get document id for a document at the given internal index
	 * @param index Internal index for document
	 * @return int document id
	 */
	public int getDocumentId(int index);
	
	/**
	 * Clear stored results
	 */
	public void clearResults();
	
	/**
	 * Create customized JSON representation of the object
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException;
	
	/**
	 * Retrieve up tp top k urls from the response set
	 * @return set of url strings
	 */
	public List<String> getTopKResponseUrls(int k);
    
}
