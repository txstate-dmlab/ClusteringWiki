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

import org.apache.lucene.util.PriorityQueue;

/**
 * Priority queue to order frequent phrases found in SuffixTree
 * according to coverage set cardinality
 * 
 * @author David C. Anastasiu
 *
 */
public abstract class PhraseQueue extends PriorityQueue<Phrase> {
	
	 protected final int maxSize;
     
	 protected final int minCardinality;
     
     protected final int minPhraseLength;
     
     protected final int maxPhraseLength;
     
     /**
      * Create a new pq with the same parameters as an existing pq
      * @param like
      */
     public PhraseQueue(PhraseQueue like){
     	super.initialize(like.maxSize);
     	maxSize = like.maxSize;
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
     public PhraseQueue(int theMaxSize, int theMinPhraseLength, 
				int theMaxPhraseLength, int theMinCardinality)
     {
			super.initialize(theMaxSize);
     	maxSize = theMaxSize;
     	minPhraseLength = theMinPhraseLength;
     	maxPhraseLength = theMaxPhraseLength;
     	minCardinality = theMinCardinality;
     }
     
     @Override
     protected abstract boolean lessThan(Phrase p1, Phrase p2);
 
     /**
      * Return <code>true</code> if a cluster with <code>score</code> will be added to
      * the priority queue.
      */
     public abstract boolean shouldInsert(int length, int cardinality);
     
     public void insert(Phrase p){
    	if(shouldInsert(p.length, p.support))
    		insertWithOverflow(p);
     }
     
     public String toString(){
     	String s = "";
     	
     	if(size() > 0) {
     		Stack<Phrase> q = new Stack<Phrase>();
     		
         	while(size() > 0){
         		Phrase p = pop();
         		s += p + "\n";
         		q.add(p);
         	}
         	
         	while(q.size() > 0)
         		add(q.pop());

     	} else
     		s += "No phrases added.";
     	
			return s;
     }
     
}
