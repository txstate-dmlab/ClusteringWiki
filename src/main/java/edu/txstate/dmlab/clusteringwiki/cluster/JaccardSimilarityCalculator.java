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
 * Classes responsible for computing 
 * document similarities using the document vectors
 * 
 * Implements Jaccard Similarity between two docs
 *   sim(A,B) = (A ^ B) / (A v B)
 * 
 * @author David C. Anastasiu
 *
 */
public class JaccardSimilarityCalculator implements ISimilarityCalculator {

	/**
	 * Compute similarity value between two document vectors
	 * @param sourceDoc
	 * @param targetDoc
	 * @return similarity
	 */
	@Override
	public double computeSimilarity(IClusterDocument sourceDoc, IClusterDocument targetDoc) {
		int intersection = sourceDoc.termsIntersectionCount(targetDoc);
		if(intersection > 0){
			int sourceLength = sourceDoc.getTerms() != null ? sourceDoc.getTerms().length : 0;
			int targetLength = targetDoc.getTerms() != null ? targetDoc.getTerms().length : 0;
			int union = sourceLength + targetLength - intersection;
			return intersection / (double) union;
		}
		return 0.0D;
	}
	
	/**
	 * Compute similarity between two strings
	 * Strings should already be analyzed (stemmed, etc.)
	 * @param source
	 * @param target
	 * @return
	 */
	public double computeSimilarity(String source, String target){
		if(source == null || target == null) return 0.0D;
		String[] s = source.split("\\s+");
		String[] t = target.split("\\s+");
		int intersection = 0;
		for(int i = 0; i < s.length; i++){
			for(int j = 0; j < t.length; j++){
				String test = s[i];
				if(test.equalsIgnoreCase(t[j])){
					intersection++;
					j = target.length();
				}
			}
		}
		if(intersection > 0){
			int union = s.length + t.length - intersection;
			return intersection / (double) union;
		}
		return 0.0D;
	}
	
	/**
	 * Compute similarity between two terms arrays
	 * @param source
	 * @param target
	 * @return
	 */
	public double computeSimilarity(int[] source, int[] target){
		if(source == null || target == null) return 0.0D;
		int intersection = 0;
		for(int i = 0; i < source.length; i++){
			for(int j = 0; j < target.length; j++){
				if(source[i] == target[j]){
					intersection++;
					j = target.length;
				}
			}
		}
		if(intersection > 0){
			int union = source.length + target.length - intersection;
			return intersection / (double) union;
		}
		return 0.0D;
	}

}
