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

@Entity
@Table(name = "test_step_executions")
public class TestStepExecution {

	private Integer id;
	
	private String executionId;
	
	private Integer stepId;
	
	private Integer tagCount;
	
	private Double userEffort;
	
	private Double baseEffort;
	
	private Double baseRelevantEffort;
	
	private Integer uid;
	
	private String cluster;
	
	private String tags;
	
	private String times;
	
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the executionId
	 */
	@Column(name = "execution_id", nullable = false)
	public String getExecutionId() {
		return executionId;
	}

	/**
	 * @param executionId the executionId to set
	 */
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	/**
	 * @return the stepId
	 */
	@Column(name = "step_id", nullable = false)
	public Integer getStepId() {
		return stepId;
	}

	/**
	 * @param stepId the stepId to set
	 */
	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	/**
	 * @return the userEffort
	 */
	@Column(name = "user_effort", nullable = false)
	public Double getUserEffort() {
		return userEffort;
	}

	/**
	 * @param userEffort the userEffort to set
	 */
	public void setUserEffort(Double userEffort) {
		this.userEffort = userEffort;
	}

	/**
	 * @return the baseEffort
	 */
	@Column(name = "base_effort", nullable = false)
	public Double getBaseEffort() {
		return baseEffort;
	}

	/**
	 * @param baseEffort the baseEffort to set
	 */
	public void setBaseEffort(Double baseEffort) {
		this.baseEffort = baseEffort;
	}

	/**
	 * @return the baseRelevantEffort
	 */
	@Column(name = "base_rel_effort", nullable = false)
	public Double getBaseRelevantEffort() {
		return baseRelevantEffort;
	}

	/**
	 * @param baseRelevantEffort the baseRelevantEffort to set
	 */
	public void setBaseRelevantEffort(Double baseRelevantEffort) {
		this.baseRelevantEffort = baseRelevantEffort;
	}

	/**
	 * @return the uid
	 */
	@Column(name = "uid", nullable = true)
	public Integer getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(Integer uid) {
		this.uid = uid;
	}

	/**
	 * @return the cluster
	 */
	@Column(name = "cluster", nullable = false, columnDefinition="TEXT")
	public String getCluster() {
		return cluster;
	}

	/**
	 * @param cluster the cluster to set
	 */
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	/**
	 * @return the tags
	 */
	@Column(name = "tags", nullable = false)
	public String getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * @return the times
	 */
	@Column(name = "times", nullable = false)
	public String getTimes() {
		return times;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(String times) {
		this.times = times;
	}

	
}
