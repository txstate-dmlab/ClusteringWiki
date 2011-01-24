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

import edu.txstate.dmlab.clusteringwiki.preprocess.ITokenType;

/**
 * Utility class for identifying token types and other
 * token attributes
 * 
 * @author David C. Anastasiu
 *
 */
public class TokenUtils {

	private TokenUtils(){
        // no instantiation allowed
    }
	
	
	public static short getTokenType(String token){
		return CharUtils.isPunctuation(token) ? ITokenType.TF_PUNCTUATION : 
			CharUtils.isNumeric(token) ? ITokenType.TF_NUMERIC : ITokenType.TF_WORD;
		
	}
	
	/**
	 * Does full analisys of token, checking whether punctuation, numeric, or word,
	 * if punctuation whether a sentence or phrase separator, whether capitalized, 
	 * all caps or lowercase (first two chars), and whetehr token is a stop word.
	 * 
	 * @param token
	 * @return
	 */
	public static short getFullTokenType(String token){
		short tt = CharUtils.isPunctuation(token) ? ITokenType.TF_PUNCTUATION : 
			CharUtils.isNumeric(token) ? ITokenType.TF_NUMERIC : ITokenType.TF_WORD;
		if(tt == ITokenType.TF_PUNCTUATION)
			tt |= CharUtils.isSentenceSeparator(token) ? ITokenType.TF_SENTENCE_SEPARATOR : 
				 ITokenType.TF_PHRASE_SEPARATOR;
		tt |= CharUtils.isAllCaps(token) ? ITokenType.TF_ALL_CAPS : 
			CharUtils.isCapitalized(token) ? ITokenType.TF_CAPITALIZED : ITokenType.TF_LOWER_CASE;
		if(StringUtils.isStopWord(token)) 
			tt |= ITokenType.TF_STOP_WORD;
		
		return tt;
	}
	
	/**
	 * Get token type from raw type encoding
	 * @param tokenType
	 * @return
	 */
	public static int getTokenType(short tokenType){
		return (tokenType & ITokenType.TYPE_MASK);
	}
	
	/**
	 * Get type of token capitalization from raw type encoding
	 * @param tokenType
	 * @return
	 */
	public static int getCapitalizationType(short tokenType){
		return (tokenType & ITokenType.CAPITAL_MASK);
	}
	
	/**
	 * Get token separator type from raw type encoding
	 * @param tokenType
	 * @return
	 */
	public static int getSeparationType(short tokenType){
		return (tokenType & ITokenType.SEPARATOR_MASK);
	}
	
	/**
	 * Check whether word is a stop word
	 * @param tokenType
	 * @return
	 */
	public static boolean isStopWord(short tokenType){
		return (tokenType & ITokenType.TF_STOP_WORD) != 0;
	}
	
	/**
	 * Check whether word is a stop word
	 * @param tokenType
	 * @return
	 */
	public static boolean isQueryWord(short tokenType){
		return (tokenType & ITokenType.TF_QUERY_TERM) != 0;
	}
	
	/**
	 * Check if token is all caps
	 * @param tokenType
	 * @return
	 */
	public static boolean isAllCaps(short tokenType){
		return getCapitalizationType(tokenType) == ITokenType.TF_ALL_CAPS;
	}
	
	/**
	 * Check if token is capitalized
	 * @param tokenType
	 * @return
	 */
	public static boolean isCapitalized(short tokenType){
		return getCapitalizationType(tokenType) == ITokenType.TF_CAPITALIZED;
	}
	
	/**
	 * Check if token is a general word
	 * @param tokenType
	 * @return
	 */
	public static boolean isWord(short tokenType){
		return getTokenType(tokenType) == ITokenType.TF_WORD;
	}
	
	/**
	 * Check if token is numeric
	 * @param tokenType
	 * @return
	 */
	public static boolean isNumeric(short tokenType){
		return getTokenType(tokenType) == ITokenType.TF_NUMERIC;
	}
	
	/**
	 * Check if token is punctuation mark
	 * @param tokenType
	 * @return
	 */
	public static boolean isPunctuation(short tokenType){
		return getTokenType(tokenType) == ITokenType.TF_PUNCTUATION;
	}
	
	/**
	 * Check if token is phrase separator
	 * @param tokenType
	 * @return
	 */
	public static boolean isPhraseSeparator(short tokenType){
		return getSeparationType(tokenType) == ITokenType.TF_PHRASE_SEPARATOR;
	}
	
	/**
	 * Check if token is sentence separator
	 * @param tokenType
	 * @return
	 */
	public static boolean isSentenceSeparator(short tokenType){
		return getSeparationType(tokenType) == ITokenType.TF_SENTENCE_SEPARATOR;
	}
	
}
