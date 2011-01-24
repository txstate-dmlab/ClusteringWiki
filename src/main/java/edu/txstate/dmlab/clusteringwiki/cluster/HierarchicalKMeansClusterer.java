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

import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Hierarchical K-Means clustering implementation
 * 
 * @author David C. Anastasiu
 *
 */
public class HierarchicalKMeansClusterer extends KMeansClusterer implements
		IClusterer {

	/**
	 * Constructor, k is deduced
	 * @param context
	 */
	public HierarchicalKMeansClusterer(ICollectionContext context) {
		super(context);
	}
	
	/**
	 * Constructor, k is provided
	 * @param context
	 * @param k
	 */
	public HierarchicalKMeansClusterer(ICollectionContext context, int k) {
		super(context, k);
	}
	
	/**
	 * Whether a cluster should be split up into multiple cluster children
	 * @param c
	 * @return
	 */
	protected boolean shouldSubCluster(ICluster c){
		int level = c.getLevel();
		//sub-cluster if root or cluster has more than 10 docs and level is
		//below max clustering level
		return level == 0 || 
			(c.size() > 10 && level < MAX_CLUSTERING_LEVELS &&
					c.getParent().size() > c.size() //try to ensure no multiple single sublevel clusters
			);
	}
	
	/**
	 * perform hierarchical clustering
	 * @param c
	 * @return
	 */
	protected ICluster subCluster(ICluster c){
		List<ICluster> children = c.getChildren();
		if(children == null && shouldSubCluster(c)){
			int[] docs;
			if(c.getLevel() == 0){
				List<IClusterDocument> clusterDocs = c.getAllDocs(); 
				docs = new int[clusterDocs.size()];
				int i = 0;
				for(IClusterDocument doc : clusterDocs)
					docs[i++] = doc.getIndex();
			}
			else {
				Map<Integer, IClusterDocument> docsMap = c.getDocs();
				docs = new int[docsMap.size()];
				int i = 0;
				for(Integer index : docsMap.keySet())
					docs[i++] = index;
			}
			setDocumentsToCluster(docs);
			children = levelCluster(c, docs);
			c.setChildren(children);
		}
		if(children != null){
			for(ICluster child : children){
				if(shouldSubCluster(child))
					child = subCluster(child);
			}
		}
		return c;
	}
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix.  Provides a root cluster with an attached
	 * hierarchy (tree) of children clusters
	 * @param docs
	 * @return
	 */
	@Override
	public ICluster cluster(int[] docs){
		ICluster root = new KMeansCluster(getNextClusterId(), context);
		root.setLevel(0);
		root.deduceLabel();
		return subCluster(root);
	}

}
