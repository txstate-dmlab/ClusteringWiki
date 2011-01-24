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
 * Suffix tree char list input
 * 
 * @author David C. Anastasiu
 *
 */
public class CharSuffixTreeInput implements ISuffixTreeInput {

	/**
	 * array of indexes of items in this input list
	 */
	public final char[] input;
	
	/**
	 * start input index where the IntSequence starts
	 */
    private final int start;
    
    /**
     * How long the input list is. input.length must be at least as
     * long as start + length
     */
    private final int length;
    
    /**
     * Constructor
     * @param input
     */
    public CharSuffixTreeInput(char[] theSeq){
    	this.input = theSeq;
    	this.start = 0;
    	this.length = theSeq.length;
    }
    
    /**
     * Constructor allowing defining input list as a sublist of given
     * raw data (input)
     * @param input
     */
    public CharSuffixTreeInput(char[] theSeq, int theStart, int theLength){
    	this.input = theSeq;
    	this.start = theStart;
    	this.length = theLength;
    }
    
    /**
     * Constructor
     * @param input
     */
    public CharSuffixTreeInput(String theSeq){
    	this.input = theSeq.toCharArray();
    	this.start = 0;
    	this.length = theSeq.length();
    }
    
    /**
     * Constructor allowing defining input list as a subsequence of given
     * raw data (input)
     * @param input
     */
    public CharSuffixTreeInput(String theSeq, int theStart, int theLength){
    	this.input = theSeq.toCharArray();
    	this.start = theStart;
    	this.length = theLength;
    }
    
    /**
	 * Get the length (item count) in the input list
	 * @return
	 */
	public int get(int i) {
		return (int) this.input[this.start + i];
	}

	/**
	 * Get collection index for item at index i in the input list.
	 * The input list has a 0-based index.
	 * @param i
	 * @return
	 */
	public int length() {
		return this.length;
	}

	/**
	 * Get a part of the current input list as a new input list.
	 * @param start
	 * @param end
	 * @return
	 */
	public ISuffixTreeInput subInput(int start, int end) {
		char[] s = new char[end - start + 1];
		System.arraycopy(this.input, start, s, 0, end - start + 1);
		return new CharSuffixTreeInput(s);
	}
	
	/**
	 * Get a part of the current input list as a new input list.
	 * @param start
	 * @return
	 */
	public ISuffixTreeInput subInput(int start){
		return this.subInput(start, this.length - 1);
	}

	/**
     * Method called after a new state is created. Extending classes can
     * overwrite method to add content.
     */
    public void nodeCreated(int nodeIndex, int position){ }
    
    /**
     * Method called by the suffix tree when iterating through the input list,
     * before the next element is accessed
     * @param pos
     */
    public void next(int position){ }
    
}
