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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SuffixTree input of int phrases which are associated with documents
 * 
 * @author David C. Anastasiu
 *
 */
public class IntPhraseSuffixTreeInput extends IntSuffixTreeInput implements
		ISuffixTreeInput, IPhraseInput {

	/**
	 * List of bounds within the input stream that repesent document bounds
	 * A bound is always right after the end of the document data (input[endChar+1])
	 */
	public final List<Integer> documentBounds;
	
	/**
	 * Mapping from document index to document Ids represented
	 * by those document indexes
	 * Documents are keps as indexes to allow for quick merging
	 * of document sets via methods in <code>OpenBitSet</code>
	 */
	public final List<Integer> documentIds;
	
	/**
	 * Mapping from node ids to document index the node belongs to
	 * Map is populated during tree construction via the 
	 * <code>nodeCreated</code> and <code>next</code> methods,
	 * which are callbacks from <code>SuffixTree</code>
	 */
	public final Map<Integer, Integer> nodeDocumentIdexes = new HashMap<Integer, Integer>();
	
	/**
	 * Used during building of the tree to match nodes with the document index
	 * of the document they belong to
	 */
	private int currentDocument = 0;
	
	/**
	 * Controller
	 * @param ib
	 */
	public IntPhraseSuffixTreeInput(IPhraseInputBuilder ib) {
		super(ib.getInput());
		documentBounds = ib.getDocumentBounds();
		documentIds = ib.getDocumentIds();
	}

	/**
	 * Get the document index associated with a given node in the 
	 * suffix tree
	 * @param nodeId
	 * @return
	 */
	public int getNodeDocumentIndex(int nodeId){
		return nodeDocumentIdexes.get(nodeId);
	}
	
	/**
	 * Get the document id associated with a given node in the 
	 * suffix tree
	 * @param nodeIndex
	 * @return
	 */
	public int getNodeDocumentId(int nodeIndex){
		return documentIds.get( nodeDocumentIdexes.get(nodeIndex) );
	}
	
	/**
     * Method called after a new state is created. Extending classes can
     * overwrite method to add content.
     */
    public void nodeCreated(int nodeIndex, int position){
    	nodeDocumentIdexes.put(nodeIndex, currentDocument);
    }
    
    /**
     * Method called by the suffix tree when iterating through the input list,
     * before the next element is accessed
     * @param pos
     */
    public void next(int position){ 
    	if (position == documentBounds.get(currentDocument))
    		currentDocument++;
    }
	
}
