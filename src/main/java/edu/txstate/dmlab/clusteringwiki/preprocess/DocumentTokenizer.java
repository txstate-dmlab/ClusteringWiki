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
import java.util.List;
import java.util.StringTokenizer;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;


/**
 * Responsible for tokenizing and analysis of document text,
 * at the same time assigning token types
 * @author David C. Anastasiu
 *
 */
public class DocumentTokenizer {

	public static final String[] EMPTY_TOKEN = new String[0];
	
	public static String[] tokenize (String document){

		if(document == null) return EMPTY_TOKEN;
		StringTokenizer st = new StringTokenizer(document, ApplicationSettings.TOKEN_CHARS, true);
		List<String> tokens = new ArrayList<String>();
		while(st.hasMoreTokens()){
			final String tok = st.nextToken();
			if(!tok.equals(" "))
				tokens.add(tok);
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	
}
