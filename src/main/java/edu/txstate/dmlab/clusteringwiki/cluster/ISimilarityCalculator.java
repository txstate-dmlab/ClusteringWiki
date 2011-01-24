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


/**
 * Interface for classes responsible for computing 
 * document similarities using the document vectors
 * 
 * @author David C. Anastasiu
 *
 */
public interface ISimilarityCalculator {
	
	/**
	 * Compute similarity value between two document vectors
	 * @param sourceDoc
	 * @param targetDoc
	 * @return similarity
	 */
	public double computeSimilarity(IClusterDocument sourceDoc, IClusterDocument targetDoc);

	/**
	 * Compute similarity between two strings
	 * Strings should already be analyzed (stemmed, etc.)
	 * @param source
	 * @param target
	 * @return
	 */
	public double computeSimilarity(String source, String target);
	
	/**
	 * Compute similarity between two strings
	 * Strings should already be analyzed (stemmed, etc.)
	 * @param source
	 * @param target
	 * @return
	 */
	public double computeSimilarity(int[] source, int[] target);
}
