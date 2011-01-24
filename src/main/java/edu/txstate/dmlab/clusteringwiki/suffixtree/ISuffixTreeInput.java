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
 * Allows abstracting input for a suffix tree of different types
 * (ex: int, char, object...) Each item can be represented by a numeric (int)
 * index within a global collection of items.
 * Additionally implements Callback and Visitor methods related to given 
 * input
 * 
 * @author David C. Anastasiu
 *
 */
public interface ISuffixTreeInput {

	/**
	 * Get the length (item count) of the input
	 * @return
	 */
	public int length();
	
	/**
	 * Get collection index for item at index i in the input.
	 * The input has a 0-based index.
	 * @param i
	 * @return
	 */
	public int get(int i);
	
	/**
	 * Get a part of the current input as a new input.
	 * @param start
	 * @param end
	 * @return
	 */
	public ISuffixTreeInput subInput(int start, int end);
	
	/**
	 * Get a part of the current input as a new input.
	 * @param start
	 * @return
	 */
	public ISuffixTreeInput subInput(int start);
	
	/**
     * Method called after a new state is created. Extending classes can
     * overwrite method to add content.
     */
    void nodeCreated(int nodeIndex, int position);
    
    /**
     * Method called by the suffix tree when iterating through the input list,
     * before the next element is accessed
     * @param pos
     */
    void next(int position);
    
    
}
