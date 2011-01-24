package edu.txstate.dmlab.clusteringwiki.preprocess;

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
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.lucene.util.OpenBitSet;

import edu.txstate.dmlab.clusteringwiki.cluster.IClusterDocument;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResultCol;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult.FIELDS;

/**
 * Defines a context for information about the document 
 * collection being analyzed
 * 
 * @author David C. Anastasiu
 *
 */
public interface ICollectionContext {

	/**
	 * @return the search results docs
	 */
	public ICWSearchResultCol getDocs();

	/**
	 * @return the fields
	 */
	public FIELDS[] getFields();

	/**
	 * @return the query
	 */
	public String getQuery();
	
	/**
	 * @return the analyzedQuery
	 */
	public String getAnalyzedQuery();
	
	/**
	 * @return the queryTerms
	 */
	public int[] getQueryTerms();
	
	/**
	 * @return the processed list of cluster documentsToCluster
	 */
	public List<IClusterDocument> getAllDocs();

	/**
	 * A Suffix tree phrase is represented as an array of term ids
	 * which is a subphrase of a number of represented documents.
	 * Get the words label best represented within the first k
	 * documents the phrase covers.
	 * @param phraseDocs Stack of documents (indexes in docs array) covered by the phrase
	 * @param phraseTerms array of allTermsIndex indexes that form the phrase
	 */
	public String getPhraseLabel(final Stack<Integer> phraseDocs, 
			final int[] phraseTerms);
	
	/**
	 * @return the customResultLabels
	 */
	public Map<Integer, String> getCustomResultLabels();
	
	/**
	 * @return the resultLabelIndex
	 */
	public Map<String, Set<Integer>> getResultLabelIndex();
	
	/**
	 * Get a unique term id for a given term in the collection
	 * @param term
	 * @return
	 */
	public Integer getTermId(String term);
	
	/**
	 * Get a unique word id for a given word in the collection
	 * @param term
	 * @return
	 */
	public Integer getWordId(String word);
	
	/**
	 * Get documents bit set that contain all terms in a phrase
	 * @param terms
	 * @return
	 */
	public OpenBitSet getDocumentsContainingTerms(int[] terms);

	/**
	 * Number of documentsToCluster in the context
	 * @return
	 */
	public int countDocs();
	
	/**
	 * Result label is built from part of the title, part of the url, and a possible 
	 * prefix if result url/title combination is not unique in the result list
	 * @param d
	 * @return
	 */
	public String getResultLabel(IClusterDocument d);
}
