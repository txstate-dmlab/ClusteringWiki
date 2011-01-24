package edu.txstate.dmlab.clusteringwiki.cluster;

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

import java.util.Map;

import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult;

/**
 * Interface for a cluster document.  Represents the document
 * in relation to the original retrieved doc in ICWSearchResultCol,
 * connected by index
 * 
 * @author David C. Anastasiu
 *
 */
public interface IClusterDocument {

	/**
	 * Get all identified phrases in this document
	 * as arrays of allTermsIndex index ids
	 * @return
	 */
	public int[][] getTermPhrases();
	
	
	/**
	 * Get the String version of a phrase of terms given the start and
	 * end indexes in termList. Note string content is terms, not words.
	 * @param start
	 * @param end
	 * @return
	 */
	public String getTermPhraseString(int start, int end);
	
	/**
	 * Get the String version of a phrase of words given the start and
	 * end indexes in wordStrings
	 * @param start
	 * @param end
	 * @return
	 */
	public String getPhraseString(int start, int end);
	
	/**
	 * Find the start and end indexes in 
	 * termList for the given termPhrase
	 * @param phrase
	 * @return
	 */
	public int[] identifyTermPhraseBounds(int[] phrase);
	
	/**
	 * Find the start and end indexes in wordStrings
	 * for the given termPhrase. 
	 * @param termPhrase
	 * @return
	 */
	public int[] identifyWordPhraseBounds(int[] termPhrase);
	
	
	/**
	 * Get the String version of a phrase of words given an 
	 * equivalent termPhrase from this document
	 * @param termPhrase
	 * @return
	 */
	public String getWordPhraseString(int[] termPhrase);
	
	/**
	 * Add a term to the doc
	 * @param index Term index in allTermsIndex
	 * @param count Term count in this doc
	 * @param rebuild Whether to rebuild the terms, counts and weight arrays
	 */
	public void add(int index, int count, boolean rebuild);
	
	/**
	 * Remove a term from this document
	 * @param index Term index in allTermsIndex
	 * @param rebuild Whether to rebuild the terms, counts and weight arrays
	 */
	public void remove(int index, boolean rebuild);
	
	/**
	 * Remove all terms
	 */
	public void clear();
	
	/**
	 * Rebuild terms, counts, and weights arrays from termCounts map
	 * Used after adding a number of individual terms
	 */
	public void rebuildArrays();
	
	/**
	 * @return the index
	 */
	public int getIndex();

	/**
	 * @return the initial search result this document is based on
	 */
	public ICWSearchResult getResultDoc();
	
	/**
	 * Get number of terms in this document
	 * @return
	 */
	public int count();
	
	/**
	 * Get the array of allTermsIndex terms contained in this doc
	 * Note that allTermsIndex must be sorted before creation of 
	 * IClusterDocument docs, else intersection and union will
	 * fail
	 * @return the terms
	 */
	public int[] getTerms();

	/**
	 * Get term counts for the terms in this document
	 * Map keys are term index values from allTermsIndex
	 * @return term counts
	 */
	public int[] getTermCounts();
	
	/**
	 * Get term frequencies for the terms in this document
	 * Map keys are term index values from allTermsIndex
	 * @return term frequencies
	 */
	public double[] getTermWeights();
	
	/**
	 * @return the wordCountsIndex
	 */
	public Map<Integer, Integer> getWordCountsIndex();

	/**
	 * @return the termCountsIndex
	 */
	public Map<Integer, Integer> getTermCountsIndex();
	
	/**
	 * Get common terms between two docs
	 * @param d
	 * @return
	 */
	public int[] termsIntersection(IClusterDocument d);
	
	/**
	 * Get the number of common terms between two docs
	 * @param d
	 * @return
	 */
	public int termsIntersectionCount(IClusterDocument d);
	
	
	/**
	 * Get all distinct terms from the two docs
	 * @param d
	 * @return
	 */
	public int[] termsUnion(IClusterDocument d);
	
	/**
	 * Get the number of total distinct terms between two docs
	 * @param d
	 * @return
	 */
	public int termsUnionCount(IClusterDocument d);
	
	/**
	 * Compute similarity with another doc
	 * @param d
	 * @return
	 */
	public double computeSimilarity(IClusterDocument d);
	
	/**
	 * Get document's eucledian norm
	 * @return
	 */
	public double eucledianNorm();
	
	/**
	 * Get document's eucledian distance between two docs
	 * @return
	 */
	public double eucledianDistance(IClusterDocument d1, IClusterDocument d2);
	
	
	/**
	 * Eucledian distance between this doc and another doc
	 * @param d
	 * @return
	 */
	public double eucledianDistance(IClusterDocument d);
}
