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

public interface ISuffixTree {

	
	/**
     * @return Return the number of edges (edges) in the tree.
     */
    public int getEdgeCount();

    /**
     * @return Return the number of nodes in the tree.
     */
    public int getNodeCount();

    /**
     * See if this suffix tree contains a path (suffix) from the root node to a
     * leaf node corresponding to the given inputList.
     */
    public boolean containsSuffix(ISuffixTreeInput inputList);
    
    /**
     * @return root node.
     */
    public int getRootNode();

    /**
     * Check if node is a leaf (has no outgoing edges).
     */
    public boolean isLeafNode(int node);

    /**
     * @return the index of the first edge from a given node or NO_EDGE if a
     * given node has no edges.
     */
    public int firstEdge(int node);

    /**
     * @return the index of the next edge (sibling) or NO_EDGE if
     * edge is the last edge in the node.
     */
    public int nextEdge(int edge);

    /**
     * Find an edge from node, labeled with a given symbol.
     * Returns NO_EDGE if no such edge exists.
     */
    public int findSymbolEdge(int node, int symbol);

    /**
     * @return the target node for a given edge.
     */
    public int goToNode(int edge);

    /**
     * @return the edge label's start index (inclusive).
     */
    public int getSubphraseStart(int edge);

    /**
     * @return the edge label's end index (inclusive).
     */
    public int getSubphraseEnd(int edge);
    
    /**
     * Walks the nodes and edges of the suffix tree, depth-first.
     */
    public void walk(final ISuffixTreeVisitor suffixTreeVisitor);

    /**
     * Start visiting from a given node.
     */
    public void visitNode(final int node, final ISuffixTreeVisitor suffixTreeVisitor);
    
}
