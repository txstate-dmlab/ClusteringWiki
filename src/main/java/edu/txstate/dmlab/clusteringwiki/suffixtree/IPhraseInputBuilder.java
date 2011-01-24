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

/**
 * Allows building an intput stream of mutiple phrases in one or more documents
 * to be added to suffix tree
 * 
 * @author David C. Anastasiu
 *
 */
public interface IPhraseInputBuilder {

	/**
     * Add a phrase to be considered
     */
    public void addPhrase(int [] terms, int start, int len);

    /**
     * Add a phrase to be considered
     */
    public void addPhrase(int... terms);
    
    /**
     * End a document and record its unique id
     */
    public void endDocument(int docId);
    
    /**
     * Retrieve input as integer array
     * @return input
     */
    public int[] getInput();
    
    /**
     * Get list of indexes within input that represent document bounds
     * (input[x] is actually a separator if x is a phrase bound, or 
     * does not exist if x == input.length.  phrase ends at index x-1).
     * @return
     */
    public List<Integer> getDocumentBounds();
    
    /**
     * Get list of document ids represented by given
     * document
     * Matches document index to document id being represented
     * @return
     */
    public List<Integer> getDocumentIds();
    
}
