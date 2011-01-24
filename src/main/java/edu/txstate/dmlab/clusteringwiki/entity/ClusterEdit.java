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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

/**
 * Cluster edit record
 * 
 * @author David C. Anastasiu
 *
 */
@Entity
@Table(name = "cluster_edits")
public class ClusterEdit implements Serializable {

	private static final long serialVersionUID = 4899612860910153283L;

	private Integer id;
	
	private Integer queryId;
	
	private Integer clusteringAlgo;
	
	private String path1 = "";
	
	private String path2 = "";
	
	private String path3 = "";
	
	private String path4 = "";
	
	private String path5 = "";

	private Integer cardinality;
	
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
	public void setId(Integer theId) {
		id = theId;
	}

	/**
	 * @return the queryId
	 */
	@Column(name = "query_id", nullable=false)
	public Integer getQueryId() {
		return queryId;
	}

	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(Integer theQueryId) {
		queryId = theQueryId;
	}
	
	/**
	 * @return the clusteringAlgo
	 */
	@Column(name = "clustering_algo", nullable=false)
	public Integer getClusteringAlgo() {
		return clusteringAlgo;
	}

	/**
	 * @param clusteringAlgo the clusteringAlgo to set
	 */
	public void setClusteringAlgo(Integer theClusteringAlgo) {
		clusteringAlgo = theClusteringAlgo;
	}

	/**
	 * @return the path1
	 */
	@Column(name = "path1", nullable=false, length=100)
	public String getPath1() {
		return path1;
	}

	/**
	 * @param path1 the path1 to set
	 */
	public void setPath1(String pathOne) {
		path1 = pathOne;
	}

	/**
	 * @return the path2
	 */
	@Column(name = "path2", length=100)
	public String getPath2() {
		return path2;
	}

	/**
	 * @param path2 the path2 to set
	 */
	public void setPath2(String pathTwo) {
		path2 = pathTwo;
	}

	/**
	 * @return the path3
	 */
	@Column(name = "path3", length=100)
	public String getPath3() {
		return path3;
	}

	/**
	 * @param path3 the path3 to set
	 */
	public void setPath3(String pathThree) {
		path3 = pathThree;
	}

	/**
	 * @return the path4
	 */
	@Column(name = "path4", length=100)
	public String getPath4() {
		return path4;
	}

	/**
	 * @param path4 the path4 to set
	 */
	public void setPath4(String pathFour) {
		path4 = pathFour;
	}

	/**
	 * @return the path5
	 */
	@Column(name = "path5", length=100)
	public String getPath5() {
		return path5;
	}

	/**
	 * @param path5 the path5 to set
	 */
	public void setPath5(String pathFive) {
		path5 = pathFive;
	}

	/**
	 * @return the cardinality
	 */
	@Column(name = "cardinality", nullable=false)
	public Integer getCardinality() {
		return cardinality;
	}

	/**
	 * @param support the cardinality to set
	 */
	public void setCardinality(Integer theCardinality) {
		cardinality = theCardinality;
	}

	
	public String toString(){
		String s = cardinality > 0 ? "+  " : "-  ";
		s += path1;
		if(!StringUtils.isEmpty(path2)) s += " >> " + path2;
		if(!StringUtils.isEmpty(path3)) s += " >> " + path3;
		if(!StringUtils.isEmpty(path4)) s += " >> " + path4;
		if(!StringUtils.isEmpty(path5)) s += " >> " + path5;
		return s + " :: " + queryId + ", " + clusteringAlgo + "\n";
	}
}
