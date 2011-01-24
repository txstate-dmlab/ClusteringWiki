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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.lucene.util.OpenBitSet;
import org.junit.Assert;
import org.junit.Test;

public class GeneralizedSuffixTreeTest {
	
	class TestGeneralizedSuffixTree extends GeneralizedSuffixTree
    {
        public TestGeneralizedSuffixTree(ISuffixTreeInput input,
				int minCardinality) {
			super(input, minCardinality);
		}
        
        public TestGeneralizedSuffixTree(ISuffixTreeInput input) {
			super(input);
		}

		public final ArrayList<String> nodes = new ArrayList<String>();

        protected void processPhrase(int state, int card, OpenBitSet bset, Stack<Integer> edges)
        {
            final StringBuilder b = new StringBuilder();
            for (int i = 0; i < edges.size(); i += 2)
                for (int j = edges.get(i); j <= edges.get(i + 1); j++)
                    b.append(input.get(j) + " ");

            nodes.add(b.toString() + "[" + card + "]");
        }
    }
    
    @Test
    public void testMultiphraseGST()
    {
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
        final TestGeneralizedSuffixTree gst = new TestGeneralizedSuffixTree( input, 2 );
        gst.processDocuments();
        
        final ArrayList<String> nodes = new ArrayList<String>();
        
        gst.walk(new BaseVisitor()
        {
            final List<Integer> states = new ArrayList<Integer>();

            public void after(int state)
            {
                if (gst.getRootNode() != state)
                {
                    final StringBuilder buffer = new StringBuilder();
                    for (int i = 0; i < states.size(); i += 2)
                        for (int j = states.get(i); j <= states.get(i + 1); j++)
                            buffer.append( gst.input.get(j) + " ");

                    if (gst.isLeafNode(state)) buffer.append(" [leaf]");
                    nodes.add(buffer.toString());

                    states.remove(states.size() - 1);
                    states.remove(states.size() - 1);
                }
            };

            public boolean visit(int fromState, int toState, int startIndex, int endIndex)
            {
                states.add(startIndex);
                states.add(endIndex);
                return true;
            }
        });

        Collections.sort(nodes);
//        for(String s : nodes)
//        	System.out.println(s);

        Collections.sort(gst.nodes);
//        for(String s : gst.nodes)
//        	System.out.println(s);
        Assert.assertArrayEquals(new Object [] {
            "1 2 3 [2]",
            "2 3 5 [2]",
            "2 3 [3]",
            "3 5 [2]",
            "3 [3]",
            "4 [3]",
            "5 [2]"
        }, gst.nodes.toArray());        
    }

    /**
     * 
     */
    @Test
    public void testSinglephraseGST()
    {
        final IntPhraseInputBuilder ib = new IntPhraseInputBuilder();
        ib.addPhrase(0, 1, 2, 3);
        ib.endDocument(0);
        ib.addPhrase(0, 1, 2, 3);
        ib.endDocument(1);
        ib.addPhrase(4, 1, 2, 3);
        ib.endDocument(2);
        ib.addPhrase(4, 2, 3, 5);
        ib.endDocument(3);

        final IntPhraseSuffixTreeInput input = new IntPhraseSuffixTreeInput(ib);
        final TestGeneralizedSuffixTree gst = new TestGeneralizedSuffixTree( input, 2 );
        gst.processDocuments();

        Collections.sort(gst.nodes);
        Assert.assertArrayEquals(new Object [] {
            "0 1 2 3 [2]",
            "1 2 3 [3]",
            "2 3 [4]",
            "3 [4]",
            "4 [2]",
        }, gst.nodes.toArray());        
    }

    /**
     * 
     */
    @Test
    public void testEmptyGST()
    {
        final IntPhraseInputBuilder ib = new IntPhraseInputBuilder();
        ib.endDocument(0);

        final IntPhraseSuffixTreeInput input = new IntPhraseSuffixTreeInput(ib);
        final TestGeneralizedSuffixTree gst = new TestGeneralizedSuffixTree( input );
        gst.processDocuments();
        
        int actual = gst.nodes.size();
        int expected = 0;
        Assert.assertEquals(expected, actual);
    }
}
