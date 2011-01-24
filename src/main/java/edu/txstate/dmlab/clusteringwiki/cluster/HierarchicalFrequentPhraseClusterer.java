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

import org.apache.commons.lang.StringUtils;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Hierarchical implementation of the frequent phrase clustering algorithm
 * 
 * @author David C. Anastasiu
 *
 */
public class HierarchicalFrequentPhraseClusterer extends FrequentPhraseClusterer implements IClusterer {
	
	/**
	 * Controller
	 * @param theContext
	 */
	public HierarchicalFrequentPhraseClusterer(ICollectionContext theContext) {
		super(theContext);
	}
	
	
	/**
	 * Whether a cluster should be split up into multiple cluster children
	 * @param c
	 * @return
	 */
	protected boolean shouldSubCluster(ICluster c){
		final int level = c.getLevel();
		final int[] terms = ((FrequentPhraseCluster) c).getLabelTerms();
		final int ln = terms != null ? terms.length : 0;
		//don't keep clustering the Other cluster
		if(StringUtils.equals(FrequentPhraseCluster.OTHER_LABEL, c.getLabel()) ) return false;
		//sub-cluster if root or cluster has more than 10 docs and level is
		//below max clustering level
		return level == 0 || ( c.size() > 5 && level < MAX_CLUSTERING_LEVELS
			&& ln >= ApplicationSettings.MINIMUM_FREQUENT_PHRASE_LENGTH);
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
			} else {
				Map<Integer, IClusterDocument> docsMap = c.getDocs();
				docs = new int[docsMap.size()];
				int i = 0;
				for(Integer index : docsMap.keySet())
					docs[i++] = index;
			}

			//only continue subclustering if there are at least 2 child 
			//clusters produced
			final List<ICluster> possibleChildren = levelCluster(c, docs);
			if(possibleChildren != null && (c.getLevel() == 0 || possibleChildren.size() > 1)) {
				children = possibleChildren;
			}
			c.setChildren(children);
		}
		if(children != null){
			for(ICluster child : children){
				if(shouldSubCluster(child)){
					ICluster possibleChild = subCluster(child);
					//only continue subclustering if there are at least 2 child 
					//clusters produced
					final List<ICluster> possibleChildren = possibleChild.getChildren();
					if(possibleChildren != null && possibleChildren.size() > 1)
						child = possibleChild;
				}
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
		ICluster root = new FrequentPhraseCluster(getNextClusterId(), context);
		root.setLevel(0);
		root.deduceLabel();
		return subCluster(root);
	}
}
