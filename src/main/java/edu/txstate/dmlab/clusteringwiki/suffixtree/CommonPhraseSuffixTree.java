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

public class CommonPhraseSuffixTree extends GeneralizedSuffixTree implements
		ISuffixTree {

	public final PhraseList phraseList;
	
	/**
	 * reference to the index of document ids for documents being clustered
	 */
	public final List<Integer> allDocumentIds;
	
	public CommonPhraseSuffixTree(ISuffixTreeInput input, int initNumPhrases,  
			int minPhraseLength, int maxPhraseLength, int minCardinality) {
		super(input, minCardinality);
		phraseList = new CardinalityPhraseList(initNumPhrases, minPhraseLength, 
				maxPhraseLength, minCardinality);
		allDocumentIds = ((IntPhraseSuffixTreeInput) input).documentIds;
		processDocuments();
	}

	@Override
	protected void processPhrase(int node, int cardinality,
			OpenBitSet documents, Stack<Integer> path) {
		final Stack<Integer> terms = new Stack<Integer>();
        for (int i = 0; i < path.size(); i += 2)
            for (int j = path.get(i); j <= path.get(i + 1); j++)
            	terms.add( input.get(j) );
        final Phrase p = new Phrase(node, cardinality, documents, path, terms, allDocumentIds);
        phraseList.insert(p);
	}

	
}
