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
@Table(name = "test_details")
public class TestDetail {
	
	private Integer id;
	
	private Integer testId;
	
	private Integer stepId;
	
	private Integer stepOrder;

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
	 * @return the testId
	 */
	@Column(name = "test_id")
	public Integer getTestId() {
		return testId;
	}

	/**
	 * @param testId the testId to set
	 */
	public void setTestId(Integer testId) {
		this.testId = testId;
	}

	/**
	 * @return the stepId
	 */
	@Column(name = "step_id")
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
	 * @return the stepOrder
	 */
	@Column(name = "step_order")
	public Integer getStepOrder() {
		return stepOrder;
	}

	/**
	 * @param stepOrder the stepOrder to set
	 */
	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}
	
	
}
