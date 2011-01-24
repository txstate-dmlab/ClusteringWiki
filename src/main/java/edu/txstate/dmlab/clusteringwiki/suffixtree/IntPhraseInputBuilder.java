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
import java.util.List;
import java.util.Stack;

import edu.txstate.dmlab.clusteringwiki.util.ArrayUtils;

public class IntPhraseInputBuilder implements IPhraseInputBuilder {

	private int phraseSeparator = -1;
	
	private final Stack<Integer> input = new Stack<Integer>();
	
	private final List<Integer> documentBounds = new ArrayList<Integer>();
	
	private final List<Integer> documentIds = new ArrayList<Integer>();
	
	@Override
	public void addPhrase(int[] terms, int start, int len) {
		for(int i=start; i < len && i < terms.length; i++)
    		input.add( terms[i] );
        input.push( phraseSeparator-- );
	}

	@Override
	public void addPhrase(int... terms) {
		addPhrase(terms, 0, terms.length);
	}

	@Override
	public void endDocument(int docId) {
		final int index = input.size();
		documentIds.add(docId);
		documentBounds.add( index );
	}

	@Override
	public List<Integer> getDocumentBounds() {
		return documentBounds;
	}

	@Override
	public int[] getInput() {
		return ArrayUtils.intColToArray(input);
	}

	@Override
    public List<Integer>  getDocumentIds(){
    	return documentIds;
    }


}
