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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.txstate.dmlab.clusteringwiki.preprocess.PorterStemmer;

/**
 * Utility class for methods dealing with string values
 * 
 * @author David C. Anastasiu
 *
 */
public class StringUtils {

	
	/** 
	 * An unmodifiable set containing stop words
	 * List of stop words was compiled by Armand Brahaj on Oct 3, 2009
	 * @see http://armandbrahaj.blog.al/2009/04/14/list-of-english-stop-words/
	 */
	public static final Set<?> ENGLISH_STOP_WORDS_SET;
  
	static {
		/** 
		 * List of stop words compiled by Armand Brahaj on Oct 3, 2009
		 * @see http://armandbrahaj.blog.al/2009/04/14/list-of-english-stop-words/
		 */
//		final List<String> stopWords = Arrays.asList(
//		    "a", "about", "above", "above", "across", "after", "afterwards", "again", 
//			"against", "all", "almost", "alone", "along", "already", "also", "although",
//			"always", "am", "among", "amongst", "amoungst", "amount", "an", "and", 
//			"another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", 
//			"around", "as",  "at", "back","be","became", "because","become","becomes", 
//			"becoming", "been", "before", "beforehand", "behind", "being", "below", 
//			"beside", "besides", "between", "beyond", "bill", "both", "bottom","but", 
//			"by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", 
//			"cry", "de", "describe", "detail", "do", "done", "down", "due", "during", 
//			"each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", 
//			"enough", "etc", "even", "ever", "every", "everyone", "everything", 
//			"everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", 
//			"first", "five", "for", "former", "formerly", "forty", "found", "four", 
//			"from", "front", "full", "further", "get", "give", "go", "had", "has", 
//			"hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", 
//			"herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", 
//			"however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", 
//			"into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", 
//			"least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", 
//			"mill", "mine", "more", "moreover", "most", "mostly", "move", "much", 
//			"must", "my", "myself", "name", "namely", "neither", "never", 
//			"nevertheless", "next", "nine", "no", "nobody", "none", "noone", 
//			"nor", "not", "nothing", "now", "nowhere", "of", "off", "often", 
//			"on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", 
//			"our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", 
//			"please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", 
//			"seems", "serious", "several", "she", "should", "show", "side", "since", 
//			"sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", 
//			"sometime", "sometimes", "somewhere", "still", "such", "system", "take", 
//			"ten", "than", "that", "the", "their", "them", "themselves", "then", 
//			"thence", "there", "thereafter", "thereby", "therefore", "therein", 
//			"thereupon", "these", "they", "thickv", "thin", "third", "this", "those", 
//			"though", "three", "through", "throughout", "thru", "thus", "to", 
//			"together", "too", "top", "toward", "towards", "twelve", "twenty", "two", 
//			"un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", 
//			"well", "were", "what", "whatever", "when", "whence", "whenever", "where", 
//			"whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever",
//			"whether", "which", "while", "whither", "who", "whoever", "whole", "whom", 
//			"whose", "why", "will", "with", "within", "without", "would", "yet", 
//			"you", "your", "yours", "yourself", "yourselves"
//	    );
		/** 
		 * List of stop words from snowball package
		 * @see http://snowball.tartarus.org/algorithms/english/stop.txt
		 */
		final List<String> stopWords = Arrays.asList(
				"i","me","my","myself","we","our","ours","ourselves","you","your","yours",
				"yourself","yourselves","he","him","his","himself","she","her","hers","herself",
				"it","its","itself","they","them","their","theirs","themselves","what","which",
				"who","whom","this","that","these","those","am","is","are","was","were","be",
				"been","being","have","has","had","having","do","does","did","doing","would",
				"should","could","ought","i'm","you're","he's","she's","it's","we're","they're",
				"i've","you've","we've","they've","i'd","you'd","he'd","she'd","we'd","they'd",
				"i'll","you'll","he'll","she'll","we'll","they'll","isn't","aren't","wasn't",
				"weren't","hasn't","haven't","hadn't","doesn't","don't","didn't","won't",
				"wouldn't","shan't","shouldn't","can't","cannot","couldn't","mustn't","let's",
				"that's","who's","what's","here's","there's","when's","where's","why's","how's",
				"a","an","the","and","but","if","or","because","as","until","while","of","at",
				"by","for","with","about","against","between","into","through","during","before",
				"after","above","below","to","from","up","down","in","out","on","off","over",
				"under","again","further","then","once","here","there","when","where","why","how",
				"all","any","both","each","few","more","most","other","some","such","no","nor",
				"not","only","own","same","so","than","too","very"
		);
		final Set<String> stopSet = new HashSet<String>();
		stopSet.addAll(stopWords);  
		ENGLISH_STOP_WORDS_SET = Collections.unmodifiableSet(stopSet); 
	}
	
	/**
	 * Stemmer used to stem words
	 */
	public static final PorterStemmer stemmer = new PorterStemmer();
	
	public static final String EMPTY_TEXT = "";
	  
	/**
	 * Patterns used in cleaning initial text received from server
	 */
	public static final Pattern HTML_CODE_PATTERN = Pattern.compile("<.+?>",
	    Pattern.CASE_INSENSITIVE);
	
	public static final Pattern SPACE_REDUCTION_PATTERN = Pattern.compile("\\s+",
	    Pattern.MULTILINE);
	
	public static final Pattern THREE_DOT_PATTERN = Pattern.compile("\\.+");
		
	
	/**
	 * Cleans HTML, reduces ... to one ., and reduces spaces to a single space
	 * @param doc
	 * @return
	 */
	public static String cleanText(String doc){
		if(doc == null) return EMPTY_TEXT;
		doc = HTML_CODE_PATTERN.matcher(doc).replaceAll("");
		doc = THREE_DOT_PATTERN.matcher(doc).replaceAll(".");
		return SPACE_REDUCTION_PATTERN.matcher(doc).replaceAll(" ").trim();
	}
	
	
	/**
	 * Check whether a given word is a stop word
	 * @param word
	 * @return
	 */
	public static boolean isStopWord(String word){
		if(word == null) return false;
		return ENGLISH_STOP_WORDS_SET.contains(word.toLowerCase());
	}
	
	/**
	 * Stem word using Porter Stemmer
	 * @param word
	 * @return
	 */
	public static String stemWord(String word){
		final int loc = word.indexOf('\'');
		//remove '<string>
		if(loc > -1)
			word = word.substring(0, loc);

		return stemmer.stem(word.toLowerCase());
	}
	
}
