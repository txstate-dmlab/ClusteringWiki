package edu.txstate.dmlab.clusteringwiki.entity;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
@Table(name = "test_steps")
public class TestStep {
	
	private Integer id;
	
	private Integer topicId;
	
	private String source;
	
	private Integer results;
	
	private Integer algorithm;
	
	private Integer enableTagging;
	
	private Integer tagCount;
	
	private Integer loggedIn;
	
	private Integer disableEditting;
	
	private Integer hideCluster;
	
	private String queryType;

	/**
	 * @return the testId
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	/**
	 * @param testId the testId to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the topicId
	 */
	@Column(name = "topic_id", nullable = false)
	public Integer getTopicId() {
		return topicId;
	}

	/**
	 * @param topicId the topicId to set
	 */
	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}

	/**
	 * @return the source
	 */
	@Column(name = "source", nullable = false)
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the results
	 */
	@Column(name = "results", nullable = false)
	public Integer getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Integer results) {
		this.results = results;
	}

	/**
	 * @return the algorithm
	 */
	@Column(name = "algorithm", nullable = false)
	public Integer getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(Integer algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the enableTagging
	 */
	@Column(name = "enable_tagging", nullable = false)
	public Integer getEnableTagging() {
		return enableTagging;
	}

	/**
	 * @param enableTagging the enableTagging to set
	 */
	public void setEnableTagging(Integer enableTagging) {
		this.enableTagging = enableTagging;
	}

	/**
	 * @return the tagCount
	 */
	@Column(name = "tag_count", nullable = false)
	public Integer getTagCount() {
		return tagCount;
	}

	/**
	 * @param tagCount the tagCount to set
	 */
	public void setTagCount(Integer tagCount) {
		this.tagCount = tagCount;
	}

	/**
	 * @return the loggedIn
	 */
	@Column(name = "logged_in", nullable = false)
	public Integer getLoggedIn() {
		return loggedIn;
	}

	/**
	 * @param loggedIn the loggedIn to set
	 */
	public void setLoggedIn(Integer loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * @return the disableEdit
	 */
	@Column(name = "disable_editting", nullable = false)
	public Integer getDisableEditting() {
		return disableEditting;
	}

	/**
	 * @param disableEdit the disableEdit to set
	 */
	public void setDisableEditting(Integer disableEditting) {
		this.disableEditting = disableEditting;
	}

	/**
	 * @return the hideCluster
	 */
	@Column(name = "hide_cluster", nullable = false)
	public Integer getHideCluster() {
		return hideCluster;
	}

	/**
	 * @param hideCluster the hideCluster to set
	 */
	public void setHideCluster(Integer hideCluster) {
		this.hideCluster = hideCluster;
	}

	/**
	 * @return the queryType
	 */
	@Column(name = "query_type", nullable = false)
	public String getQueryType() {
		return queryType;
	}

	/**
	 * @param queryType the queryType to set
	 */
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	/**
	 * Get a string representation of the step, including topic details
	 * @return
	 */
	public JSONObject toJSONObject() throws JSONException{
		JSONObject j = new JSONObject();
		
		//first get topic details
		if(this.topicId == null){
			j.put("error", "Invalid step topic id.");
			return j;
		}
		
		
		j.put("stepId", this.id);
		j.put("source", this.source);
		j.put("results", this.results);
		j.put("algorithm", this.algorithm);
		j.put("enableTagging", this.enableTagging);
		j.put("tagCount", this.tagCount);
		j.put("loggedIn", this.loggedIn);
		j.put("disableEditting", this.disableEditting);
		j.put("hideCluster", this.hideCluster);
		j.put("queryType", this.queryType);
		j.put("topidId", this.topicId);
		
		return j;
	}
	
	
}
