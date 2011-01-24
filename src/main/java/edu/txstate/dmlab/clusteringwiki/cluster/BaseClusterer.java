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

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Base data and methods needed by all Clusterers
 * 
 * @author David C. Anastasiu
 *
 */
public abstract class BaseClusterer implements IClusterer {

	public static final int ROOT_CLUSTER_ID = 0;
	
	protected int nextClusterId = ROOT_CLUSTER_ID;
	
	public static final int MAX_CLUSTERING_LEVELS = 4;
	
	
	/**
	 * Reference to the processed documentsToCluster from the context
	 */
	protected final List<IClusterDocument> allDocs;
	
	/**
	 * reference to the context data
	 */
	protected final ICollectionContext context;
	
	/**
	 * Set of documentsToCluster to be clustered
	 */
	protected int[] documentsToCluster;
	
	/**
	 * Maximum number of iterations that should be performed
	 */
	protected int maxIterations = ApplicationSettings.getMaxClusteringIterations();
	
	
	/**
	 * @return the documentsToCluster
	 */
	public int[] getDocumentsToCluster() {
		return documentsToCluster;
	}

	/**
	 * @param documentsToCluster the documentsToCluster to set
	 */
	public void setDocumentsToCluster(int[] documents) {
		documentsToCluster = documents;
	}

	/**
	 * @return the maxIterations
	 */
	public int getMaxItterations() {
		return maxIterations;
	}

	/**
	 * @param maxIterations the maxIterations to set
	 */
	public void setMaxItterations(int maxItt) {
		maxIterations = maxItt;
	}

	public BaseClusterer(ICollectionContext theContext){
		allDocs = theContext.getAllDocs();
		context = theContext;
	}
	
	/**
	 * sequential cluster id provider method
	 * @return
	 */
	protected synchronized String getNextClusterId(){
		int id = nextClusterId++;
		return String.valueOf( id );
	}
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix.  Provides a root cluster with an attached
	 * List<ICluster> of 1st level children clusters.
	 * @param docs
	 * @return
	 */
	public ICluster cluster(int[] docs){
		ICluster root = new BaseCluster(getNextClusterId(), context);
		root.setLevel(0);
		root.deduceLabel();
		root.setChildren(levelCluster(root, docs));
		return root;
	}

	/**
	 * cluster all 
	 * @return
	 */
	public ICluster cluster(){
		
		if(allDocs == null){
			return new BaseCluster(getNextClusterId(), context);
		}
		final int numDocs = allDocs.size();
		
		int[] docs = new int[numDocs];
		for(int i=0; i< numDocs; i++)
			docs[i] = i;
		return cluster(docs);
		
	}
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix.
	 * @param parent cluster node
	 * @param docs
	 * @return
	 */
	@Override
	public abstract List<ICluster> levelCluster(ICluster parent, int[] docs);

	/**
	 * cluster all 
	 * @param parent cluster node
	 * @return
	 */
	@Override
	public List<ICluster> levelCluster(ICluster parent){
		if(parent == null) return levelCluster(null, null);
		List<IClusterDocument> clusterDocs = parent.getDocuments();
		//if parent does not have documents assigned cluster based on
		//the entire context document collection
		if(clusterDocs == null){
			if(allDocs == null)
				return null;
			int numDocs = allDocs.size();
			
			int[] docs = new int[numDocs];
			for(int i=0; i< numDocs; i++)
				docs[i] = i;
			return levelCluster(parent, docs);
		}
		//if parent has documents assigned cluster based on the parent's
		//document collection
		int[] docs = new int[clusterDocs.size()];
		int i = 0;
		for(IClusterDocument doc : clusterDocs)
			docs[i++] = doc.getIndex();
		return levelCluster(parent, docs);
	}
	
	/**
	 * cluster all with no parent
	 * @param parent cluster node
	 * @return
	 */
	public List<ICluster> levelCluster(){
		return levelCluster(null, null);
	}
	
}
