package edu.txstate.dmlab.clusteringwiki.dao;

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

import java.util.List;

import edu.txstate.dmlab.clusteringwiki.entity.ClusterEdit;

/**
 * Data access object public interface for a cluster edit record
 * 
 * @author David C. Anastasiu
 *
 */
public interface IClusterEditDao {
	public void saveClusterEdit(ClusterEdit ce);
	
	public ClusterEdit selectClusterEditById(Integer id);
	
	public ClusterEdit selectClusterEditByPath(Integer queryId, Integer clusteringAlgo,
			String path1, String path2, String path3, String path4, String path5);
	
	/**
	 * Insert, update or delete a path in order to maintain set of paths in database
	 * for given query id and clustering algorithm
	 * @param queryId
	 * @param clusteringAlgo
	 * @param path1
	 * @param path2
	 * @param path3
	 * @param path4
	 * @param cardinality
	 * @throws Exception
	 */
	public void updatePath(Integer queryId, Integer clusteringAlgo,
			String path1, String path2, String path3, String path4, String path5, 
			Integer cardinality) 
	throws Exception;
	
	/**
	 * Select edits for a given query.  Edits are selected from cluster_edits or
	 * cluster_edits_all if userAll is true
	 * @param queryId Query id edits are associated with
	 * @param clusteringAlgo clustering algorithm for the edits
	 * @param userAll Whether query is associated with user "all"
	 * @return
	 */
	public List<ClusterEdit> selectClusterEditsForUserQuery(Integer queryId,
			Integer clusteringAlgo, boolean userAll);
	
	public void deleteClusterEditsForUserQuery(Integer queryId,
			Integer clusteringAlgo);
	
	public void deleteClusterEdit(ClusterEdit ce);
}
