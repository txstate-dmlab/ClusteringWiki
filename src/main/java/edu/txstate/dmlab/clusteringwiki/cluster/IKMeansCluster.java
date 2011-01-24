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

/**
 * Public interface defining a generic k-means cluster
 * 
 * @author David C. Anastasiu
 *
 */
public interface IKMeansCluster extends ICluster {

	
	/**
	 * @return the centroid
	 */
	public IClusterDocument getCentroid();

	/**
	 * Compute centroid based on document vectors currently assigned
	 * to this cluster
	 */
	public void computeCentroid();	
	
	/**
	* Returns the radius of the cluster. The radius is the average of the
	* square root of the sum of squares of its constituent document term
	* vector coordinates with that of the centroid.
	* @return the radius of the cluster.
	*/
	public double getRadius();


	/**
	* Returns the Eucledian distance between the centroid of this cluster
	* and the new document.
	* @param doc the document to be measured for distance.
	* @return the Eucledian distance between the cluster centroid and the 
	* document.
	*/
	public double getEucledianDistance(IClusterDocument doc);

	/**
	* Returns the maximum distance from the specified document to any of
	* the documentsToCluster in the cluster.
	* @param doc the document to be measured for distance.
	* @return the complete linkage distance from the cluster.
	*/
	public double getCompleteLinkageDistance(IClusterDocument doc);

	/**
	 * Get the closest doc to centroid
	 * @return closest doc to centroid
	 */
	public IClusterDocument getClosestDocToCentroid();
	
	/**
	* Returns the cosine similarity between the centroid of this cluster
	* and the new document.
	* @param doc the document to be measured for similarity.
	* @return the similarity of the centroid of the cluster to the document.
	*/
	public double getSimilarity(IClusterDocument doc);
	
	/**
	* Returns the cosine similarity between two documentsToCluster.
	* @param doc1
	* @param doc2 
	* @return the similarity between the two documentsToCluster.
	*/
	public double getSimilarity(IClusterDocument doc1, IClusterDocument doc2);
	
	
}
