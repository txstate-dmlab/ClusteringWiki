package edu.txstate.dmlab.clusteringwiki.cluster;

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

/**
 * Public interface for a clustering algorithm
 * 
 * @author David C. Anastasiu
 *
 */
public interface IClusterer {
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix.  Provides a root cluster with an attached
	 * List<ICluster> of 1st level children clusters.
	 * @param docs
	 * @return
	 */
	public ICluster cluster(int[] docs);

	/**
	 * cluster all 
	 * @return
	 */
	public ICluster cluster();
	
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix.
	 * @param parent cluster node
	 * @param docs
	 * @return
	 */
	public List<ICluster> levelCluster(ICluster parent, int[] docs);

	/**
	 * cluster all 
	 * @param parent cluster node
	 * @return
	 */
	public List<ICluster> levelCluster(ICluster parent);
	
	
	/**
	 * cluster all with no parent
	 * @param parent cluster node
	 * @return
	 */
	public List<ICluster> levelCluster();
	
	
}
