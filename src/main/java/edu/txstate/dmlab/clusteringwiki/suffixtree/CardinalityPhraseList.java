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

/**
 * Phrase list that compares phrases based on cardinality, breaking on
 * phrase length
 * 
 * @author David C. Anastasiu
 *
 */
public class CardinalityPhraseList extends PhraseList {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -8730302986044053595L;

	public CardinalityPhraseList(PhraseList like) {
		super(like);
	}

	public CardinalityPhraseList(int theMaxSize, int theMinPhraseLength,
			int theMaxPhraseLength, int theMinCardinality) {
		super(theMaxSize, theMinPhraseLength, theMaxPhraseLength, theMinCardinality);
	}

	@Override
	protected boolean greaterThan(Phrase p1, Phrase p2) {
		if(p1.support == p2.support) return p1.length > p2.length;
        return p1.support > p2.support;
	}

	@Override
	public boolean shouldInsert(int length, int cardinality) {
		if( length < minPhraseLength || 
    		length > maxPhraseLength ||
    		cardinality < minCardinality )
    	return false;
	    	
	    return true;
	}

}
