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

import java.util.Collection;

/**
 * Suffix tree integer list input
 * 
 * @author David C. Anastasiu
 *
 */
public class IntSuffixTreeInput implements ISuffixTreeInput {
	
	/**
	 * array of indexes of items in this input list
	 */
	public final int[] input;
	
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
    public IntSuffixTreeInput(int[] input){
    	this.input = input;
    	this.start = 0;
    	this.length = input.length;
    }
    
    /**
     * Constructor
     * @param input
     */
    public IntSuffixTreeInput(Collection<Integer> input){
    	this.input = new int[input.size()];
    	int i = 0;
    	for(Integer j : input)
    		this.input[i++] = j;
    	this.start = 0;
    	this.length = input.size();
    }
    
    /**
     * Constructor allowing defining input list as a sublist of given
     * raw data (input)
     * @param input
     */
    public IntSuffixTreeInput(int[] input, int start, int length){
    	this.input = input;
    	this.start = start;
    	this.length = length;
    }
    
    /**
     * Constructor
     * @param input
     */
    public IntSuffixTreeInput(Collection<Integer> input, int start, int length){
    	this.input = new int[input.size()];
    	int i = 0;
    	for(Integer j : input)
    		this.input[i++] = j;
    	this.start = start;
    	this.length = length;
    }
    
    /**
	 * Get the length (item count) in the input list
	 * @return
	 */
	public int get(int i) {
		return this.input[start + i];
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
		int[] s = new int[end - start + 1];
		System.arraycopy(this.input, start, s, 0, end - start + 1);
		return new IntSuffixTreeInput(s);
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
