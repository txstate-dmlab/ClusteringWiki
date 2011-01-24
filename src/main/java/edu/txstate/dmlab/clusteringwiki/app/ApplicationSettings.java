package edu.txstate.dmlab.clusteringwiki.app;

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

import edu.txstate.dmlab.clusteringwiki.cluster.ISimilarityCalculator;
import edu.txstate.dmlab.clusteringwiki.cluster.JaccardSimilarityCalculator;

/**
 * Application settings static context
 * 
 * @author David C. Anastasiu
 *
 */
public class ApplicationSettings {

	/**
	 * Whether timing is enabled
	 */
	private static boolean timingEnabled = false;
	
	/**
	 * Top k urls from query responses should be stored in db
	 * to be checked against when selecting appropriate 
	 * close enough queries (query transfer)
	 */
	private static int topKQueryUrls = 20;
	
	/**
	 * Similarity calculator - defaults to Jaccard
	 */
	private static ISimilarityCalculator similarityCalculator = new JaccardSimilarityCalculator();

	/**
	 * Max clustering iterations
	 */
	private static int maxClusteringIterations = 10;

	/**
	 * Whether to keep reverse term index
	 * Used for debuging purposes
	 */
	public static boolean KEEP_REVERSE_TERM_INDEX = false;
	
	/**
	 * Max field size in DB for a path field - used to determine max length of result label
	 * cluster label max size should also be set to max this value
	 * 
	 * This value should only be changed manually when db schema is altered
	 */
	private static final int MAX_PATH_FIELD_SIZE = 300;
	
	/**
	 * How many initial matching queries should be analyzed to find q' in
	 * the Trans algorithm
	 */
	private static int termSimQueryResultsLimit = 10;
	
	/**
	 * Terms similarity threshold to be used in the termSim function of the Trans algorithm
	 */
	private static double termSimThreshold = 0.5D;
	
	/**
	 * Result similarity threshold to be used in the resultSim function of the Trans algorithm
	 */
	private static double resultSimThreshold = 0.05D;
	
	
	/**
	 * characters used to separate tokens in a string
	 */
	public static final String TOKEN_CHARS = " \t\n\r\f:";
	
	/**
	 * The minimum length for a term
	 */
	public static final int MINIMUM_TERM_LENGTH = 3;
	
	/**
	 * the minimum length of a term phrase to be considered a phrase
	 */
	public static final int MINIMUM_PHRASE_LENGTH = 1;

	/**
	 * the minimum length for a frequent phrase to be considered
	 */
	public static final int MINIMUM_FREQUENT_PHRASE_LENGTH = 2;
	
	/**
	 * the maximum length for a frequent phrase to be considered
	 */
	public static final int MAXIMUM_FREQUENT_PHRASE_LENGTH = 5;
	
	/**
	 * the minimum cardinality for a frequent phrase to be considered
	 */
	public static final int MINIMUM_FREQUENT_PHRASE_CARDINALITY = 2;

	/**
	 * Number of documents that will be checked when
	 * looking for a term phrase interpretation as a word phrase
	 * If no label is found, another set of documents up to this 
	 * length is retrieved until all documents are exhausted or
	 * a label is found
	 */
	public static final int LABEL_SET_DOCUMENT_SIZE = 15;
	

	/**
	 * No instantiation allowed
	 */
	private ApplicationSettings(){
		
	}
	
	/**
	 * @return the timingEnabled
	 */
	public static boolean isTimingEnabled() {
		return timingEnabled;
	}
	
	public static void setTimingEnabled(String timingEnabled) {
		ApplicationSettings.timingEnabled = timingEnabled != null && timingEnabled.toLowerCase().equals("true");
	}

	/**
	 * @return the topKQueryUrls
	 */
	public static int getTopKQueryUrls() {
		return topKQueryUrls;
	}

	/**
	 * @param topKQueryUrls the topKQueryUrls to set
	 */
	public static void setTopKQueryUrls(int topKQueryUrls) {
		ApplicationSettings.topKQueryUrls = topKQueryUrls;
	}

	/**
	 * @return the similarityCalculator
	 */
	public static ISimilarityCalculator getSimilarityCalculator() {
		return similarityCalculator;
	}

	/**
	 * @param similarityCalculator the similarityCalculator to set
	 */
	public static void setSimilarityCalculator(
			ISimilarityCalculator similarityCalculator) {
		ApplicationSettings.similarityCalculator = similarityCalculator;
	}

	/**
	 * @return the maxClusteringIterations
	 */
	public static int getMaxClusteringIterations() {
		return maxClusteringIterations;
	}

	/**
	 * @param maxClusteringIterations the maxClusteringIterations to set
	 */
	public static void setMaxClusteringIterations(int maxClusteringIterations) {
		ApplicationSettings.maxClusteringIterations = maxClusteringIterations;
	}

	/**
	 * @return the maxpathfieldsize
	 */
	public static int getMaxPathFieldSize() {
		return MAX_PATH_FIELD_SIZE;
	}

	/**
	 * @return the termsimqueryresultslimit
	 */
	public static int getTermSimQueryResultsLimit() {
		return termSimQueryResultsLimit;
	}
	
	/**
	 * Setter for the termSimQueryResultsLimit
	 * @param limit
	 */
	public static void setTermSimQueryResultsLimit( int limit ){
		termSimQueryResultsLimit = limit;
	}

	/**
	 * @return the termsimthreshold
	 */
	public static double getTermSimThreshold() {
		return termSimThreshold;
	}

	/**
	 * Setter for the termSimThreshold
	 * @param t
	 */
	public static void setTermSimThreshold( double t ){
		termSimThreshold = t;
	}
	
	/**
	 * @return the resultsimthreshold
	 */
	public static double getResultSimThreshold() {
		return resultSimThreshold;
	}
	
	/**
	 * Setter for the resultSimThreshold
	 * @param t
	 */
	public static void setResultSimThreshold( double t ){
		resultSimThreshold = t;
	}
	
}
