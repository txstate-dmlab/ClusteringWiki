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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Clusters a collection of documentsToCluster using K-Means clustering algorithm.
 * 
 * @author David C. Anastasiu
 *
 */
public class KMeansClusterer extends BaseClusterer implements IClusterer {

	/**
	 * Number of clusters to be created
	 */
	protected int numClusters = 0;
	
	
	/**
	 * Constructor
	 * @param context
	 */
	public KMeansClusterer(ICollectionContext context) {
		super(context);
	}

	/**
	 * Constructor providing number of clusters to build
	 * @param context
	 * @param k
	 */
	public KMeansClusterer(ICollectionContext context, int k) {
		super(context);
		numClusters = k;
	}
	
	
	
	/**
	 * @return the numClusters
	 */
	public int getNumClusters() {
		return numClusters;
	}

	/**
	 * @param numberClusters the numClusters to set
	 */
	public void setNumClusters(int numberClusters) {
		numClusters = numberClusters;
	}
	
	/**
	 * Set number of clusters based on document collection to be clustered
	 * Very simple approach - try to get an average of 10 results per cluster
	 */
	public void setNumClusters() {
		if(documentsToCluster != null){
			int n = documentsToCluster.length;
			numClusters = (int) Math.ceil( Math.sqrt( n ) );
		}
	}
	
	/**
	 * Choose initial seeds.  Should be called after setting
	 * numClusters and documentsToCluster.
	 * @return array of initial centroid/seed vectors
	 */
	protected List<IClusterDocument> chooseSeeds(String parentId){
		if(documentsToCluster == null || documentsToCluster.length == 0)
			throw new IllegalArgumentException("Documents cannot be empty");
		if(numClusters < 0 || numClusters > documentsToCluster.length)
			throw new IllegalArgumentException("Invalid number of clusters");
		
		//prepare container for the seeds
		List<IClusterDocument> seeds = new ArrayList<IClusterDocument>(numClusters);
		//first order the docs by URL
		Map<String, Integer> orderedDocs = new TreeMap<String, Integer>();
		for(int i : documentsToCluster){
			IClusterDocument d = allDocs.get(i);
			String key = StringUtils.abbreviate(d.getResultDoc().getUrl(), 20)
				+ d.getIndex();
			orderedDocs.put(key, d.getIndex());
		}
		Integer[] orderedDocIds = new Integer[orderedDocs.size()];
		orderedDocs.values().toArray(orderedDocIds);

		//then choose distributed values
		List<Integer> chosen =  new ArrayList<Integer>();
		int i = 1;
		int n = orderedDocs.size();
		if(n > 1) {
			//choose a
			int a = n / 10 - 1;
			while (a < 0 || a%n == 0) a++;
			//choose cluster seeds
			while(chosen.size() < numClusters){
				int c = (a * i) % n;
				if(!chosen.contains(c))
					chosen.add(c);
				i++;
			}
		} else {
			chosen.add(0);
		}
		
		
		Integer[] chosenSeeds = new Integer[chosen.size()];
		int m = 0;
		//retrieve the seeds according to the computed distribution
		for(int c : chosen){
			seeds.add( allDocs.get( orderedDocIds[c] ) );
			chosenSeeds[m++] = orderedDocIds[c];
		}
		
		return seeds;
	}
	
	
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix
	 * @param docs
	 * @return
	 */
	public List<ICluster> levelCluster(ICluster parent, int[] docs) {
		if(docs == null){ //cluster all docs
			return levelCluster(parent);
		}
		documentsToCluster = docs;

		setNumClusters();
		//choose initial cluster seeds
		String parentId = parent.getId();
		List<IClusterDocument> seeds = chooseSeeds(parentId);
		//create clusters and assign initial centroids/seeds
		List<ICluster> clusters = new ArrayList<ICluster>();
		for (int i = 0; i < numClusters; i++) {
			KMeansCluster cluster = new KMeansCluster(getNextClusterId(), context, parent);
			clusters.add(cluster);
		}
		//initial cluster assignments
		int numDocs = docs.length;
		int bestCluster;
		int currentCluster;
		double similarity;
		double maxSimilarity;
		// For every document d, find the cluster j whose centroid is 
		// most similar, assign d to cluster j.
		for (int i = 0; i < numDocs; i++) {
			bestCluster = 0;
			maxSimilarity = Double.MIN_VALUE;
			IClusterDocument d = allDocs.get(docs[i]);
			for (int j = 0; j < numClusters; j++) {
				similarity = seeds.get(j).computeSimilarity(d);
				if(Double.compare(similarity, maxSimilarity) > 0){
					bestCluster = j;
					maxSimilarity = similarity;
				}
			}
			for (ICluster cluster : clusters)
				cluster.removeDocument(docs[i]);
			clusters.get(bestCluster).addDocument(docs[i]);
		}
		
		// Repeat until termination conditions are satisfied
		int iteration = 0;
	    for (;;) {
	    	// For every cluster, re-compute the centroid based on the
	        // current member documentsToCluster.
	    	for (int j = 0; j < numClusters; j++) {
	    		((KMeansCluster) clusters.get(j)).computeCentroid();
	    	}
	    	// For every document d, find the cluster i whose centroid is 
			// most similar, assign d to cluster i. (If a document is 
			// equally similar from all centroids, then just dump it into 
			// cluster 0).
	    	int numChanged = 0;
			for (int i = 0; i < numDocs; i++) {
				bestCluster = 0;
				currentCluster = -1;
				maxSimilarity = Double.MIN_VALUE;
				IClusterDocument d = allDocs.get(docs[i]);
				for (int j = 0; j < numClusters; j++) {
					similarity = ((KMeansCluster) clusters.get(j)).getSimilarity(d);
					if(Double.compare(similarity, maxSimilarity) > 0){
						bestCluster = j;
						maxSimilarity = similarity;
					}
					if(clusters.get(j).contains(docs[i]))
						currentCluster = j;
				}
				//if another cluster is better
				if(bestCluster != currentCluster){
					clusters.get(currentCluster).removeDocument(docs[i]);
					clusters.get(bestCluster).addDocument(docs[i]);
					numChanged++;
				}
			}
			iteration++;
			if(iteration > maxIterations || numChanged == 0)
				break;
	    }
	    
	    //set cluster labels
	    for (int j = 0; j < numClusters; j++) {
	    	ICluster c = clusters.get(j);
	    	c.deduceLabel();
	    	if(c.getLabel().equals("") && c.getDocuments() == null) {
	    		//if empty cluster, trim
	    		clusters.remove(j);
	    		j--;
	    		numClusters--;
	    	}
	    }
	    
	    return clusters;
		
	}
	
}
