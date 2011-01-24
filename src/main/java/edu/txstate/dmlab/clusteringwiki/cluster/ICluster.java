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
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;


/**
 * Public interface defining a generic cluster
 * 
 * @author David C. Anastasiu
 *
 */
public interface ICluster {

	/**
	 * Each cluster must have a uniquue id
	 * @return cluster id
	 */
	public String getId();
	
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id);

	/**
	 * @return the parent
	 */
	public ICluster getParent();
	
	/**
	 * @return the children
	 */
	public List<ICluster> getChildren();

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<ICluster> children);
	
	/**
	 * Get a child cluster by referencing its cluster id
	 * @param id
	 * @return
	 */
	public ICluster getChildById(String id);
	
	/**
	 * @return the path
	 */
	public String getPath();
	
	/**
	 * @return level the cluster level in a possible hierarchy, -1 otherwise
	 */
	public int getLevel();
	
	/**
	 * @param level the cluster level in a possible hierarchy
	 */
	public void setLevel(int level);
	
	/**
	 * Each cluster must have a label
	 * @return cluster label
	 */
	public String getLabel();
	
	/**
	 * set label
	 * @param label
	 */
	public void setLabel(String label);
	
	/**
	 * Come up with a label
	 */
	public void deduceLabel();
	
	/**
	 * Make child labels unique to achieve unique paths
	 */
	public void makeChildLabelsUnique();
	
	/**
	 * Check how many times a label is in use by one of the child nodes
	 */
	public int isChildLabelInUse(String label);
	
	/**
	 * @return the allDocs
	 */
	public List<IClusterDocument> getAllDocs();

	/**
	 * @return the context
	 */
	public ICollectionContext getContext();

	/**
	 * @return the docs
	 */
	public Map<Integer, IClusterDocument> getDocs();
	
	/**
	 * Retrieve the size of the cluster
	 * @return size of cluster.
	 */
	public int size();

	/**
	 * Get document with index i if it exists in cluster
	 * If document not present, null is returned.
	 * @param i document index in allDocs
	 * @return document vector or null
	 */
	public IClusterDocument getDocument(int i);
	
	/**
	 * Get a list of all contained documentsToCluster.  If cluster has
	 * children, children clusters will be flattened
	 * @return
	 */
	public List<IClusterDocument> getDocuments();
	
	/**
	 * Get a list of all contained documentsToCluster ids.  If cluster has
	 * children, children clusters will be flattened
	 * @return
	 */
	@JsonIgnore
	public List<Integer> getDocumentIds();
	
	/**
	 * Get a list of all contained documentsToCluster.  If cluster has
	 * children, children clusters will be flattened
	 * @return
	 */
	public List<String> getDocumentPaths();
	
	/**
	 * Add documentsToCluster with index i in the termDocMatrix
	 * to this cluster
	 * @param i document index in allDocs
	 */
	public void addDocument(int i);
	
	/**
	 * Remove document with index i from this cluster 
	 * @param i document index in allDocs
	 */
	public void removeDocument(int i);
	
	/**
	 * Check whether cluster contains document with index i
	 * @param i document index in allDocs
	 * @return whether document exists in cluster
	 */
	public boolean contains(int i);
	
	/**
	 * @return the errors
	 */
	public List<String> getErrors();

	/**
	 * clear all current errors
	 */
	public void clearErrors();
	
	/**
	 * Add an error to the errors list
	 * @param error the error to be added
	 */
	public void addError(String error);
	

	/**
	 * Build html version of the cluster tree
	 * @return
	 */
	public String getHtmlClusterTree();
	
	/**
	 * Create JSON representation of the cluster node
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException;
	
}
