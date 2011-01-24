package edu.txstate.dmlab.clusteringwiki.suffixtree;

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
import java.util.Arrays;
import java.util.Stack;

import org.apache.lucene.util.OpenBitSet;


public abstract class GeneralizedSuffixTree extends SuffixTree {

	/**
	 * Path from the root (edges index ranges) when walking through the tree.
	 */
    protected final Stack<Integer> path = new Stack<Integer>();
    
    /**
     * Bitsets of document ids represented in each level
     */
    protected final ArrayList<OpenBitSet> bsets = new ArrayList<OpenBitSet>();
    
    /**
     * Minimum cardinality that should be considered when retrieving paths
     */
    protected int minCardinality = 1;
	
	public GeneralizedSuffixTree(ISuffixTreeInput input) {
		super(input);
	}

	public GeneralizedSuffixTree(ISuffixTreeInput input, int minCardinality){
		super(input);
		this.minCardinality = minCardinality;
	}
	
	/**
	 * processDocuments from the root on
	 */
	public void processDocuments()
    {
        // In a suffix tree without any documents there is nothing to do 
        if (isLeafNode(getRootNode()))
            return;

        processDocuments(0, getRootNode());
        path.clear();
        bsets.clear();
    }
	
	/**
	 * walk the tree and look for phrases that meet the given cardinality
	 * requirements 
	 * @param level
	 * @param state
	 */
	private void processDocuments(int level, int state)
    {
        assert !isLeafNode(state);

        final OpenBitSet me = getBitSet(level);
        for (int edge = firstEdge(state); edge != NO_EDGE; edge = nextEdge(edge))
        {
            final int childState = goToNode(edge);
            if (isLeafNode(childState))
            {
                final int documentIndex = ((IntPhraseSuffixTreeInput) input).getNodeDocumentIndex(childState);
                me.set(documentIndex);
            }
            else
            {
                final OpenBitSet child = getBitSet(level + 1);
                Arrays.fill(child.getBits(), 0);
                path.push( getSubphraseStart(edge) );
                path.push( getSubphraseEnd(edge) );
                processDocuments(level + 1, childState);
                path.pop();
                path.pop();
                me.or(child);
            }
        }

        if (getRootNode() != state)
        {
            final int card = (int) me.cardinality();
            if (card >= minCardinality)
            {
                processPhrase(state, card, me, path);
            }
        }
    }
	
	/**
	 * A phrase has been found that matches the given cardinality
	 * criteria. Do the necessary work related to the phrase
	 * @param state
	 * @param cardinality
	 * @param documents
	 * @param path
	 */
	protected abstract void processPhrase(int state, int cardinality, OpenBitSet documents, Stack<Integer> path);
	
	/**
	 * Internal method to initialize and retrieve OpenBitSets
	 * for any given level
	 * @param level
	 * @return
	 */
	private OpenBitSet getBitSet(int level) {
        while (bsets.size() <= level) bsets.add(new OpenBitSet());
        return bsets.get(level);
    }
	
}
