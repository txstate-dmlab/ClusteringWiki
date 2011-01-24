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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.txstate.dmlab.clusteringwiki.suffixtree.Phrase;

/**
 * Utility class for methods dealing with arrays
 * 
 * @author David C. Anastasiu
 *
 */
public class ArrayUtils {

	private ArrayUtils(){
		//no instantiation allowed
	}
	
	/**
	 * Create an int array from a collection of Integers
	 * @param l
	 * @return
	 */
	public static int[] intColToArray(Collection<Integer> l){
		int[] r = new int[l.size()];
		int i = 0;
		for(int e : l) r[i++] = e;
		return r;
	}
	
	/**
	 * Create a double array from a collection of Double
	 * @param l
	 * @return
	 */
	public static double[] doubleColToArray(Collection<Double> l){
		double[] r = new double[l.size()];
		int i = 0;
		for(double e : l) r[i++] = e;
		return r;
	}
	
	/**
	 * Check if given sub-array can be found in array at given index. It is assumed that
	 * arrays were checked and are not null 
	 * @param array
	 * @param subarray
	 * @param start
	 * @return
	 */
	public static boolean isSubarrayAtIndex(final int[] array, final int[] subarray, 
			final int start){
		final int ln = array.length;
		final int sln = subarray.length;
		if(sln + start > ln) return false;
		int j = 0;
		for (int i = start; i < start + sln; i++)
			if(subarray[j++] != array[i]) return false;
		return true;
	}
	
	/**
	 * Check whether match is a sub-phrase of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSubphraseOf(int[] match, int[] container){
		if(container.length < match.length) return false;
		for(int j = 0; j < container.length - match.length + 1; j++)
			if(isSubarrayAtIndex(container, match, j))
				return true;
		
		return false;
	}
	
	
	/**
	 * Check whether match is a super-phrase of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSuperphraseOf(int[] match, int[] container){

		if(container.length > match.length) return false;
		for(int j = 0; j < match.length - container.length + 1; j++)
			if(isSubarrayAtIndex(match, container, j))
				return true;
		
		return false;
	}
	
	/**
	 * Check whether match is a sub-phrase of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSubphraseOf(int[] match, int[][] container){
		if(container != null)
			for(int i = 0; i < container.length; i++)
				if(container[i] != null){
					if(container[i].length < match.length) continue;
					for(int j = 0; j < container[i].length - match.length + 1; j++)
						if(isSubarrayAtIndex(container[i], match, j))
							return true;
				}
		
		return false;
	}
	
	
	/**
	 * Check whether match is a super-phrase of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSuperphraseOf(int[] match, int[][] container){
		if(container != null)
			for(int i = 0; i < container.length; i++)
				if(container[i] != null){
					if(container[i].length > match.length) continue;
					for(int j = 0; j < match.length - container[i].length + 1; j++)
						if(isSubarrayAtIndex(match, container[i], j))
							return true;
				}
		
		return false;
	}
	

	/**
	 * Check whether match is a sub-phrase of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSubphraseOf(int[] match, List<Phrase> container){
		if(container != null)
			for(int i = 0; i < container.size(); i++) {
				final Phrase p = container.get(i);
				final int[] against = p.getTerms();
				if(against == null || against.length < match.length) continue;
				for(int j = 0; j < against.length - match.length + 1; j++)
					if(isSubarrayAtIndex(against, match, j))
						return true;
			}
		
		return false;
	}
	
	/**
	 * Check whether match is a super-phrase of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSuperphraseOf(int[] match, List<Phrase> container){
		for(int i = 0; i < container.size(); i++) {
			final Phrase p = container.get(i);
			final int[] against = p.getTerms();
			if(against == null || match.length > against.length) continue;
			for(int j = 0; j < match.length && j < against.length; j++)
				if(isSubarrayAtIndex(match, against, j))
					return true;
		}
		
		return false;
	}
	
	
	/**
	 * Check whether match is a sub-set of the phrase in
	 * the container.  Neither match or container are assumed to
	 * be sorted and they may contain duplicates.  We do not check for a
	 * proper subset (i.e. container has additional elements other than
	 * those found in match).  Returns true if match and container have
	 * the same set of elements.
	 * @param container
	 * @param match
	 * @return
	 */
	public static boolean isSubsetOf(int[] match, int[] container){
		final int cln = container.length;
		final int mln = match.length;
		final int[] mtch = Arrays.copyOf(match, mln);
		final int[] ctnr = Arrays.copyOf(container, cln);
		Arrays.sort(mtch);
		Arrays.sort(ctnr);
		int j = 0;
		int d, m; //index + matched item
		for(int i = 0; i < mln; i++){
			if(j >= cln) break;
			m = mtch[i];
			d = Arrays.binarySearch(ctnr, j, cln, m);
			if(d < 0) return false;
			j = d;
			while(i + 1 < mln && mtch[i + 1] == m)
				i++;
			while(j + 1 < cln && ctnr[j + 1] == m)
				j++;
		}
		
		return true;
	}
	
	
	/**
	 * Check whether match is a super-set of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSupersetOf(int[] match, int[] container){
		return isSubsetOf(container, match);
	}
	
	/**
	 * Check whether match is a sub-set of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSubsetOf(int[] match, int[][] container){
		if(container != null)
			for(int i = 0; i < container.length; i++)
				if(container[i] != null)
					if(isSubsetOf(match, container[i])) return true;
		
		return false;
	}
	
	
	/**
	 * Check whether match is a super-set of any of the phrases in
	 * the container
	 * @param container
	 * @param contained
	 * @return
	 */
	public static boolean isSupersetOf(int[] match, int[][] container){
		if(container != null)
			for(int i = 0; i < container.length; i++)
				if(container[i] != null)
					if(isSupersetOf(match, container[i])) return true;
		
		return false;
	}

	/**
	 * Fill given array with given repeated value
	 * @param <T>
	 * @param arr
	 * @param val
	 */
	public static <T> void fillArray(T[] arr, T val){
		if(arr != null)
			for(int i=0; i < arr.length; i++)
				arr[i] = val;
	}

	/**
	 * Fill given array with given repeated value
	 * @param arr
	 * @param val
	 */
	public static void fillArray(int[] arr, int val) {
		if(arr != null)
			for(int i=0; i < arr.length; i++)
				arr[i] = val;
	}
	
	/**
	 * Combine elements in a given two level array and an optional
	 * second array of terms, combine them all in a single array of terms
	 * @param arr
	 * @return
	 */
	public static int[] combine(int[][] arr, int[] terms){
		int c = 0;
		if(arr != null)
			for(int i = 0; i < arr.length; i++)
				c += arr[i].length;
		if(terms != null)
			c+= terms.length;
		int[] r = new int[c];
		c = 0;
		if(arr != null)
			for(int i = 0; i < arr.length; i++)
				for(int j = 0; j < arr[i].length; j++)
					r[c++] = arr[i][j];
		if(terms != null)
			for(int i=0; i < terms.length; i++)
				r[c++] = terms[i];
		return r;
	}

	/**
	 * Fill given array with given repeated value
	 * @param arr
	 * @param val
	 */
	public static void fillArray(double[] arr, double val) {
		if(arr != null)
			for(int i=0; i < arr.length; i++)
				arr[i] = val;
	}
	
	/**
	 * Present an int array as a comma separated list string
	 * @param arr
	 * @return
	 */
	public static String intArrayToString(int[] arr){
		String s = "";
		if(arr != null)
			for(int i=0; i < arr.length - 1; i++)
				s += arr[i] + ",";
		if(arr.length > 0) s += arr[arr.length - 1];
		
		return s;
	}
	
}
