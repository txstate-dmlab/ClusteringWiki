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
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import edu.txstate.dmlab.clusteringwiki.preprocess.CollectionContext;
import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Simple POJO to model a cluster. Contains various convenience methods
 * that are used by the clusterers. 
 * 
 * @author David C. Anastasiu
 *
 */
@XStreamAlias("cluster")
public class KMeansCluster extends BaseCluster implements ICluster, IKMeansCluster {


	/**
	 * Cluster centroid
	 */
	@XStreamOmitField
	@JsonIgnore
	private final ClusterDocument centroid;
	
	
	/**
	 * Clusters are initiated with references to the termDocumentMatrix,
	 * collection context, and a cluster id.  
	 * @param id termDocumentMatrix
	 * @param context
	 * @param matrix
	 */
	public KMeansCluster(String theId, ICollectionContext theContext){
		super(theId, theContext);
		centroid = new ClusterDocument(-1, null, (CollectionContext) context);
	}

	/**
	 * Clusters are initiated with references to the termDocumentMatrix,
	 * collection context, and a cluster id.  
	 * @param id termDocumentMatrix
	 * @param context
	 * @param matrix
	 * @param parent parent cluster, if any.  Null otherwise
	 * @param children children clusters, if any, Null otherwise.
	 */
	public KMeansCluster(String theId, ICollectionContext theContext, 
			ICluster theParent){
		super(theId, theContext, theParent);
		centroid = new ClusterDocument(-1, null, (CollectionContext) context);
	}
	
	/**
	 * Clusters are initiated with references to the termDocumentMatrix,
	 * collection context, and a cluster id.  
	 * @param id termDocumentMatrix
	 * @param context
	 * @param matrix
	 * @param parent parent cluster, if any.  Null otherwise
	 * @param children children clusters, if any, Null otherwise.
	 */
	public KMeansCluster(String theId, ICollectionContext theContext, 
			ICluster theParent, List<ICluster> theChildren){
		super(theId, theContext, theParent, theChildren);
		centroid = new ClusterDocument(-1, null, (CollectionContext) context);
	}
	
	
	/**
	 * Come up with a label
	 */
	public void deduceLabel(){
		if(level == 0) setLabel( ROOT_LABEL );
		
		if(docs != null && docs.size() > 0)
		try {
			IClusterDocument d = getClosestDocToCentroid();
			setLabel(d.getResultDoc().getTitle());
		} catch (Exception e) { /* do nothing */
			System.out.println(e.getMessage());
		}
		
	}
	
	/**
	 * @return the centroid
	 */
	@JsonIgnore
	public IClusterDocument getCentroid() {
		if(centroid == null) 
			computeCentroid();
		return centroid;
	}
	
	/**
	 * Compute centroid based on document vectors currently assigned
	 * to this cluster
	 */
	public void computeCentroid(){
		if (docs == null || docs.size() == 0) return;
		centroid.clear();
		//add all terms from all cluster documentsToCluster to the centroid
		for(int i : docs.keySet()){
			IClusterDocument d = docs.get(i);
			int[] terms = d.getTerms();
			int[] counts = d.getTermCounts();
			for(int j=0; j < terms.length; j++)
				centroid.add(terms[j], counts[j], false);
		}
		centroid.rebuildArrays();
	}

	/**
	* Returns the radius of the cluster. The radius is the average of the
	* square root of the sum of squares of its constituent document term
	* vector coordinates with that of the centroid.
	* @return the radius of the cluster.
	*/
	@JsonIgnore
	public double getRadius() {
		if(docs == null) return 0.0D;
		double radius = 0.0D;
		for(int i : docs.keySet()){
			IClusterDocument d = docs.get(i);
			radius += d.eucledianNorm();
		}
		return radius / docs.size();
	}

	/**
	* Returns the Eucledian distance between the centroid of this cluster
	* and the new document.
	* @param doc the document to be measured for distance.
	* @return the eucledian distance between the cluster centroid and the 
	* document.
	*/
	@JsonIgnore
	public double getEucledianDistance(IClusterDocument doc) {
		return centroid.eucledianDistance(doc);
	}

	/**
	* Returns the maximum distance from the specified document to any of
	* the documentsToCluster in the cluster.
	* @param doc the document to be measured for distance.
	* @return the complete linkage distance from the cluster.
	*/
	@JsonIgnore
	public double getCompleteLinkageDistance(IClusterDocument doc) {
		if (docs == null || docs.size() == 0) {
	      return 0.0D;
	    }
		double max = Double.MIN_VALUE;
		double distance;
		for(Map.Entry<Integer, IClusterDocument> entry : docs.entrySet()){
			IClusterDocument clusterDoc = entry.getValue();
			distance = doc.eucledianDistance(clusterDoc);
			if (Double.compare(distance, max) > 0) max = distance;
		}
		return max;
	}
	
	/**
	 * Get index of closest doc to centroid
	 * @return closest doc to centroid
	 */
	@JsonIgnore
	public IClusterDocument getClosestDocToCentroid(){
		if (docs == null || docs.size() == 0) {
	      throw new IllegalArgumentException("Cluster must have at least one document");
	    }
		IClusterDocument closest = null;
		double min = Double.MAX_VALUE;
		double distance;
		for(Map.Entry<Integer, IClusterDocument> entry : docs.entrySet()){
			IClusterDocument clusterDoc = entry.getValue();
			distance = centroid.eucledianDistance(clusterDoc);
			if (Double.compare(min, distance) > 0){
				min = distance;
				closest = entry.getValue();
			}
		}
		return closest;
	}

	/**
	* Returns the similarity between the centroid of this cluster
	* and the new document.  Centroid should never be null.
	* @param doc the document to be measured for similarity.
	* @return the similarity of the centroid of the cluster to the document.
	*/
	@JsonIgnore
	public double getSimilarity(IClusterDocument doc) {
		return centroid.computeSimilarity(doc);
	}

	
	/**
	* Returns the similarity between two documentsToCluster given the application settings
	* chosen similarity calculator
	* @param doc1
	* @param doc2 
	* @return the similarity between the two documentsToCluster.
	*/
	@JsonIgnore
	public double getSimilarity(IClusterDocument doc1, IClusterDocument doc2) {
		if (doc1 != null && doc2 != null) {
			return doc1.computeSimilarity(doc2);
		}
	    return 0.0D;
	}
	
}
