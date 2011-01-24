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

import org.junit.Assert;
import org.junit.Test;

import edu.txstate.dmlab.clusteringwiki.util.TokenUtils;

public class DocumentTokenizerTest {

	@Test
	public void testTokenization(){
		final String test = "The above code is an example of using StringTokenizer to split a string. In the current JDK this class is discourageg to be used, using instead the String.split(...) method or using a new java.util.regex package. Another test, if we dare (which we do), is to test: is it true? Hello! World{}[] \"Yes\" \'No.\'";
		String[] tokens = DocumentTokenizer.tokenize(test);
		short[] types = new short[tokens.length];
		for(int i=0; i < tokens.length; i++)
			types[i] = TokenUtils.getFullTokenType(tokens[i]);
//		System.out.println(StringUtils.join(tokens, "\", \""));
//		for(int i = 0; i < types.length; i++)
//			System.out.println(tokens[i] + ": " + TokenUtils.isPunctuation(types[i]));
		
		short[] expecteds = new short[]{
			4129,4129,33,4129,4129,33,4129,33,33,4129,33,4129,33,4129,4129,33,17,4129,33,4129,33,
			4129,4129,33,33,33,4129,33,33,4129,33,4129,33,33,33,33,33,4129,4129,33,33,
			4129,33,4129,4129,33,291,4129,4129,33,33,33,33,33
		};
		
		Assert.assertArrayEquals(expecteds, types);
	}
}
