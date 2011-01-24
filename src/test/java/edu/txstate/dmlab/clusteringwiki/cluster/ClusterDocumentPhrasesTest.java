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

import org.junit.Assert;
import org.junit.Test;

import edu.txstate.dmlab.clusteringwiki.preprocess.CollectionContext;

public class ClusterDocumentPhrasesTest {

	@Test
	public void testDocPhrases() throws Exception{
		final CollectionContext ctx = ClusterDocumentComparisonTest.getTestContext();
		
		int[] actuals = new int[50];
		
		for(IClusterDocument d1 : ctx.allDocs) {
			int[][] phrases = d1.getTermPhrases();
			String document = "";
			for(int j = 0; j < ctx.fields.length; j++)
				document += d1.getResultDoc().getFieldValue(ctx.fields[j]).trim() + ". ";
			actuals[d1.getIndex()] = phrases.length;
//			System.out.println(document);
//			System.out.println("Phrases:");
//			for(int i=0; i < phrases.length; i++){
//				final String ph = d1.getWordPhraseString(phrases[i]);
//				if(ph.length() > 0)
//					System.out.println("\t" + ph);
//			}
//			System.out.println("");
		}
//		int j = 1;
//		for(int i : actuals){
//			System.out.print(i + ",");
//			j++;
//			if(j%20 == 0) System.out.println("");
//		}
		
		int[] expecteds = new int[]{
			2,8,8,9,3,6,7,6,4,4,3,9,2,6,6,7,2,4,7,
			6,5,4,4,3,2,14,6,9,2,2,6,7,13,9,2,6,3,2,6,
			6,6,8,6,6,2,5,5,2,4,9
		};
		
		Assert.assertArrayEquals(expecteds, actuals);
		
//		IClusterDocument d1 = ctx.allDocs.get(ctx.allDocs.size() - 1);
//		int[][] phrases = d1.getTermPhrases();
//		String document = "";
//		for(int j = 0; j < ctx.fields.length; j++)
//			document += d1.getResultDoc().getFieldValue(ctx.fields[j]).trim() + ". ";
//		System.out.println(document);
//		System.out.println("Phrases:");
//		for(int i=0; i < phrases.length; i++){
//			final String ph = d1.getPhraseString(phrases[i]);
//			if(ph.length() > 0)
//				System.out.println("\t" + ph);
//		}
	}
	
	
}
