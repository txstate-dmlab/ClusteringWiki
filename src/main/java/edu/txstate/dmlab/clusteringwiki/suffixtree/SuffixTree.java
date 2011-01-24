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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.txstate.dmlab.clusteringwiki.util.NumberUtils;

/**
 * Direct implementation of Esko Ukkonen's suffix tree algorithm for
 * building a suffix tree on-line.  Builds a suffix tree of either integer or
 * char input lists.  Other collection type objects (ex document text) can be parsed 
 * into a list of numeric integer item ids using a type of context index of items.
 * Then an integer based suffix tree can be built for the objects.
 * 
 * @see "E. Ukkonen, On-line construction of suffix trees, Algorithmica, 1995, volume 14, number 3, pages 249-260." 
 * 
 * Implementation is similar to and inspired by that in the Carrot2 project
 * without dependency on the hppc library, with callbacks within input, etc. 
 * The Carrot2 implementation is likely slightly faster due to exclusive use of
 * primitive data types, at the cost of dependency on hppc library.
 * @see http://www.carrot2.org
 *  	Carrot2 project.
 * 		Copyright (C) 2002-2010, Dawid Weiss, StanisÅ‚aw OsiÅ„ski.
 * 		All rights reserved.
 * 		Refer to the full license file "carrot2.LICENSE"
 * 		in the root folder of the repository checkout or at:
 * 		http://www.carrot2.org/carrot2.LICENSE
 * 
 * @author David C. Anastasiu
 *
 */
public class SuffixTree {

	/**
	 * Node does not have a link
	 */
	public final static int NO_LINK = Integer.MIN_VALUE;
	
	/**
	 * Node without outgoing edge
	 */
	public final static int NO_EDGE = -1;
	
	/**
	 * Root node
	 */
	public final static int ROOT_NODE = 1;
	
	/**
	 * Leaf node
	 */
	public final static int LEAF_NODE = -1;
	
	/**
	 * The input list being represented by the suffix tree
	 */
	public final ISuffixTreeInput input;
	
	/**
	 * Length of the input list so we don't have to keep
	 * invoking ISuffixTreeInput.length()
	 */
	public final int inputLength;
	
	/**
	 * Nodes list indexed by node number. 
	 * During build the list contains suffix pointers.
	 * After the tree is built it holds the first edge from the given node.
	 */
	protected final List<Integer> nodes = new ArrayList<Integer>();
	
	/**
	 * List of all edges
	 */
	protected final List<Integer> edges = new ArrayList<Integer>();
	
	/**
	 * A hash map of edges between nodes in the suffix tree. The map is
     * keyed by a combination of node (upper 32 bits) and symbol (lower 32 bits). The
     * value is an index in the edges array.
	 */
	protected final Map<Long, Integer> edgeMap = new HashMap<Long, Integer>();
	
	/**
     * Head node, root node.
     */
	protected final int head, root;

    /**
     * Number of slots in the edges list used for each edge.
     */
	protected final int edgeLength;
	
	/**
     * Variables used during tree construction. 
     * @see Ukkonen's algorithm.
     */
	protected int s, k, i;
	protected boolean endPoint;
	
	/**
     * Build a suffix tree for a given input list of symbols.
     */
    public SuffixTree(ISuffixTreeInput theInput, boolean build)
    {
        this.input = theInput;
        this.inputLength = theInput.length();
        
        // Prepare initial conditions.
        this.head = createNode( this.nodes.size() );
        this.root = createNode( this.nodes.size() );
        this.nodes.set(this.root, this.head);
        createEdge(0, this.root, 0, 0);
        this.edgeLength = this.edges.size();
        this.s = this.root;
        
        if(build)
        	build();
    }
    
    /**
     * Build a suffix tree for a given input list of symbols.
     */
    public SuffixTree(ISuffixTreeInput input){
    	this(input, true);
    }
    
    /**
	 * Initiates the tree building algorithm
	 */
    protected final void build(){
    	// Build the tree.
        for (this.k = this.i = 1; i <= this.inputLength; i++)
        {
        	this.input.next(this.i - 1); //let input obj know we're advancing to next element
            update();
            canonize(this.s, this.k, this.i);
        }

        // Connect edges from a single node to speed up iterators.
        for (int i = 0; i < this.nodes.size(); i++)
        	this.nodes.set(i, LEAF_NODE);

        for (Long c : this.edgeMap.keySet())
        {
            final int g = this.edgeMap.get(c);
            final int node = (int) (c >>> 32);
            final int prev = this.nodes.get(node);
            if (prev != LEAF_NODE)
            {
            	this.edges.set(g + 3, prev);
            }
            this.nodes.set(node, g);
        }
    }
    

    
    /**
     * Tree building methods
     */
    
    /**
     * Ukonnen's suffix tree building algorithm update method.
     */
    protected void update(){
        int oldr = this.root;
        while (true)
        {
            int r = testAndSplit(this.i - 1, this.i);
            if (this.endPoint) break;

            createEdge(r, createNode(this.i), this.i, this.inputLength);
            if (oldr != this.root) this.nodes.set(oldr, r);
            oldr = r;

            canonize(this.nodes.get(this.s), this.k, this.i - 1);
        }

        if (oldr != this.root) this.nodes.set(oldr, this.s);
    }

    /**
     * Ukonnen's suffix tree building algorithm test and split method.
     */
    protected final int testAndSplit(int p, int ti)
    {
        if (this.k <= p)
        {
            final int g = findEdge(this.s, this.k);
            assert g >= 0;

            final int gk = this.edges.get(g + 1);
            final int gj = this.edges.get(g + 2);
            final int gs = this.edges.get(g);

            if (this.input.get(ti - 1) == this.input.get(gk + p - this.k))
            {
            	this.endPoint = true;
                return this.s;
            }
            else
            {
                final int r = createNode(gk + p - this.k);
                updateEdge(removeEdge(this.s, this.k), this.s, gk, gk + p - this.k, r);
                createEdge(r, gs, gk + p - this.k + 1, gj);
                this.endPoint = false;
                return r;
            }
        }
        else
        {
        	this.endPoint = findEdge(this.s, ti) >= 0;
            return this.s;
        }
    }

    /**
     * Ukonnen's suffix tree building algorithm canonization method.
     */
    protected void canonize(int s, int k, int p)
    {
        if (p >= k)
        {
            int g = findEdge(s, k);
            int d;
            while (g >= 0 && (d = this.edges.get(g + 2) - this.edges.get(g + 1)) <= p - k)
            {
                k += d + 1;
                s = this.edges.get(g);
                if (k <= p) g = findEdge(s, k);
            }
        }

        this.s = s;
        this.k = k;
    }


    /**
     * Add a new node to the tree
     */
    protected final int createNode(int position)
    {
        final int node = this.nodes.size();
        this.nodes.add(NO_LINK);
        this.input.nodeCreated(node, position); //let input know we've created a new node
        return node;
    }
    
    /**
     * Convert node index and symbol in node to a long to be used 
     * as key in the edge map
     * @param node
     * @param k
     * @return
     */
    protected final long edgeMapKey(final int node, final int symbol){
    	return NumberUtils.combineInts(node, symbol);
    }


    /**
     * Create an edge from node s to node ts, labeled
     * with symbols between k and p (1-based, inclusive).
     */
    protected final int createEdge(int s, int ts, int k, int p)
    {
    	final int edge = this.edges.size();
    	this.edges.add(ts);
    	this.edges.add(k);
    	this.edges.add(p);
    	this.edges.add(NO_EDGE);
        if(s > 0)
        	this.edgeMap.put(edgeMapKey(s, this.input.get(k - 1)), edge);
        return edge;
    }

    /**
     * Update an existing edge to store an edge from node s
     * to node ts, labeled with symbols between k and
     * p (1-based, inclusive).
     */
    protected final void updateEdge(int edge, int s, int k, int p, int ts)
    {
    	this.edges.set(edge, ts);
    	this.edges.set(edge + 1, k);
    	this.edges.set(edge + 2, p);
    	this.edgeMap.put(edgeMapKey(s, this.input.get(k - 1)), edge);
    }


    /**
     * Find a transition from node s, labeled with symbol at index
     * k - 1 in the input list.
     */
    protected final int findEdge(int s, int k)
    {
        if(s == this.head) return 0;
        return findSymbolEdge(s, this.input.get(k - 1));
    }

    /**
     * Remove the transition from node s, labeled with symbol at index
     * k - 1 and return its slot in the edges array.
     */
    protected int removeEdge(int s, int k)
    {
        assert s != this.head;
        return this.edgeMap.remove(edgeMapKey(s, this.input.get(k - 1)));
    }

    /**
     * @return Return the number of edges (edges) in the tree.
     */
    public final int getEdgeCount()
    {
        return (this.edges.size() / this.edgeLength) - 1;
    }

    /**
     * @return Return the number of nodes in the tree.
     */
    public final int getNodeCount()
    {
        return this.nodes.size() - 1;
    }

    /**
     * See if this suffix tree contains a path (suffix) from the root node to a
     * leaf node corresponding to the given inputList.
     */
    public boolean containsSuffix(ISuffixTreeInput inputList)
    {
        int node = this.root;
        int i = 0;
        while (true)
        {
            // Find an edge leaving the current node marked with symbol inputList[i].
            final int edge = findSymbolEdge(node, inputList.get(i));
            if (edge < 0)
            {
                // Different characters on explicit node.
                return false;
            }

            // Follow the edge, checking symbols on the way.
            int j = getSubphraseStart(edge);
            final int m = getSubphraseEnd(edge) + 1;
            for (;i < inputList.length() && j < m; j++, i++)
            {
                if (inputList.get(i) != this.input.get(j))
                {
                    // Different characters on implicit node.
                    return false;
                }
            }

            if (i == inputList.length())
            {
                // End of input must be aligned with the tree's leaf node.
                return j == this.inputLength;
            }

            // Follow to the child node.
            node = goToNode(edge);
        }
    }
    

    
    /**
     * @return root node.
     */
    public int getRootNode()
    {
        return this.root;
    }

    /**
     * Check if node is a leaf (has no outgoing edges).
     */
    public final boolean isLeafNode(int node)
    {
        return this.nodes.get(node) == LEAF_NODE;
    }

    /**
     * @return the index of the first edge from a given node or NO_EDGE if a
     * given node has no edges.
     */
    public final int firstEdge(int node)
    {
        return this.nodes.get(node);
    }

    /**
     * @return the index of the next edge (sibling) or NO_EDGE if
     * edge is the last edge in the node.
     */
    public final int nextEdge(int edge)
    {
        return this.edges.get(edge + 3);
    }

    /**
     * Find an edge from node, labeled with a given symbol.
     * Returns NO_EDGE if no such edge exists.
     */
    public final int findSymbolEdge(int node, int symbol)
    {
    	final Long key = edgeMapKey(node, symbol);
        return this.edgeMap.containsKey( key ) ? 
        		this.edgeMap.get(key) : NO_EDGE;
    }

    /**
     * @return the target node for a given edge.
     */
    public int goToNode(int edge)
    {
        return this.edges.get(edge);
    }

    /**
     * @return the edge label's start index (inclusive).
     */
    public int getSubphraseStart(int edge)
    {
        return this.edges.get(edge + 1) - 1;
    }

    /**
     * @return the edge label's end index (inclusive).
     */
    public int getSubphraseEnd(int edge)
    {
        return this.edges.get(edge + 2) - 1;
    }
    
    /**
     * Walks the nodes and edges of the suffix tree, depth-first.
     */
    public void walk(final ISuffixTreeVisitor suffixTreeVisitor)
    {
        visitNode(this.root, suffixTreeVisitor);
    }

    /**
     * Start visiting from a given node.
     */
    public void visitNode(final int node, final ISuffixTreeVisitor suffixTreeVisitor)
    {
        if (suffixTreeVisitor.before(node))
        {
            int edge = firstEdge(node);
            while (edge != NO_EDGE)
            {
                final int toNode = this.edges.get(edge);
                if (suffixTreeVisitor.visit(node, toNode, getSubphraseStart(edge), getSubphraseEnd(edge)))
                {
                	visitNode(toNode, suffixTreeVisitor);
                }
                edge = nextEdge(edge);
            }
            suffixTreeVisitor.after(node);
        }
    }
    
}
