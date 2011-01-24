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

/**
 * Identifies tokens by type using a bitwise flag. A token
 * may thus have multiple flags.  The tokenizer will assign
 * flags as appropriate.
 * 
 * Implementation is similar to that of Apache lucene analyzers.
 * 
 * @author David C. Anastasiu
 *
 */
public interface ITokenType {

	
	/**
     * Token type mask: 0x000f
     */
    public static final short TYPE_MASK = 0x000f;
    
    /** Current token is a general word. */
    public static final short TF_WORD = 0x0001;
    
    /** Current token is a number. */
    public static final short TF_NUMERIC = 0x0002;
    
    /** Current token is a punctuation mark. */
    public static final short TF_PUNCTUATION = 0x0003;
    

    /**
     * Token capital mask: 0x00f0
     */
    public static final short CAPITAL_MASK = 0x00f0;
    
    /** The current token starts with two capital letters. */
    public static final short TF_ALL_CAPS = 0x0010;
    
    /** The current token starts with a capital letter followed by a lower case letter. */
    public static final short TF_CAPITALIZED = 0x0020;
    
    /** The current token starts with lower case letters (if not TF_ALL_CAPS or TF_CAPITALIZED, then this. */
    public static final short TF_LOWER_CASE = 0x0020;
    
    
    /**
     * Token capital mask: 0x0f00
     * These flags are applied during analisys, after tokanization is complete
     */
    public static final short SEPARATOR_MASK = 0x0f00;
    
    /** The current token is common. */
    public static final short TF_PHRASE_SEPARATOR = 0x0100;

    /** The current token is part of the query. */
    public static final short TF_SENTENCE_SEPARATOR = 0x0200;
    
    /**
     * Markers for stop words or words belonging to query
     * Only one of the two will be present
     * These flags are applied during analisys, after tokanization is complete
     */
    
    /** The current token is common. */
    public static final short TF_STOP_WORD = 0x1000;

    /** The current token is part of the query. */
    public static final short TF_QUERY_TERM = 0x2000;
    
}
