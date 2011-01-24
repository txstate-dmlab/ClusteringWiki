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

import java.util.Stack;

import org.apache.lucene.util.OpenBitSet;

public interface IPhrase {

	/**
	 * Get covered documents as a stack of integers
	 * representing the indexes of the covered documents
	 * in the original set of documents
	 * @return
	 */
	public Stack<Integer> getDocumentsStack();
	
	/**
	 * Get a copy of the documents bit set
	 * @return
	 */
	public OpenBitSet getDocumentsCopy();
	
	/**
	 * Check what the support would be if we removed given docs
	 * @param coveredDocuments
	 * @return
	 */
	public int supportAfterDocsRemoval(OpenBitSet coveredDocuments);
	
	/**
	 * Remove covered documents
	 * @param coveredDocuments
	 */
	public void removeDocuments(OpenBitSet coveredDocuments);
	
	/**
	 * @return document ids for the covered documents
	 */
	public Stack<Integer> getDocIds();


	/**
	 * Get the terms representing this phrase
	 * @return
	 */
	public int[] getTerms();
	
}
