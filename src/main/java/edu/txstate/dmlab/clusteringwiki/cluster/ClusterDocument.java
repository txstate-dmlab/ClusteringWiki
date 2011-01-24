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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.preprocess.CollectionContext;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult;
import edu.txstate.dmlab.clusteringwiki.util.ArrayUtils;
import edu.txstate.dmlab.clusteringwiki.util.CharUtils;
import edu.txstate.dmlab.clusteringwiki.util.StringUtils;
import edu.txstate.dmlab.clusteringwiki.util.TokenUtils;

/**
 * Cluster document.  Represents the document
 * in relation to the original retrieved doc in ICWSearchResultCol,
 * connected by index
 * 
 * @author David C. Anastasiu
 *
 */
@XStreamAlias("result")
public class ClusterDocument implements IClusterDocument {

	/**
	 * Index within the originally received ICWSearchResultCol
	 * for this document
	 */
	protected final int index;
	
	/**
	 * Reference to the the initial search result this document is based on
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final ICWSearchResult resultDoc;
	
	/**
	 * Reference to the collection context
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final CollectionContext context;
	
	
	/**
	 * Reference to context dictionary of words.
	 */
	@XStreamOmitField
	@JsonIgnore
	public final Map<String, Integer> allWordsIndex;
	
	/**
	 * Reference to context dictionary of terms.
	 */
	@XStreamOmitField
	@JsonIgnore
	public final Map<String, Integer> allTermsIndex;
	
	/**
	 * List of word strings in this doc after initial
	 * processing. HTML code is removed and text is
	 * lower-cased after which it is split on whitespace.
	 */
	@XStreamOmitField
	@JsonIgnore
	protected String[] wordStrings;
	
	/**
	 * Cached value of the number of words contained in this doc
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int wordsLength;
	
	/**
	 * List of unique allWordsIndex index ids in this doc after 
	 * initial processing. Will likely contain duplicates.
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int[] words;
	
	/**
	 * Indeces in words (and wordStrings) that represent the beggining 
	 * and the last word index in a phrase. 
	 */
	protected int[] phraseBounds;
	
	/**
	 * Indeces in terms that represent the beggining and the last
	 * term in a phrase. Phrases are matched between phraseBounds and
	 * termPhraseBounds such that index i of each represents either the
	 * beggining (if i is even) or end (if i is odd) of the same phrase
	 */
	protected int[] termPhraseBounds;
	
	/**
	 * Set of words (index id from allWordsIndex) and count for that word
	 * (No duplicates, order not preserved)
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final Map<Integer, Integer> wordCountsIndex = new HashMap<Integer, Integer>();
	
	/**
	 * Set of terms (index id from allTermsIndex) and count for that term
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final Map<Integer, Integer> termCountsIndex = new HashMap<Integer, Integer>();
	
	/**
	 * List of terms (stemmed non-stop-word, lower-cased words) in this doc after processing
	 */
	@XStreamOmitField
	@JsonIgnore
	protected String[] termStrings;
	
	/**
	 * List of allTermsIndex index ids in this doc after processing
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int[] termList;
	
	/**
	 * Mapping between indexes of terms in termsList and indexes of words in the 
	 * wordStrings & words arrays
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int[] termListMapping;
	
	/**
	 * Cached value of how many terms are included in the term list
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int termListLength;
	
	/**
	 * Set of unique allTermsIndex index ids in this doc after processing
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int[] terms;
	
	/**
	 * Cached value of how many terms are included in this document
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int termsLength;

	/**
	 * Number of times each term in terms appears in doc
	 * count(terms) == count(tdf) and terms[i] refers to same terms as tdf[i]
	 */
	@XStreamOmitField
	@JsonIgnore
	protected int[] tdf;
	
	/**
	 * Frequency with which each term appears in doc
	 * (TF or TF*IDF)
	 * count(terms) == count(weights) and terms[i] refers to same terms as weights[i]
	 */
	@XStreamOmitField
	@JsonIgnore
	protected double[] weights;
	
	/**
	 * Reference to the chosen similarity calculator
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final ISimilarityCalculator symCalc = ApplicationSettings.getSimilarityCalculator();
	
	
	/**
	 * Construct the doc without processing its term internals
	 * @param index
	 * @param resultDoc
	 */
	public ClusterDocument (int theIndex, ICWSearchResult theResultDoc, CollectionContext theContext){
		resultDoc = theResultDoc;
		index = theIndex;
		context = theContext;
		
		if(context != null) {
			allWordsIndex = context.allWordsIndex;
			allTermsIndex = context.allTermsIndex;
		} else {
			allWordsIndex = null;
			allTermsIndex = null;
		}
		
		if(resultDoc != null) {
			String document = "";
			final int fln = context.fields.length;
			for(int j = 0; j < fln; j++){
				document += resultDoc.getFieldValue(context.fields[j]).trim();
				if(j != fln)  document += ". ";
			}
			//before we tokenize and analize the document text, we need to remove HTML code
			//and reduce spaces in text
			document = StringUtils.cleanText(document);
			
			//get list of text words
			List<String> wds = new ArrayList<String>(); //keep track of tokens (words)
			List<Integer> wdIds = new ArrayList<Integer>(); //words array equivalent holding word ids
			List<Integer> phrBounds = new ArrayList<Integer>(); //phrase bounds in words array
			List<String> tms = new ArrayList<String>(); //keep track of terms
			List<Integer> tmIds = new ArrayList<Integer>(); //terms array equivalent holding term ids
			List<Integer> tmsMapping = new ArrayList<Integer>(); //terms array mapping to word ids
			List<Integer> tmsPhrBounds = new ArrayList<Integer>(); //phrase bounds in terms array
			
			//tokenize the input string and process tokens
			StringTokenizer st = new StringTokenizer(document, ApplicationSettings.TOKEN_CHARS, true);
			
			boolean phraseStarted = false;
			
			while(st.hasMoreTokens()){
				String tok = st.nextToken();
				
				if(!tok.equals(" ") && tok.length() > 0){
					while(CharUtils.startsWithPhraseSeparator(tok))
						tok = tok.substring(1);
					boolean endsWithPhraseSep = CharUtils.endsWithPhraseSeparator(tok);
					while(CharUtils.endsWithPhraseSeparator(tok))
						tok = tok.substring(0, tok.length() - 1);
					
					final short tt = TokenUtils.getFullTokenType(tok);
					final boolean isStopWord = TokenUtils.isStopWord(tt);
					final boolean isPunctuation = TokenUtils.isPunctuation(tt);
					final int wordsIndex = wds.size(); //the index in the words array that current word will have
					
					if(!isPunctuation){
						if(!phraseStarted && !isStopWord){
							//new phrase
							phrBounds.add(wds.size());
							tmsPhrBounds.add(tms.size());
							phraseStarted = true;
						}
						//add the word to the list, find its id and add it to the word ids list
						wds.add(tok);
						final int wid = getWordId(tok);
						wdIds.add(wid);
						//keep track of the number of times word is used in document
						final Integer cnt = wordCountsIndex.get(wid);
						if(cnt == null) wordCountsIndex.put( wid, 1 );
						else wordCountsIndex.put( wid, cnt + 1 );
						//get term for the word if necessary
						if(!isStopWord){
							final String tm = StringUtils.stemWord(tok);
							if(tm.length() >= ApplicationSettings.MINIMUM_TERM_LENGTH){
								tms.add(tm);
								final int tid = getTermId(tm);
								final Integer cnt2 = termCountsIndex.get(tid);
								if(cnt2 == null) termCountsIndex.put( tid, 1 );
								else termCountsIndex.put( tid, cnt2 + 1 );
								tmIds.add(tid);
								tmsMapping.add(wordsIndex);
							}
						}
					
					} else {
						//we still need to add the token to the word list
						wds.add(tok);
						wdIds.add(-1); //negative id means not a word
					}
					
					//token is a word that ends with a phrase separator or punctuation 
					//we end a started phrase here if punctuation
					if( (isPunctuation || endsWithPhraseSep) && phraseStarted){
						//decide whether to add the phrase
						final int sz = tmsPhrBounds.size();
						int ln = sz > 0 ? tms.size() - tmsPhrBounds.get(sz - 1) : 0;
						if(ln >= ApplicationSettings.MINIMUM_PHRASE_LENGTH){
							phrBounds.add(wds.size());
							tmsPhrBounds.add(tms.size());
						} else if(sz > 0){
							//phrase does not meet minimum length constraint
							phrBounds.remove(phrBounds.size() - 1);
							tmsPhrBounds.remove(tmsPhrBounds.size() - 1);
						}
						phraseStarted = false;
					}
					
				}
			}
			
			//end any started phrase
			if(phraseStarted){
				phrBounds.add(wds.size());
				tmsPhrBounds.add(tms.size());
				phraseStarted = false;
			}
			
			//populate word data in document
			wordsLength = wds.size();
			wordStrings = wds.toArray(new String[wordsLength]);
			phraseBounds = ArrayUtils.intColToArray(phrBounds);
			words = ArrayUtils.intColToArray(wdIds);
			
			//populate term data in document
			termsLength = termCountsIndex.size();
			terms = new int[termsLength];
			tdf = new int[termsLength];
			weights = new double[termsLength];
			int bi = 0;
			List<Integer> termIds = new ArrayList<Integer>( termCountsIndex.keySet() );
			Collections.sort(termIds);
			for(Integer tid : termIds){
				terms[bi] = tid;
				tdf[bi] = termCountsIndex.get(tid);
				bi++;
			}
			termPhraseBounds = ArrayUtils.intColToArray(tmsPhrBounds);
			
			//populate term string data in the document
			termListLength = tms.size();
			termStrings = tms.toArray(new String[termListLength]);
			termList = ArrayUtils.intColToArray(tmIds);
			
			//populate mapping between terms array and words array
			termListMapping = ArrayUtils.intColToArray(tmsMapping);
			
			normalize();

		}
	}
	
	/**
	 * Get all identified phrases in this document
	 * as arrays of allTermsIndex index ids
	 * @return
	 */
	public int[][] getTermPhrases(){
		final int cnt = this.termPhraseBounds.length;
		if(cnt <= 0) return new int[0][0];
		int[][] phrs = new int[cnt/2][];
		for(int i = 0; i < cnt - 1; i += 2){
			int ln = this.termPhraseBounds[i+1] - this.termPhraseBounds[i];
			if(ln == 0) continue;
			int[] phr = new int[ln];
			int c = 0;
			for(int j = this.termPhraseBounds[i]; j < this.termPhraseBounds[i+1]; j++)
				phr[c++] = this.termList[j];
			phrs[i/2] = phr;
		}
		return phrs;
	}
	
	
	/**
	 * Get the String version of a phrase of terms given the start and
	 * end indexes in termList.  Note string content is terms, not words.
	 * @param start
	 * @param end
	 * @return
	 */
	public String getTermPhraseString(int start, int end){
		if(end >= this.termListLength || end < start + 1) return "";
		StringBuilder sb = new StringBuilder();
		int i = start;
		for( ; i < end - 1; i++)
			sb.append(this.termList[i] + " ");
		sb.append(this.termList[i]);
		return sb.toString();
	}
	
	/**
	 * Get the String version of a phrase of words given the start and
	 * end indexes in wordStrings  Note string content is words.
	 * @param start Start index in words array
	 * @param end End index in words array
	 * @return
	 */
	public String getPhraseString(int start, int end){
		if(end > this.wordsLength || end < start + 1) return "";
		StringBuilder sb = new StringBuilder();
		int i = start;
		for( ; i < end - 1; i++)
			sb.append(this.wordStrings[i] + " ");
		sb.append(this.wordStrings[i]);
		return sb.toString();
	} 
	
	/**
	 * Find the start and end indexes in 
	 * termList for the given termPhrase
	 * @param phrase
	 * @return
	 */
	public int[] identifyTermPhraseBounds(final int[] phrase){
		int[] bounds = new int[2];
		final int cnt = this.termPhraseBounds.length;
		final int phraseLn = phrase.length;
		if(phrase == null || cnt < 1) return bounds;
		int start, end;
		for(int i = 0; i < cnt - 1; i += 2){
			start = this.termPhraseBounds[i];
			end = this.termPhraseBounds[i+1];
			//advance within phrase until subphrase found or can no longer be found
			for(; start < end - phraseLn + 1; start++)
				if(ArrayUtils.isSubarrayAtIndex(this.termList, phrase, start)) break;
			
			if(start < end - phraseLn + 1){
				bounds[0] = start;
				bounds[1] = start + phraseLn;
				return bounds;
			}
			
		}
		
		return bounds;
	}
	
	/**
	 * Find the start and end indexes in wordStrings
	 * for the given termPhrase. 
	 * Bounds in this and all other methods utilizing bounds are
	 * defined by the start index in the array where a straing starts
	 * and the index after the string end (end index + 1).
	 * @param termPhrase
	 * @return
	 */
	public int[] identifyWordPhraseBounds(int[] termPhrase){
		int[] bounds = this.identifyTermPhraseBounds(termPhrase);
		if(bounds[1] == 0) return bounds;
		bounds[0] = this.termListMapping[ bounds[0] ];
		bounds[1] = this.termListMapping[ bounds[1] - 1 ] + 1;
		return bounds;
	}
	
	
	/**
	 * Get the String version of a phrase of words given an 
	 * equivalent termPhrase from this document.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public String getWordPhraseString(int[] termPhrase){
		int[] bounds = this.identifyWordPhraseBounds(termPhrase);
		if(bounds[1] == 0){
			String s = "";
			for(int g : termPhrase){
				for(String j : allTermsIndex.keySet())
					if(allTermsIndex.get(j) == g){
						s += j + " ";
						break;
					}
						
				s += "(" + g + "),";
			}
//			System.out.println(s + "  -- " + bounds[0] + " - " +  bounds[1]);
			return null;
		}
		return this.getPhraseString(bounds[0], bounds[1]);
	}
	
	/**
	 * Add a term to the doc
	 * @param index Term index in allTermsIndex
	 * @param count Term count in this doc
	 * @param rebuild Whether to rebuild the terms, counts and weight arrays
	 */
	public void add(int index, int count, boolean rebuild){
		Integer tc = termCountsIndex.get(index);
		if(tc == null) termCountsIndex.put(index, count);
		else termCountsIndex.put(index, tc + count);
		if(rebuild) rebuildArrays();
	}
	
	/**
	 * Remove a term from this document
	 * @param index Term index in allTermsIndex
	 * @param rebuild Whether to rebuild the terms, counts and weight arrays
	 */
	public void remove(int index, boolean rebuild){
		termCountsIndex.remove(index);
		if(rebuild) rebuildArrays();
	}
	
	/**
	 * Remove all terms
	 */
	public void clear(){
		termCountsIndex.clear();
		terms = null;
		tdf = null;
		weights = null;
	}
	
	/**
	 * Rebuild terms, counts, and weights arrays from termCounts map
	 * Used after adding a number of individual terms
	 */
	public void rebuildArrays(){
		termsLength = termCountsIndex.size();
		terms = new int[termsLength];
		tdf = new int[termsLength];
		weights = new double[termsLength];
		
		int i = 0;
		List<Integer> termIds = new ArrayList<Integer>( termCountsIndex.keySet() );
		Collections.sort(termIds);
		for(Integer tid : termIds){
			terms[i] = tid;
			tdf[i] = termCountsIndex.get(tid);
			i++;
		}
		normalize();
	}
	
	/**
	 * Get a unique term id for a given term in the collection
	 * @param term
	 * @return
	 */
	public Integer getTermId(String term){
		Integer id = allTermsIndex.get(term);
		if(id == null){
			id = allTermsIndex.size();
			allTermsIndex.put(term, id);
			if(ApplicationSettings.KEEP_REVERSE_TERM_INDEX)
				this.context.allTermsReverseIndex.set(id, term);
		}
		return id;
	}
	
	/**
	 * Get a unique word id for a given word in the collection
	 * @param term
	 * @return
	 */
	public Integer getWordId(String word){
		Integer id = allWordsIndex.get(word);
		if(id == null){
			Integer i = allWordsIndex.size();
			allWordsIndex.put(word, i);
			return i;
		}
		return id;
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the initial search result this document is based on
	 */
	public ICWSearchResult getResultDoc(){
		return resultDoc;
	}
	
	/**
	 * Get number of terms in this document
	 * @return
	 */
	public int count(){
		return termCountsIndex.size();
	}
	
	/**
	 * Get number of times a term appears in this doc
	 * @param index
	 * @return
	 */
	public int getTermCount(int index){
		Integer t = termCountsIndex.get(index);
		return t != null ? t : 0;
	}

	/**
	 * @return the wordCountsIndex
	 */
	public Map<Integer, Integer> getWordCountsIndex() {
		return wordCountsIndex;
	}

	/**
	 * @return the termCountsIndex
	 */
	public Map<Integer, Integer> getTermCountsIndex() {
		return termCountsIndex;
	}

	/**
	 * @return the terms
	 */
	public int[] getTerms() {
		return terms;
	}
	
	/**
	 * Get term counts for the terms in this document
	 * Map keys are term index values from allTermsIndex
	 * @return term counts
	 */
	public int[] getTermCounts(){
		return tdf;
	}
	
	/**
	 * Get term weights for the terms in this document
	 * Map keys are term index values from allTermsIndex
	 * @return term weights
	 */
	public double[] getTermWeights(){
		return weights;
	}
	
	/**
	 * Get term weights for the terms in this document
	 * Map keys are term index values from allTermsIndex
	 * @return term weights
	 */
	@JsonIgnore
	public Map<Integer, Double> getTermWeightsMap(){
		Map<Integer, Double> m = new HashMap<Integer, Double>(termsLength);
		for(int i = 0; i < termsLength; i++)
			m.put(Integer.valueOf(terms[i]), weights[i]);
		return m;
	}
	
	
	/**
	 * Build weights array by normalizing counts using TF measure
	 */
	protected void normalize(){
		if(terms == null || termsLength == 0) return;
		for(int i = 0; i < termsLength; i++)
			weights[i] = tdf[i] / (double) termsLength;
		
		/**
		 * For TF-IDF normalization we would need access to the context
		 * It slows down pre-processing by about .2 s for 200 docs, so
		 * not included for now.  Could be included with code similar to
		 * below:
		 * 
		 * final int[] terms = d.getTerms();
		 * final int[] counts = d.getTermCounts();
		 * final int n = d.getContext().countDocs();
		 * 
		 * if(terms == null || terms.length == 0) return null;
		 * final int numTerms = terms.length;
		 * double[] weights = new double[numTerms];
		 * for(int i = 0; i < numTerms; i++){
		 * 	int dm = d.getContext().getNumDocsTermIsIn(terms[i]);
		 * 	weights[i] = counts[i] * (1 + Math.log(n) - Math.log(dm));
		 * }
		 * 
		 * Also, in the context of sub-clusters one would probably need to
		 * implement getNumDocsTermIsIn specific to the parent cluster, not
		 * the full request context
		 * 
		 * Note I removed getNumDocsTermIsIn as it required an additional
		 * reverse lookup index of terms to docs tems were in that was not
		 * needed for anything else and was just slowing down execution.
		 */
	}
	
	/**
	 * Get common terms between two docs
	 * @param d
	 * @return
	 */
	public int[] termsIntersection(IClusterDocument d){
		int sourceIndex = 0;
		int targetIndex = 0;
		int[] target = d.getTerms();
		if(terms == null || target == null) return null;
		Set<Integer> intersect = new TreeSet<Integer>();
		while(sourceIndex < termsLength && targetIndex < target.length){
			int s = terms[sourceIndex];
			int t = target[targetIndex];
			if(s == t){
				intersect.add(s);
				sourceIndex++;
				targetIndex++;
			}
			else if (s < t) sourceIndex++;
			else targetIndex++;
		}
		return ArrayUtils.intColToArray(intersect);
	}
	
	/**
	 * Get the number of common terms between two docs
	 * @param d
	 * @return
	 */
	public int termsIntersectionCount(IClusterDocument d){
		int sourceIndex = 0;
		int targetIndex = 0;
		int[] target = d.getTerms();
		if(terms == null || target == null) return 0;
		int count = 0;
		while(sourceIndex < termsLength && targetIndex < target.length){
			int s = terms[sourceIndex];
			int t = target[targetIndex];
			if(s == t){
				count++;
				sourceIndex++;
				targetIndex++;
			}
			else if (s < t) sourceIndex++;
			else targetIndex++;
		}
		return count;
	}
	
	/**
	 * Get all distinct terms from the two docs
	 * @param d
	 * @return
	 */
	public int[] termsUnion(IClusterDocument d){
		int sourceIndex = 0;
		int targetIndex = 0;
		int[] target = d.getTerms();
		if(terms == null) return target;
		if(target == null) return terms;
		Set<Integer> union = new TreeSet<Integer>();
		while(sourceIndex < termsLength && targetIndex < target.length){
			int s = terms[sourceIndex];
			int t = target[targetIndex];
			if(s == t){
				union.add(s);
				sourceIndex++;
				targetIndex++;
			} else if (s < t) {
				union.add(s);
				sourceIndex++;
			} else {
				union.add(t);
				targetIndex++;
			}
		}
		//one list may have finished before the other
		//add remaining terms
		if(sourceIndex < termsLength) {
			for(int i = sourceIndex; i < termsLength; i++)
				union.add(terms[sourceIndex]);
		}
		else if(targetIndex < target.length) {
			for(int i = targetIndex; i < target.length; i++)
				union.add(target[targetIndex]);
		}
		
		return ArrayUtils.intColToArray(union);
	}
	
	/**
	 * Get the number of total distinct terms between two docs
	 * @param d
	 * @return
	 */
	public int termsUnionCount(IClusterDocument d){
		int sourceIndex = 0;
		int targetIndex = 0;
		int[] target = d.getTerms();
		if(terms == null) return target == null ? 0 : target.length;
		if(target == null) return termsLength;
		int count = 0;
		while(sourceIndex < termsLength && targetIndex < target.length){
			int s = terms[sourceIndex];
			int t = target[targetIndex];
			if(s == t){
				sourceIndex++;
				targetIndex++;
			} else if (s < t) sourceIndex++;
			else targetIndex++;
			//increment count of distinct terms
			count++;
		}
		//one list may have finished before the other
		//add remaining terms
		if(sourceIndex < termsLength) count += termsLength - sourceIndex;
		else if(targetIndex < target.length) count += target.length - targetIndex;
		
		return count;
	}
	
	/**
	 * Compute similarity between this document with another document d
	 */
	public double computeSimilarity(IClusterDocument d){
		return symCalc.computeSimilarity(this, d);
	}
	
	/**
	 * Get document's eucledian norm
	 * @return
	 */
	public double eucledianNorm(){
		double norm = 0.0D;
		for(int i=0; i < termsLength; i++){
			norm += weights[i] * weights[i];
		}
		return Math.sqrt(norm);
	}
	
	
	/**
	 * Get document's eucledian distance between two docs
	 * @return
	 */
	public double eucledianDistance(IClusterDocument d1, IClusterDocument d2){
		return d1.eucledianDistance(d2);
	}
	
	/**
	 * Eucledian distance between this doc and another doc
	 * @param d
	 * @return
	 */
	public double eucledianDistance(IClusterDocument d){
		int sourceIndex = 0;
		int targetIndex = 0;
		int[] target = d.getTerms();
		double[] targetWeights = d.getTermWeights();
		double sourceNorm = 0.0D;
		double targetNorm = 0.0D;
		double commonNorm = 0.0D;
		while(sourceIndex < termsLength || targetIndex < target.length){
			int s = sourceIndex < termsLength ? terms[sourceIndex] : Integer.MAX_VALUE;
			int t = targetIndex < target.length ? target[targetIndex] : Integer.MAX_VALUE;
			if(s == t){
				commonNorm += ( (weights[sourceIndex] - targetWeights[targetIndex]) *
					(weights[sourceIndex] - targetWeights[targetIndex]) );
				sourceIndex++;
				targetIndex++;
			} else if (s < t){
				sourceNorm += weights[sourceIndex] * weights[sourceIndex];
				sourceIndex++;
			} else {
				targetNorm += targetWeights[targetIndex] * targetWeights[targetIndex];
				targetIndex++;
			}
		}
		return Math.sqrt(commonNorm + sourceNorm + targetNorm);
	}
	
	
}
