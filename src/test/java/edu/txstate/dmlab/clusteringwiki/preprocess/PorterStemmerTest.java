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

import org.junit.Test;

public class PorterStemmerTest {

	public static final PorterStemmer stemmer = new PorterStemmer();
	
	@Test
	public void StemmerTest(){
		final String[] phrases = new String[]{
			"completing",
			"another",
			"this",
			"computer",
			"computation",
			"generality",
			"bikes",
			"hikes",
			"contemplativity"
		};
		
		final String[] correct = new String[]{
				"complet",
				"anoth",
				"thi",
				"comput",
				"comput",
				"gener",
				"bike",
				"hike",
				"contempl"
			};
		
		String[] stems = new String[phrases.length];
		for(int i = 0; i < phrases.length; i++)
			stems[i] = stemmer.stem(phrases[i]);
		
		org.junit.Assert.assertArrayEquals(correct, stems);
		
	}
	
}
