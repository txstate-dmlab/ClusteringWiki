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

/**
 * Utility class for methods dealing with char values
 * 
 * @author David C. Anastasiu
 *
 */
public class CharUtils {

	private CharUtils() {
		//no instantiation allowed
	}
	
	/**
	 * All methods below assume non-null input and appropriate start/end
	 * values.  They should be checked elsewhere
	 */
	
	/**
	 * Check whether token is numeric 
	 * @param token
	 * @return
	 */
	public static boolean isNumeric(char[] token){
		return isNumeric(token, 0, token.length);
	}
	
	/**
	 * Check whether token is numeric 
	 * @param token
	 * @return
	 */
	public static boolean isNumeric(String token){
		return isNumeric(token.toCharArray(), 0, token.length());
	}

	/**
	 * Weak check if a token is numeric - does not fully validate a number
	 * @param token
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isNumeric(char[] token, int start, int end){
		for (int i = start; i < end; i++)
			if(!isNumeric(token[i], start, i))
				return false; // invalid  
		return true; // valid
	}
	
	/**
	 * Weak check if a token character is numeric - does not fully validate a number
	 * @param token
	 * @param start
	 * @param pos - current position in the string
	 * @return
	 */
	public static boolean isNumeric(char c, int start, int pos){
			if (pos == start && (c == '-')) return true;  // negative    
			if ((c >= '0') && (c <= '9')) return true;  // 0 - 9
			if ((c == '.') || (c == ',')) return true;  // . or ,
			if ((c == 'x') || (c == 'f') || (c == 'd')) return true;  // scientific notation
			return false;
	}
	
	/**
	 * Check if a token contains only puctuation marks
	 * @param token
	 * @return
	 */
	public static boolean isPunctuation(char[] token){
		return isPunctuation(token, 0, token.length);
	}
	
	/**
	 * Check if a token contains only puctuation marks
	 * @param token
	 * @return
	 */
	public static boolean isPunctuation(String token){
		return isPunctuation(token.toCharArray(), 0, token.length());
	}
	
	/**
	 * Check if a token contains only puctuation marks
	 * @param token
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isPunctuation(char[] token, int start, int end){
		return isPhraseSeparator(token, start, end);
	}
	
	/**
	 * Check if token is all caps - just first two chars
	 * @param token
	 * @return
	 */
	public static boolean isAllCaps(char[] token){
		return isAllCaps(token, 0, token.length);
	}
	
	/**
	 * Check if token is all caps - just first two chars
	 * @param token
	 * @return
	 */
	public static boolean isAllCaps(String token){
		return isAllCaps(token.toCharArray(), 0, token.length());
	}
	
	/**
	 * Weak check if a token is all caps - just first two chars
	 * @param token
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isAllCaps(char[] token, int start, int end){
		if(end - start < 2) return false;
		boolean cc = Character.isUpperCase(token[start+1]);
		return cc && Character.isUpperCase(token[start]);
	}
	
	/**
	 * Check if token is capitalized - just first two chars
	 * @param token
	 * @return
	 */
	public static boolean isCapitalized(char[] token){
		return isCapitalized(token, 0, token.length);
	}
	
	/**
	 * Check if token is capitalized - just first two chars
	 * @param token
	 * @return
	 */
	public static boolean isCapitalized(String token){
		return isCapitalized(token.toCharArray(), 0, token.length());
	}
	
	/**
	 * Weak check if a token is capitalized - starts with capital letter
	 * and next letter is lower case
	 * @param token
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isCapitalized(char[] token, int start, int end){
		if(end - start < 2) return false;
		boolean cc = Character.isLowerCase(token[start+1]);
		return cc && Character.isUpperCase(token[start]);
	}
	
	/**
	 * Check if token is a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean isPhraseSeparator(char[] token){
		return isPhraseSeparator(token, 0, token.length);
	}
	
	/**
	 * Check if token is a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean isPhraseSeparator(String token){
		return isPhraseSeparator(token.toCharArray(), 0, token.length());
	}
	
	/**
	 * Check if token is a phrase separator
	 * @param token
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isPhraseSeparator(char[] token, int start, int end){
		for(int i = start; i < end; i++)
			if(!isPhraseSeparator(token[i])) return false;
		return true;
	}
	
	/**
	 * Check if chracter is a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean isPhraseSeparator(char token){
		if(Character.isLetterOrDigit(token)) return false;
		switch(token){
			case '.':
			case '!':
			case '?':
			case ',':
			case '\'':
			case '"':
			case '(':
			case ')':
			//case '&': //causes break on html codes, should be handled separately
			case ':':
			case ';':
			case '[':
			case ']':
			case '{':
			case '}':
			case '|':
			case '\\':
			case '/':
				return true;
			default:
				//nothing
		}
		return false;
	}

	/**
	 * Check if token is a sentence separator
	 * @param token
	 * @return
	 */
	public static boolean isSentenceSeparator(char[] token){
		return isSentenceSeparator(token, 0, token.length);
	}
	
	/**
	 * Check if token is a sentence separator
	 * @param token
	 * @return
	 */
	public static boolean isSentenceSeparator(String token){
		return isSentenceSeparator(token.toCharArray(), 0, token.length());
	}
	
	/**
	 * Check if token is a sentence separator
	 * @param token
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isSentenceSeparator(char[] token, int start, int end){
		for(int i = start; i < end; i++)
			if(!isSentenceSeparator(token[i])) return false;
		return true;
	}
	
	/**
	 * Check if token ends with a sentence separator
	 * @param token
	 * @return
	 */
	public static boolean endsWithSentenceSeparator(char[] token){
		return token.length > 2 && isSentenceSeparator(token[token.length - 1]);
	}
	
	/**
	 * Check if token ends with a sentence separator
	 * @param token
	 * @return
	 */
	public static boolean endsWithSentenceSeparator(String token){
		final int ln = token.length();
		return ln > 0 && isSentenceSeparator(token.charAt(ln - 1));
	}
	
	/**
	 * Check if token ends with a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean endsWithPhraseSeparator(char[] token){
		return token.length > 2 && isPhraseSeparator(token[token.length - 1]);
	}
	
	/**
	 * Check if token ends with a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean endsWithPhraseSeparator(String token){
		final int ln = token.length();
		return ln > 2 && isPhraseSeparator(token.charAt(ln - 1));
	}
	
	/**
	 * Check if token starts with a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean startsWithPhraseSeparator(char[] token){
		return token.length > 0 && isPhraseSeparator(token[0]);
	}
	
	/**
	 * Check if token starts with a phrase separator
	 * @param token
	 * @return
	 */
	public static boolean startsWithPhraseSeparator(String token){
		final int ln = token.length();
		return ln > 0 && isPhraseSeparator(token.charAt(0));
	}
	
	/**
	 * Check if character is sentence separator. We define sentences
	 * ending in period, question mark, or exclamation point.
	 * @param token
	 * @return
	 */
	public static boolean isSentenceSeparator(char token){
			switch(token){
				case '.':
				case '!':
				case '?':
					return true;
				default:
					//nothing
			}
		return false;
	}
}
