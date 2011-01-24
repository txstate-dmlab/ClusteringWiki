package edu.txstate.dmlab.clusteringwiki.util;

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


public class NumberUtils {

	private NumberUtils(){
		//no instantiation allowed
	}
	
	/**
	 * Combine two ints into a long
	 * @param a
	 * @param b
	 * @return
	 */
	public static long combineInts(int a, int b){
		return ((long) a) << 32 | (b & 0xffffffffL);
	}
	
	/**
	 * Retrieve the first (left) int from a two int long key
	 * @param a
	 * @return
	 */
	public static int LeftIntFromLong(long a){
		return (int) (a >>> 32);
	}
	
	/**
	 * Retrieve the second (right) int from a two int long key
	 * @param a
	 * @return
	 */
	public static int RightIntFromLong(long a){
		return (int) (a & 0xffffffffL);
	}
	
	/**
	 * Given two ordered lists of integer ids, 
	 * return true if doc A has the lowest starting integers
	 * lists are assumed to be ordered
	 * @param docIdsA
	 * @param docIdsB
	 * @return
	 */
	public static boolean orderedLessThan(List<Integer> docIdsA, List<Integer> docIdsB) {
		for(int i = 0; i < docIdsA.size() && i < docIdsB.size(); i++){
			if(docIdsA.get(i) > docIdsB.get(i))
				return false;
			else if(docIdsA.get(i) < docIdsB.get(i))
				return true;
		}
		return false;
	}
}
