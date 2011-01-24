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

import java.util.List;
import java.util.Stack;

import org.apache.lucene.util.OpenBitSet;

public class Phrase implements IPhrase {

	/**
	 * State id for the phrase leaf state
	 */
	public final int state;
	
	/**
	 * Number of documents covered by this phrase
	 */
	public int support;
	
	/**
	 * Document indexes that this phrase is found in
	 */
	public final OpenBitSet documents;
	
	/**
	 * reference to the original set of documents being 
	 * clustered, used for matching document index to
	 * document id
	 */
	public final List<Integer> allDocIds;
	
	/**
	 * States path leading to this phrase leaf state
	 */
	public final Stack<Integer> path;
	
	/**
	 * terms included in this path
	 */
	public final int[] terms;
	
	/**
	 * Length of the terms array
	 */
	public final int length;
	
	@SuppressWarnings("unchecked")
	public Phrase(int theState, int support, OpenBitSet docs, 
			Stack<Integer> thePath, Stack<Integer> theTerms, List<Integer> docIds){
		this.state = theState;
		this.support = support;
		this.documents = (OpenBitSet) docs.clone();
		this.path = thePath != null ? (Stack<Integer>) thePath.clone() : new Stack<Integer>();
		this.terms = new int[theTerms.size()];
		int i = 0;
		for(int t : theTerms)
			this.terms[i++] = t;
		this.length = this.terms.length;
		this.allDocIds = docIds;
	}
	
	/**
	 * Get covered documents as a stack of integers
	 * @return
	 */
	public Stack<Integer> getDocumentsStack(){
		Stack<Integer> docs = new Stack<Integer>();
		if(documents != null)
			for(int i = 0; i < documents.size(); i++)
				if(documents.fastGet(i)) docs.add(i);
		return docs;
	}
	
	/**
	 * Get a copy of the documents bit set
	 * @return
	 */
	public OpenBitSet getDocumentsCopy(){
		return (OpenBitSet) this.documents.clone();
	}
	
	/**
	 * Check what the support would be if we removed given docs
	 * @param coveredDocuments
	 * @return
	 */
	public int supportAfterDocsRemoval(OpenBitSet coveredDocuments){
		OpenBitSet docs = (OpenBitSet) this.documents.clone();
		docs.remove(coveredDocuments);
		return (int) docs.cardinality();
	}
	
	/**
	 * Remove covered documents
	 * @param coveredDocuments
	 */
	public void removeDocuments(OpenBitSet coveredDocuments){
		this.documents.remove(coveredDocuments);
		this.support = (int) this.documents.cardinality();
	}
	
	/**
	 * @return the docIds
	 */
	public Stack<Integer> getDocIds() {
		Stack<Integer> docIds = new Stack<Integer>();
		if(documents != null)
			for(int i = 0; i < documents.size(); i++)
				if(documents.fastGet(i)) docIds.add(allDocIds.get(i));
		return docIds;
	}

	/**
	 * Get terms in this phrase
	 */
	public int[] getTerms(){
		return this.terms;
	}
	
	/**
	 * String representation of the phrase
	 */
	public String toString(){
		String s = terms + ", c:" + support + " l:" + length +
		", p:" + path + ", d:" + getDocumentsStack() + ", s:" + state;
		
		return s;
	}	
	
}
