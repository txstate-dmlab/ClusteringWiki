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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Base implementation of ClusteringWiki search result collection
 * Extended by others
 * 
 * @author David C. Anastasiu
 *
 */
@XStreamAlias("search")
public class BaseCWSearchResultCol implements
		ICWSearchResultCol {

	@XStreamAlias("clusterKey")
	protected String clusterKey;
	
	@XStreamAlias("dataSourceKey")
	protected String dataSourceKey = "";
	
	@XStreamAlias("dataSourceName")
	protected String dataSourceName = "";
	
	@XStreamAlias("service")
	protected String service = "";
	
	@XStreamAlias("totalResults")
	protected int totalResults = 0;
	
	@XStreamAlias("returnedCount")
	protected int returnedCount = 0;
	
	@XStreamAlias("firstPosition")
	protected int firstPosition = 0;
	
	@XStreamAlias("results")
	protected ICWSearchResult[] results = null;
	
	@XStreamAlias("errors")
	protected final List<String> errors =  new ArrayList<String>();
	
	@XStreamOmitField
	@JsonIgnore
	private int count = 0;
	
	@XStreamOmitField
	@JsonIgnore
	private Map<Integer, Integer> resultIndex = null;

	/**
	 * @return the clusterKey
	 */
	public String getClusterKey() {
		return clusterKey;
	}

	/**
	 * @param clusterKey the clusterKey to set
	 */
	public void setClusterKey(String theClusterKey) {
		clusterKey = theClusterKey;
	}

	/**
	 * Provide identification information for the data source that
	 * produced this collection of results
	 * 
	 * @param key Key for the currently used data source
	 * @param label Label for the currently used data source
	 */
	public void setDataSourceInfo(String key, String label){
		setDataSourceKey(key);
		setDataSourceName(label);
	}
	
	/**
	 * @return the dataSourceKey
	 */
	public String getDataSourceKey() {
		return dataSourceKey;
	}

	/**
	 * @param dataSourceKey the dataSourceKey to set
	 */
	public void setDataSourceKey(String theDataSourceKey) {
		dataSourceKey = theDataSourceKey;
	}

	/**
	 * @return the dataSourceName
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * @param dataSourceName the dataSourceName to set
	 */
	public void setDataSourceName(String theDataSourceName) {
		dataSourceName = theDataSourceName;
	}

	/**
	 * @return the totalResults
	 */
	public int getTotalResults() {
		return totalResults;
	}

	/**
	 * @param totalResults the totalResults to set
	 */
	public void setTotalResults(int numTotalResults) {
		totalResults = numTotalResults;
	}

	/**
	 * @return the returnedCount
	 */
	public int getReturnedCount() {
		return returnedCount;
	}

	/**
	 * @param returnedCount the returnedCount to set
	 */
	public void setReturnedCount(int theReturnedCount) {
		returnedCount = theReturnedCount;
	}

	/**
	 * @return the firstPosition
	 */
	public int getFirstPosition() {
		return firstPosition;
	}

	/**
	 * @param start the firstPosition to set
	 */
	public void setFirstPosition(int start) {
		firstPosition = start;
	}

	
	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	
	/**
	 * clear all current errors
	 */
	public void clearErrors(){
		errors.clear();
	}
	
	/**
	 * Add an error to the errors list
	 * @param error the error to be added
	 */
	public void addError(String error){
		errors.add(error);
	}

	/**
	 * @return the results
	 */
	public ICWSearchResult[] getResults() {
		return results;
	}

	/**
	 * Sets results array
	 * Note that firstPosition must be set before adding
	 * results as result ids depend on that value.
	 * @param results the results to set
	 */
	public void setResults(ICWSearchResult[] theResults) {
		results = theResults;
		int start = firstPosition;
		resultIndex = new HashMap<Integer, Integer>();
		for(int i=0; i < results.length; i++){
			results[i].setId(start + i);
			//register in inverted index for fast retrieval
			resultIndex.put(Integer.valueOf(start + i), Integer.valueOf(i));
		}
		count = results.length;
	}
	
	/**
	 * Add a result
	 * Note that firstPosition must be set before adding
	 * results as result ids depend on that value.
	 * @param result result to be added
	 */
	public void addResult(ICWSearchResult result){
		if(results == null){
			results = new ICWSearchResult[returnedCount];
			resultIndex = new HashMap<Integer, Integer>();
		}
		if(result.getId() < 1)
			result.setId(firstPosition + count);
		if(count < returnedCount){
			results[count] = result;
			//register in inverted index for fast retrieval
			resultIndex.put(Integer.valueOf(result.getId()), 
				Integer.valueOf(count));
			count++;
		}
	}
	
	/**
	 * Get a given document from the collection by referencing
	 * its id value
	 * @param id Document id (ICWSearchResult.id) for the result
	 * @return ICWSearchResult the result sought for in the collection
	 */
	public ICWSearchResult getResultByDocumentId(int id){
		int index = -1;
		try {
			index = getDocumentIndex(id);
		} catch (IllegalArgumentException e){
			return null;
		}
		if(index > 0)
			return results[index];
		return null;
	}
	
	/**
	 * Get a document from the collection by referencing its collection
	 * index
	 * @param index Collection doc id / index
	 * @return ICWSearchResult the result sought for in the collection
	 */
	public ICWSearchResult getResultByCollectionId(int index){
		if(index < 0 || index > results.length) return null;
			return results[index];
	}
	
	/**
	 * Get the internal index of a document with given id
	 * @param id Document id
	 * @return int Collection Index
	 */
	public int getDocumentIndex(int id){
		Integer index = resultIndex.get(Integer.valueOf(id));
		if(index == null) throw new IllegalArgumentException();
		int i = index.intValue();
		if(i < 0 || i > results.length) throw new IllegalArgumentException();
		return i;
	}
	
	/**
	 * Get document id for a document at the given internal index
	 * @param index Internal index for document
	 * @return int document id
	 */
	public int getDocumentId(int index){
		Integer id =  resultIndex.get(Integer.valueOf(index));
		if(id == null) throw new IllegalArgumentException();
		return id.intValue();
	}
	
	/**
	 * Clear stored results
	 */
	public void clearResults(){
		results = null;
		resultIndex = null;
		count = 0;
	}

	/**
	 * Create customized JSON representation of the object
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException{
		JSONObject j = new JSONObject();
		
		j.put("clusterKey", clusterKey);
		j.put("dataSourceKey", dataSourceKey);
		j.put("dataSourceName", dataSourceName);
		j.put("service", service);
		j.put("totalResults", totalResults);
		j.put("returnedCount", returnedCount);
		j.put("firstPosition", firstPosition);
		
		if(results != null){
			JSONArray a = new JSONArray();
			for(ICWSearchResult r : results)
				a.put(r.toJSON());
			j.put("results", a);
		} else 
			j.put("results", JSONObject.NULL);
		j.put("errors", errors);
		
		return j;
	}
	
	/**
	 * Retrieve up tp top k urls from the response set
	 * @return set of url strings
	 */
	public List<String> getTopKResponseUrls(int k){
		List<String> urls = new ArrayList<String>(k);
		int i = 0;
		if(results != null)
			for(ICWSearchResult r : results){
					urls.add(r.getUrl());
				i++;
				if(i == k) return urls;
			}
		
		return urls;
	}

}
