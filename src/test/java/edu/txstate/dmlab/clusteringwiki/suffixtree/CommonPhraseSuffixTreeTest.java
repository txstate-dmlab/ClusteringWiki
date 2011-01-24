package edu.txstate.dmlab.clusteringwiki.suffixtree;

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

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.cluster.ClusterDocumentComparisonTest;
import edu.txstate.dmlab.clusteringwiki.cluster.IClusterDocument;
import edu.txstate.dmlab.clusteringwiki.preprocess.CollectionContext;

public class CommonPhraseSuffixTreeTest {

	@Test
	public void test(){
		Assert.assertEquals(1, 1);
	}
	
	@Test
	public void testExtractedCommonPhrases(){
		final IntPhraseInputBuilder ib = new IntPhraseInputBuilder();
		ib.addPhrase(0, 1, 2, 3);
		ib.addPhrase(0, 1, 2, 3);
		ib.addPhrase(0, 1, 2, 3, 4, 5, 6, 7, 8);
		ib.addPhrase(0, 1, 2, 3, 6, 8, 9, 11);
		ib.addPhrase(0, 1, 2, 3, 5, 7, 8, 9, 10, 11);
		ib.endDocument(0);
		ib.addPhrase(4, 1, 2, 3);
		ib.endDocument(1);
		ib.addPhrase(4, 2, 3, 5);
		ib.endDocument(2);
		final IntPhraseSuffixTreeInput input = new IntPhraseSuffixTreeInput(ib);
        
		final CommonPhraseSuffixTree c = new CommonPhraseSuffixTree(input, 20,
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MAXIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_CARDINALITY);
		
		int expected = 4; 
		
		Assert.assertEquals(expected, c.phraseList.size());
	}
	
	@Test
	public void testDocumentPhrases() throws Exception {
		final CollectionContext ctx = ClusterDocumentComparisonTest.getTestContext();
		
		final IntPhraseInputBuilder ib = new IntPhraseInputBuilder();
		
		int[] docs = new int[ctx.allDocs.size()];
		int j = 0;
		for(IClusterDocument d1 : ctx.allDocs) {
			docs[j++] = d1.getIndex();
			int[][] phrases = d1.getTermPhrases();
			for(int i=0; i < phrases.length; i++){
				final String ph = d1.getWordPhraseString(phrases[i]);
				if(ph.length() > 0){
					ib.addPhrase(phrases[i]);
				}
			}
			ib.endDocument(j);
		}
		final IntPhraseSuffixTreeInput input = new IntPhraseSuffixTreeInput(ib);
        
		final CommonPhraseSuffixTree c = new CommonPhraseSuffixTree(input, 
				ctx.allDocs.size() * 100,
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MAXIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_CARDINALITY);
		
		int expected = 32; 
		Assert.assertEquals(expected, c.phraseList.size());
		
//		while(c.pq.size() > 0){
//			final Phrase p =c.pq.pop();
//			System.out.println(ctx.getPhraseLabel(p.getDocumentsStack(), p.getTerms()) + "  |  Documents: " + p.getDocumentsStack());
//		}
	}
	
	
	@Test
	public void testSomeDocumentPhrases() throws Exception {
		final CollectionContext ctx = ClusterDocumentComparisonTest.getTestContext();
		
		final IntPhraseInputBuilder ib = new IntPhraseInputBuilder();
		
		int[] docs = new int[ctx.allDocs.size()];
		int j = 0;
		for(IClusterDocument d1 : ctx.allDocs) {
			if(d1.getIndex() % 3 == 0) continue;
			docs[j++] = d1.getIndex();
			int[][] phrases = d1.getTermPhrases();
			for(int i=0; i < phrases.length; i++){
				final String ph = d1.getWordPhraseString(phrases[i]);
				if(ph.length() > 0){
					ib.addPhrase(phrases[i]);
				}
			}
			ib.endDocument(d1.getIndex());
		}
		final IntPhraseSuffixTreeInput input = new IntPhraseSuffixTreeInput(ib);
        
		final CommonPhraseSuffixTree c = new CommonPhraseSuffixTree(input,
				ctx.allDocs.size() * 10, 
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MAXIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_CARDINALITY);

		int expected = 16; 
		Assert.assertEquals(expected, c.phraseList.size());
		
//		while(pq.size() > 0){
//			Phrase p = pq.pop();
//			System.out.println(ctx.getPhraseLabel(p.getDocIds(), p.getTerms()) + "  |  Documents: " + p.getDocumentsStack());
//		}
	}
}
