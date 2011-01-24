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
 * Visitor interface for suffix tree traversals.
 * 
 * @see BaseVisitor
 */
public interface ISuffixTreeVisitor
{
    /**
     * Invoked before node is descended into.
     * Returning false omits the subtree of the node.
     */
    public boolean before(int node);

    /**
     * Invoked when an edge is visited.
     * Returning false skips the traversal of toNode
     */
    public boolean visit(int fromNode, int toNode, int startIndex, int endIndex);
    
    /**
     * Invoked after node is fully traversed.
     */
    public void after(int node);

}