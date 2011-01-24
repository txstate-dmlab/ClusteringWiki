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

/**
 * Public interface contract for a suffix tree input of int phrases
 * 
 * @author David C. Anastasiu
 *
 */
public interface IPhraseInput {

	/**
	 * Get the document id associated with a given node in the 
	 * suffix tree
	 * @param nodeId
	 * @return
	 */
	public int getNodeDocumentId(int nodeId);
	

    /**
	 * Get the document index associated with a given node in the 
	 * suffix tree
	 * @param nodeId
	 * @return
	 */
	public int getNodeDocumentIndex(int nodeId);
    
}
