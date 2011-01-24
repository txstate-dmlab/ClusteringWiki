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

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.OpenBitSet;

/**
 * List of Phrases able to greedily retrieve top quality phrase
 * and remove phrases no longer meeting coverage criteria
 * 
 * @author David C. Anastasiu
 *
 */
public abstract class PhraseList extends ArrayList<Phrase> {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 2391794554871731196L;

	protected final int initialSize;
	
	protected final int minCardinality;
	
	protected final int minPhraseLength;
	
	protected final int maxPhraseLength;
    
    /**
     * Create a new pq with the same parameters as an existing pq
     * @param like
     */
    public PhraseList(PhraseList like){
    	super(like.initialSize);
    	initialSize = like.initialSize;
    	minPhraseLength = like.minPhraseLength;
    	maxPhraseLength = like.maxPhraseLength;
    	minCardinality = like.minCardinality;
    }
    
    /**
     * Constructor
     * @param theMaxSize
     * @param theMinPhraseLength
     * @param theMaxPhraseLength
     * @param theMinCardinality
     */
    public PhraseList(int theMaxSize, int theMinPhraseLength, 
				int theMaxPhraseLength, int theMinCardinality) {
		super(theMaxSize);
    	initialSize = theMaxSize;
    	minPhraseLength = theMinPhraseLength;
    	maxPhraseLength = theMaxPhraseLength;
    	minCardinality = theMinCardinality;
    }
    
    /**
     * Decide whether a phrase is less important than another
     * @param p1
     * @param p2
     * @return
     */
    protected abstract boolean greaterThan(Phrase p1, Phrase p2);

    /**
     * Return <code>true</code> if a cluster with <code>score</code> will be added to
     * the priority queue.
     */
    public abstract boolean shouldInsert(int length, int cardinality);
    
    /**
     * Insert elem in the list
     * @param p
     */
    public void insert(Phrase p){
	   	if(shouldInsert(p.length, p.support))
	   		add(p);
    }
    
    /**
     * Get top phrase after removing covered docs
     * Phrases that no longer meet the necessary conditions to
     * be part of the list will be removed
     * Super-phrases of the chosen phrase will also be removed
     * @param coveredDocs
     * @return
     */
    public Phrase getTopPhrase(OpenBitSet coveredDocs){
    	if(this.size() == 0) return null;
    	Phrase p = null;
    	int index = -1;
    	//Greedily find the best phrase
    	for(int i = 0; i < this.size(); i++){
    		final Phrase p2 = this.get(i);
    		final int suport = p2.supportAfterDocsRemoval(coveredDocs);
    		if(!this.shouldInsert(p2.length, suport)){
    			this.remove(i);
    			i--;
    			continue;
    		}
    		if(p == null || this.greaterThan(p2, p)){
    			p = p2;
    			index = i;
    		}
    	}
    	if(index >= 0)
    		this.remove(index);
    	return p;
    }
    
    
    
    /**
     * String representation of the list
     */
    public String toString(){
    	String s = "  initialSize: " + initialSize + 
    		"\n  minCardinality: " + minCardinality +
    		"\n  minPhraseLength: " + minPhraseLength +
    		"\n  maxPhraseLength: " + maxPhraseLength + 
    		"\n  Elements: \n\n";
    	return s + StringUtils.join(this, "\n");
    }
    
}
