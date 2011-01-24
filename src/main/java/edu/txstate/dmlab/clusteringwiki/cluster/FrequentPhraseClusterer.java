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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.OpenBitSet;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;
import edu.txstate.dmlab.clusteringwiki.suffixtree.CommonPhraseSuffixTree;
import edu.txstate.dmlab.clusteringwiki.suffixtree.IntPhraseInputBuilder;
import edu.txstate.dmlab.clusteringwiki.suffixtree.IntPhraseSuffixTreeInput;
import edu.txstate.dmlab.clusteringwiki.suffixtree.Phrase;
import edu.txstate.dmlab.clusteringwiki.suffixtree.PhraseList;
import edu.txstate.dmlab.clusteringwiki.util.ArrayUtils;

/**
 * Implementation of the frequent phrase clustering algorithm 
 * 
 * @author David C. Anastasiu
 *
 */
public class FrequentPhraseClusterer extends BaseClusterer implements IClusterer {

	/**
	 * Controller
	 * @param theContext
	 */
	public FrequentPhraseClusterer(ICollectionContext theContext) {
		super(theContext);
	}

	/**
	 * Get a set of frequent phrases associated with documents
	 * @param docs
	 * @return
	 */
	public PhraseList getFrequentPhrase(int[] docs, int minPhraseLength, 
			int maxPhraseLength, int minCardinality){

		final IntPhraseInputBuilder ib = new IntPhraseInputBuilder();
		
		for(int docId : docs) {
			final IClusterDocument d1 = this.allDocs.get(docId);
			final int[][] phrases = d1.getTermPhrases();
			for(int i=0; i < phrases.length; i++)
				if(phrases[i] != null)
					ib.addPhrase(phrases[i]);
			ib.endDocument(docId);
		}
		final IntPhraseSuffixTreeInput input = new IntPhraseSuffixTreeInput(ib);
		final CommonPhraseSuffixTree c = new CommonPhraseSuffixTree(input,
			docs.length * 5, minPhraseLength, 
			maxPhraseLength, minCardinality);

		return c.phraseList;
		
	}
	
	
	/**
	 * Cluster a given level in a cluster hierarchy
	 * @param parent
	 * @param docs
	 */
	@Override
	public List<ICluster> levelCluster(ICluster parent, int[] docs) {
		if(docs == null){ //cluster all docs
			return levelCluster(parent);
		}
		documentsToCluster = docs;

		final List<ICluster> clusters = new ArrayList<ICluster>();
		//initial clustering, use set application limits to retrieve 
		//list of frequent phrases with appropriate phrase limits
		PhraseList phraseList = getFrequentPhrase(docs,
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MAXIMUM_FREQUENT_PHRASE_LENGTH, 
				ApplicationSettings.MINIMUM_FREQUENT_PHRASE_CARDINALITY);
		
		//keep track of covered docs
		final OpenBitSet coveredDocs = new OpenBitSet(docs.length);
		
		Phrase p = phraseList.getTopPhrase(coveredDocs);
		final int[][] termPhrasesPath = ((FrequentPhraseCluster) parent).getLabelTermsPath();
		final int[] queryTerms = context.getQueryTerms();
		
		while(p != null) {
			//trim phrases are subphrases of parent labels or the query
			while(p != null && ( 
					ArrayUtils.isSubsetOf(p.getTerms(), termPhrasesPath)
					|| ArrayUtils.isSubsetOf(p.getTerms(), queryTerms))){
					p = phraseList.getTopPhrase(coveredDocs);
				}
			if(p == null) break;
		
			ICluster c = new FrequentPhraseCluster(getNextClusterId(), context, parent);
			final int[] terms = p.getTerms();
			((FrequentPhraseCluster) c).setLabelTerms(terms);
			final String label = context.getPhraseLabel(p.getDocIds(), terms);
			if(!StringUtils.equals(FrequentPhraseCluster.OTHER_LABEL, label)) 
				c.setLabel(label);
			else
				c.setLabel(label.toLowerCase() + "."); //prevent possible label conflict with our Other cluster
			
			//get all docs phrase terms + terms in parent labels are found in
			final OpenBitSet pDocs = getDocumentsContainingTerms(p.terms, docs); //indexes into docs from allTermsDocumentIndexes
			//remove already covered docs
			//pDocs.remove(coveredDocs);
			//set remainder
			for(int i = 0; i < pDocs.size(); i++)
				if(pDocs.fastGet(i)){
					c.addDocument(docs[i]);
					coveredDocs.fastSet(i);
				}

			clusters.add(c);
			
			p = phraseList.getTopPhrase(coveredDocs);
		}
		
		
		//any left over uncovered docs are put in the Other cluster
		ICluster c = new FrequentPhraseCluster(getNextClusterId(), context, parent);
		int cnt = 0;
		for(int i = 0; i < docs.length; i++)
			if(!coveredDocs.fastGet(i)){
				c.addDocument(docs[i]);
				cnt++;
			}
		if(cnt > 0){
			//create the OTHER cluster only if there are remaining docs
			c.setLabel(FrequentPhraseCluster.OTHER_LABEL);
			clusters.add(c);
		}
		
		return clusters;
	}
	
	/**
	 * Retrieve documents that contain all the terms in the phrase 
	 * and reduce to the OpenBitSet for this level of documents
	 * @param terms Terms of current label
	 * @param docs
	 * @return
	 */
	protected OpenBitSet getDocumentsContainingTerms(int[] terms, int[] docs){
		OpenBitSet phraseDocs = this.context.getDocumentsContainingTerms(terms);
		OpenBitSet localPhraseDocs = new OpenBitSet(docs.length);
		for(int i = 0; i < docs.length; i++)
			if(phraseDocs.fastGet(docs[i])) localPhraseDocs.fastSet(i);
		return localPhraseDocs;
	}
	
	/**
	 * Cluster a set of documentsToCluster provided as an array of indexes within the
	 * term document matrix.  Provides a root cluster with an attached
	 * List<ICluster> of 1st level children clusters.
	 * @param docs
	 * @return
	 */
	public ICluster cluster(int[] docs){
		ICluster root = new FrequentPhraseCluster(getNextClusterId(), context);
		root.setLevel(0);
		root.deduceLabel();
		root.setChildren(levelCluster(root, docs));
		return root;
	}

}
