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

import java.util.List;

import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Frequent phrase cluster representation
 * 
 * @author David C. Anastasiu
 *
 */
public class FrequentPhraseCluster extends BaseCluster implements ICluster {

	protected int[] labelTerms = null;
	
	/**
	 * Constructor
	 * @param theId
	 * @param theContext
	 * @param theParent
	 * @param theChildren
	 */
	public FrequentPhraseCluster(String theId, ICollectionContext theContext,
			ICluster theParent, List<ICluster> theChildren) {
		super(theId, theContext, theParent, theChildren);
	}

	/**
	 * Constructor
	 * @param theId
	 * @param theContext
	 * @param theParent
	 */
	public FrequentPhraseCluster(String theId, ICollectionContext theContext,
			ICluster theParent) {
		super(theId, theContext, theParent);
	}

	/**
	 * Constructor
	 * @param theId
	 * @param theContext
	 */
	public FrequentPhraseCluster(String theId, ICollectionContext theContext) {
		super(theId, theContext);
	}

	/**
	 * @return the labelTerms
	 */
	public int[] getLabelTerms() {
		return labelTerms;
	}
	
	/**
	 * Get a string representation of the label terms
	 * @return
	 */
	public String getLabelTermsString(){
		String s = "";
		int i = 0;
		for(; i < labelTerms.length - 1; i++)
			s += labelTerms[i] + ",";
		if(i < labelTerms.length)
			s += labelTerms[i];
		return s;
	}
	
	/**
	 * Get label terms for this and parent clusters
	 * @return
	 */
	public int[][] getLabelTermsPath(){
		int[][] l = new int[this.level][];
		int i = 0;
		if(this.labelTerms != null)
			l[i++] = this.labelTerms;
		FrequentPhraseCluster p = (FrequentPhraseCluster) this.parent;
		while(p != null){
			final int[] lp = p.getLabelTerms();
			if(lp == null) break;
			l[i++] = lp;
			p = (FrequentPhraseCluster) p.getParent();
		}
		return l;
	}

	/**
	 * @param labelTerms the labelTerms to set
	 */
	public void setLabelTerms(int[] labelTerms) {
		this.labelTerms = labelTerms;
	}

	
}
