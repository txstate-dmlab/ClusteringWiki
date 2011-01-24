package edu.txstate.dmlab.clusteringwiki.cluster;

import java.util.Collections;
import java.util.List;

import org.apache.lucene.util.PriorityQueue;

import edu.txstate.dmlab.clusteringwiki.util.NumberUtils;

/**
 * Order clusters in one level based on documents they contain
 * 
 * @author David C. Anastasiu
 *
 */
public class MinDocIndexClusterQueue extends PriorityQueue<ICluster> {

	public MinDocIndexClusterQueue(int size){
		super.initialize(size);
	}
	
	@Override
	protected boolean lessThan(ICluster a, ICluster b) {
		final List<Integer> docIdsA = a.getDocumentIds();
		Collections.sort(docIdsA);
		
		final List<Integer> docIdsB = b.getDocumentIds();
		Collections.sort(docIdsB);
		
		return NumberUtils.orderedLessThan(docIdsA, docIdsB);
	}

}
