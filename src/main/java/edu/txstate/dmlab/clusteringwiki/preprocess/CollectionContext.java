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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.lucene.util.OpenBitSet;
import org.springframework.util.StringUtils;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.cluster.ClusterDocument;
import edu.txstate.dmlab.clusteringwiki.cluster.IClusterDocument;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResultCol;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult.FIELDS;
import edu.txstate.dmlab.clusteringwiki.util.CharUtils;
import edu.txstate.dmlab.clusteringwiki.util.TokenUtils;
import edu.txstate.dmlab.clusteringwiki.util.ArrayUtils;

/**
 * Defines a context for information about the document 
 * collection being analyzed.  It encapsulates methods to
 * analyze a collection of documentsToCluster as well as retrieve
 * specific information about them or the collection needed
 * by other methods.
 * 
 * @author David C. Anastasiu
 *
 */
public class CollectionContext implements ICollectionContext {

	/**
	 * Initially received document collection
	 */
	public final ICWSearchResultCol docs;
	
	/**
	 * Cache for number of documents to be processed
	 */
	public final int docsLength;
	
	/**
	 * Fields in ICWSearchResult that comprise the document
	 * and should be analyzed.  Note that the "document" filed in
	 * the ICWSearchResult will generally by dynamically retrieved on
	 * first call and can cause app slowness
	 */
	public final FIELDS[] fields;
	
	/**
	 * Query that should be used when performing query related
	 * analysis
	 */
	public final String query;
	
	/**
	 * Query after it has been stemmed
	 */
	public String analyzedQuery;
	
	/**
	 * Analyzed query as array of terms
	 */
	public final int[] queryTerms;

	/**
	 * Map for quickly finding the numeric id of a string version of a word.
	 */
	public final Map<String, Integer> allWordsIndex = new HashMap<String, Integer>();
	
	/**
	 * Cache for how many words are stored in the collection
	 */
	public final int allWordsLength;
		
	/**
	 * Map for quickly finding the numeric id of a string version of a term.
	 */
	public final Map<String, Integer> allTermsIndex = new HashMap<String, Integer>();
	
	/**
	 * Cache for how many terms there are in the collection
	 */
	public final int allTermsLength;
	
	/**
	 * Map for all term ids matched back to the term given this id
	 */
	public final List<String> allTermsReverseIndex = new ArrayList<String>();
	
	/**
	 * Keep track of document indexes that term is found in
	 */
	public final List<OpenBitSet> allTermsDocumentIndexes = new ArrayList<OpenBitSet>();
	
	/**
	 * Processed documentsToCluster that should be clustered
	 */
	public final List<IClusterDocument> allDocs;
	
	/**
	 * Some results have the same URL + title, causing non-result set duplicate paths problems
	 * customResultLabels identifies duplicates and adds a prefix for copies of result labels
	 */
	protected final Map<Integer, String> customResultLabels = new HashMap<Integer, String>();
	
	/**
	 * Index of result nodes, key = result label, value = page indexes in allDocs
	 */
	protected final Map<String, Set<Integer>> resultLabelIndex = new HashMap<String, Set<Integer>>();
	
	/**
	 * Max path field size in db
	 * @see ApplicationSettings
	 */
	protected final int maxPathFieldSize = ApplicationSettings.getMaxPathFieldSize();
		
	/**
	 * Ordered set of all noun phrases encountered in the document collection
	 * Provides an index for each phrase
	 */
	protected final List<String> allNounPhrases = new ArrayList<String>(0);
	
	/**
	 * Reverse lookup index for noun phrases
	 */
	protected final Map<String, Integer> nounPhraseIndex = new HashMap<String, Integer>(0);
	
	/**
	 * Index for what documents a phrase in found in
	 */
	protected final Map<Integer, Set<Integer>> nounPhraseDocIndex = new HashMap<Integer, Set<Integer>>(0);
	
	
	/**
	 * @return the search returned docs
	 */
	public ICWSearchResultCol getDocs() {
		return this.docs;
	}

	/**
	 * @return the fields
	 */
	public FIELDS[] getFields() {
		return this.fields;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return this.query;
	}

	/**
	 * @return the analyzedQuery
	 */
	public String getAnalyzedQuery() {
		return this.analyzedQuery;
	}

	/**
	 * @return the queryTerms
	 */
	public int[] getQueryTerms() {
		return queryTerms;
	}

	/**
	 * @return the processed list of cluster documentsToCluster
	 */
	public List<IClusterDocument> getAllDocs() {
		return this.allDocs;
	}

	/**
	 * @return the customResultLabels
	 */
	public Map<Integer, String> getCustomResultLabels() {
		return this.customResultLabels;
	}

	/**
	 * @return the resultLabelIndex
	 */
	public Map<String, Set<Integer>> getResultLabelIndex() {
		return this.resultLabelIndex;
	}

	/**
	 * Initialize the document collection context by providing
	 * the documentsToCluster to be analyzed, a list of fields within
	 * those documentsToCluster to be analyzed, 
	 * and the query that was posed on this collection
	 * @param docs Collection of received documentsToCluster
	 * @param fields Array of ICWSearchResult document fields to analyze 
	 * @param query String of executed query 
	 */
	public CollectionContext (ICWSearchResultCol theDocs, FIELDS[] theFields,
		String theQuery){
		this.docs = theDocs;
		this.fields = theFields;
		this.query = theQuery;
		
		//analyze query
		this.queryTerms = analyzeQuery(theQuery);
		
		//get results from collection
		ICWSearchResult[] results = this.docs.getResults();
		
		if(results == null || results.length == 0)
			throw new IllegalArgumentException("No results received");
		
		this.docsLength = results.length;
		this.allDocs = new ArrayList<IClusterDocument>(docsLength);
		
		//for each retrieved document, build document to cluster, which also
		//does initial processing of text. also identify any duplicate result 
		//labels and tag them as such
		for (int i = 0; i < this.docsLength; i++) {
			IClusterDocument doc = new ClusterDocument(i, results[i], this);
			//add reference to document
			this.allDocs.add(doc);
			
			//register terms in doc with the allTermsDocumentIndexes index
			final int[] terms = doc.getTerms();
			for(int t : terms)
				this.setTermDocumentIndex(t, i);
			
			//build result label index and customResultLabels
			String key = getResultLabel(doc);
			Set<Integer> p = this.resultLabelIndex.get(key);
			if(p == null) {
				p = new HashSet<Integer>();
			} else {
				// register duplicate result label
				this.customResultLabels.put(i, p.size() + "-");
				key = getResultLabel(doc);
				p = this.resultLabelIndex.get(key);
				if(p == null)
					p = new HashSet<Integer>();
			}
			p.add(i);
			this.resultLabelIndex.put(key, p);
			
		}
		
		this.allWordsLength = this.allWordsIndex.size();
		this.allTermsLength = this.allTermsIndex.size();
		
	}
	
	/**
	 * Analyzes query and created queryTerms array, then
	 * returns analyzed query string
	 * @param query
	 * @return
	 */
	public int[] analyzeQuery(String query){
		StringTokenizer st = new StringTokenizer(query, ApplicationSettings.TOKEN_CHARS, true);
		List<Integer> qTerms = new ArrayList<Integer>();
		List<String> qTermStrings = new ArrayList<String>();
		while(st.hasMoreTokens()){
			String tok = st.nextToken();
			
			if(!tok.equals(" ") && tok.length() > 0){
				while(CharUtils.startsWithPhraseSeparator(tok))
					tok = tok.substring(1);
				while(CharUtils.endsWithPhraseSeparator(tok))
					tok = tok.substring(0, tok.length() - 1);
				
				final short tt = TokenUtils.getFullTokenType(tok);
				final boolean isStopWord = TokenUtils.isStopWord(tt);
				final boolean isPunctuation = TokenUtils.isPunctuation(tt);
				if(!isStopWord && !isPunctuation) {
					final String tm = edu.txstate.dmlab.clusteringwiki.util.StringUtils.stemWord(tok);
					if(tm.length() >= ApplicationSettings.MINIMUM_TERM_LENGTH){
						final int tid = this.getTermId(tm); // add token
						this.getWordId(tok); //add word to allWordsIndex
						qTerms.add(tid);
						qTermStrings.add(tm);
					} else {
						this.getWordId(tok);
					}
				}
			}
		}
		
		this.analyzedQuery = StringUtils.collectionToDelimitedString(qTermStrings, " ");
		
		int[] terms = ArrayUtils.intColToArray(qTerms);
		
		return terms;
		
	}
	
	
	/**
	 * A Suffix tree phrase is represented as an array of term ids
	 * which is a subphrase of a number of represented documents.
	 * Get the words label best represented within the first k
	 * documents the phrase covers.
	 * @param phraseDocs Stack of documents (indexes in allDocs array) covered by the phrase
	 * @param phraseTerms array of allTermsIndex indexes that form the phrase
	 */
	public String getPhraseLabel(final Stack<Integer> phraseDocs, 
			final int[] phraseTerms){
		final Map<String, Set<Integer>> index = new HashMap<String, Set<Integer>>();
		int i = 0;
		for(int phraseDoc : phraseDocs){
			IClusterDocument d = this.allDocs.get(phraseDoc);
			if(d == null) continue;
			final String l = d.getWordPhraseString(phraseTerms);
			if(l == null) continue;
			Set<Integer> cover = index.get(l);
			if(cover == null) cover = new HashSet<Integer>();
			cover.add(phraseDoc);
			index.put(l, cover);
			i++;
			if(i + 1 % ApplicationSettings.LABEL_SET_DOCUMENT_SIZE == 0 && index.size() > 0) break;
		}
		if(index.size() > 0){
			int coverSize = 0;
			String chosen = "";
			for(String s : index.keySet()){
				final int compSize = index.get(s).size();
				if(compSize > coverSize){
					chosen = s;
					coverSize = compSize;
				}
			}
			return chosen;
		}
		return "Unrepresented label";
	}
	
	/**
	 * Register that a term exists in a given document in the 
	 * allTermsDocumentIndexes array
	 * @param termIndex
	 * @param docIndex
	 */
	protected void setTermDocumentIndex(int termIndex, int docIndex){
		while(this.allTermsDocumentIndexes.size() < termIndex + 1)
			this.allTermsDocumentIndexes.add(new OpenBitSet(this.docsLength));
		OpenBitSet termDocIndex = this.allTermsDocumentIndexes.get(termIndex);
		termDocIndex.fastSet(docIndex);
	}
	
	/**
	 * Get documents bit set that contain all terms in a phrase
	 * @param terms
	 * @return
	 */
	public OpenBitSet getDocumentsContainingTerms(int[] terms){
		if(terms == null || terms.length < 1) return new OpenBitSet(this.countDocs());
		OpenBitSet docs = (OpenBitSet) this.allTermsDocumentIndexes.get(terms[0]).clone();
		for(int i = 1; i < terms.length; i++)
			docs.and(this.allTermsDocumentIndexes.get(terms[i]));
		return docs;
	}
	
	
	/**
	 * Get a unique term id for a given term in the collection
	 * @param term
	 * @param documentIndex
	 * @return
	 */
	public Integer getTermId(String term){
		Integer id = this.allTermsIndex.get(term);
		if(id == null){
			id = this.allTermsIndex.size();
			this.allTermsIndex.put(term, id);
			if(ApplicationSettings.KEEP_REVERSE_TERM_INDEX)
				this.allTermsReverseIndex.set(id, term);
		}
		return id;
	}
	
	
	/**
	 * Get a unique word id for a given word in the collection
	 * @param term
	 * @return
	 */
	public Integer getWordId(String word){
		Integer id = this.allWordsIndex.get(word);
		if(id == null){
			Integer i = this.allWordsIndex.size();
			this.allWordsIndex.put(word, i);
			return i;
		}
		return id;
	}

	/**
	 * Number of documentsToCluster in the context
	 * @return
	 */
	public int countDocs() {
		return this.docsLength;
	}
	
	/**
	 * Result label is built from the url, and a possible 
	 * prefix if result url is not unique in the result list
	 * @param d
	 * @return
	 */
	public String getResultLabel(IClusterDocument d){
		String label = d.getResultDoc().getUrl().trim();
		String prefix = this.customResultLabels.get(d.getIndex());
		if(prefix != null) label = prefix + label;
		if(label.length() > this.maxPathFieldSize)
			return label.substring(0, this.maxPathFieldSize - 1);
		return label;
	}
	
}
