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
 * A o B = x1*x2 + y1*y2
 * dist(A,0) = sqrt((xa-x0)^2 + (ya-y0)^2) == |A|
 * Therefore:
 * sim(A,B) = cos t = A o B/|A|x|B|  
 * 
 * @author David C. Anastasiu
 *
 */
public class CosineSimilarityCalculator implements ISimilarityCalculator {

	/**
	 * Compute similarity value between two document vectors
	 * @param sourceDoc
	 * @param targetDoc
	 * @return similarity
	 */
	@Override
	public double computeSimilarity(IClusterDocument sourceDoc, IClusterDocument targetDoc) {
		int sourceIndex = 0;
		int targetIndex = 0;
		int[] source = sourceDoc.getTerms();
		double[] sourceWeights = sourceDoc.getTermWeights();
		int[] target = targetDoc.getTerms();
		double[] targetWeights = targetDoc.getTermWeights();
		double dotProduct = 0.0D;
		double sourceNorm = 0.0D;
		double targetNorm = 0.0D;
		while(sourceIndex < source.length || targetIndex < target.length){
			int s = sourceIndex < source.length ? source[sourceIndex] : Integer.MAX_VALUE;
			int t = targetIndex < target.length ? target[targetIndex] : Integer.MAX_VALUE;
			if(s == t){
				dotProduct += sourceWeights[sourceIndex] * targetWeights[targetIndex];
				sourceNorm += sourceWeights[sourceIndex] * sourceWeights[sourceIndex];
				targetNorm += targetWeights[targetIndex] * targetWeights[targetIndex];
				sourceIndex++;
				targetIndex++;
			} else if (s < t){
				sourceNorm += sourceWeights[sourceIndex] * sourceWeights[sourceIndex];
				sourceIndex++;
			} else {
				targetNorm += targetWeights[targetIndex] * targetWeights[targetIndex];
				targetIndex++;
			}
		}
		if(sourceNorm == 0 || targetNorm == 0)
			return 0.0D;
		sourceNorm = Math.sqrt(sourceNorm);
		targetNorm = Math.sqrt(targetNorm);
		return dotProduct / (sourceNorm * targetNorm);
	}
	
	
	/**
	 * Compute similarity between two strings
	 * Strings should already be analyzed (stemmed, etc.)
	 * @param source
	 * @param target
	 * @return
	 */
	public double computeSimilarity(String source, String target){
		
		//NOT YET IMPLEMENTED
		
		return 0.0D;
	}
	
	/**
	 * Compute similarity between two int arrays
	 * Strings should already be analyzed (stemmed, etc.)
	 * @param source
	 * @param target
	 * @return
	 */
	public double computeSimilarity(int[] source, int[] target){

		//NOT YET IMPLEMENTED
		
		return 0.0D;
	}

}
