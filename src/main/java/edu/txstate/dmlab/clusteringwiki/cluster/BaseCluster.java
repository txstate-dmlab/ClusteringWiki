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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult;

/**
 * Simple POJO to model a cluster. Contains various convenience methods
 * that are used by the clusterers. 
 * 
 * @author David C. Anastasiu
 *
 */
@XStreamAlias("cluster")
public class BaseCluster implements ICluster {

	public static final String PATH_SEPARATOR = ".";
	
	public static final String ROOT_LABEL = "All";
	
	public static final String OTHER_LABEL = "Other";
	
	public static final int MAX_LABEL_LENGTH = 100;
	
	/**
	 * Cluster id
	 */
	protected String id;
	
	/**
	 * Parent cluster in a hierarchy of clusters
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final ICluster parent;
	
	/**
	 * Children clusters in a hierarchy of clusters
	 */
	protected List<ICluster> children;
	
	/**
	 * The path from the root node to this cluster
	 */
	protected final String path;
	
	/**
	 * the cluster level in a possible hierarchy, -1 otherwise
	 */
	protected int level = -1;
	
	/**
	 * Cluster label
	 */
	protected String label = "";
	
	/**
	 * Reference to all processed docs in context
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final List<IClusterDocument> allDocs;
	
	/**
	 * reference to the context data
	 */
	@XStreamOmitField
	@JsonIgnore
	protected final ICollectionContext context;
	
	/**
	 * Documents in the cluster
	 */
	@XStreamOmitField
	@JsonIgnore
	protected Map<Integer, IClusterDocument> docs = null;
	
	
	/**
	 * Possible Errors
	 */
	protected final List<String> errors =  new ArrayList<String>();
	
	/**
	 * Clusters are initiated with references to the termDocumentMatrix,
	 * collection context, and a cluster id.  
	 * @param id termDocumentMatrix
	 * @param context
	 * @param matrix
	 */
	public BaseCluster(String theId, ICollectionContext theContext){
		this.id = theId;
		this.allDocs = theContext != null ? theContext.getAllDocs() : null;
		this.context = theContext;
		this.parent = null;
		this.children = null;
		this.path = this.id;
	}

	/**
	 * Clusters are initiated with references to the termDocumentMatrix,
	 * collection context, and a cluster id.  
	 * @param id termDocumentMatrix
	 * @param context
	 * @param matrix
	 * @param parent parent cluster, if any.  Null otherwise
	 * @param children children clusters, if any, Null otherwise.
	 */
	public BaseCluster(String theId, ICollectionContext theContext, 
			ICluster theParent){
		this.id = theId;
		this.allDocs = theContext != null ? theContext.getAllDocs() : null;
		this.context = theContext;
		this.parent = theParent;
		this.children = null;
		this.path = this.parent != null ? this.parent.getPath() + PATH_SEPARATOR + this.id : this.id;
		this.level = this.parent.getLevel() + 1;
	}
	
	/**
	 * Clusters are initiated with references to the termDocumentMatrix,
	 * collection context, and a cluster id.  
	 * @param id termDocumentMatrix
	 * @param context
	 * @param matrix
	 * @param parent parent cluster, if any.  Null otherwise
	 * @param children children clusters, if any, Null otherwise.
	 */
	public BaseCluster(String theId, ICollectionContext theContext, 
			ICluster theParent, List<ICluster> theChildren){
		this.id = theId;
		this.allDocs = theContext != null ? theContext.getAllDocs() : null;
		this.context = theContext;
		this.parent = theParent;
		this.children = null;
		this.path = this.parent != null ? this.parent.getPath() + PATH_SEPARATOR + this.id : this.id;
		this.level = this.parent.getLevel() + 1;
		this.children = theChildren;
		this.docs = null;  //an internal node will not contain documentsToCluster
	}
	
	/**
	 * Each cluster must have a unique id
	 * @return cluster id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the parent
	 */
	@JsonIgnore
	public ICluster getParent() {
		return this.parent;
	}

	/**
	 * @return the children
	 */
	public List<ICluster> getChildren() {
		return this.children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<ICluster> theChildren) {
		if(theChildren == null){
			this.children = null;
			return;
		}
		if(this.level >= 0)
			for(ICluster c : theChildren)
				c.setLevel(this.level + 1);
		this.children = theChildren;
		this.docs = null; //cluster became internal node
	}
	
	/**
	 * Get a child cluster by referencing its cluster id
	 * @param id
	 * @return
	 */
	@JsonIgnore
	public ICluster getChildById(String id){
		if(id == null) return null;
		if(this.children != null)
			for(ICluster c : this.children)
				if(c.getId() != null && c.getId().equals(id))
					return c;
		return null;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return level the cluster level in a possible hierarchy, -1 otherwise
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * @param level the cluster level in a possible hierarchy
	 */
	public void setLevel(int theLevel) {
		this.level = theLevel;
	}

	/**
	 * Each cluster must have a label
	 * @return cluster label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * set label
	 * @param label
	 */
	public void setLabel(String theLabel){
		this.label = theLabel != null ? theLabel : "No label provided";
		if(this.label.length() > MAX_LABEL_LENGTH) 
			this.label = this.label.substring(0, MAX_LABEL_LENGTH - 1);
	}
	
	/**
	 * Come up with a label for the cluster
	 * Method should be overwritten by sub classes to provide
	 * specific naming of clusters
	 */
	public void deduceLabel(){
		if(this.level == 0) setLabel( ROOT_LABEL );
		else setLabel("Cluster " + this.id);
	}
	
	/**
	 * Make child labels unique to achieve unique label paths
	 */
	public void makeChildLabelsUnique(){
		//check that label is unique among siblings
		int i = 2;
		if(this.parent != null && StringUtils.isNotEmpty(this.label) 
				&& this.parent.isChildLabelInUse(this.label) > 1) {
			final String lb = this.label + " - ";
			while(this.parent.isChildLabelInUse(lb + i) > 0)
				i++;
			this.label = lb + i;
		}
		if(this.children != null)
			for(ICluster c: this.children)
				c.makeChildLabelsUnique();
	}
	
	/**
	 * Check how many times a label is in use by one of the child nodes
	 */
	public int isChildLabelInUse(String label){
		int cnt = 0;
		if(this.children != null)
			for(ICluster c : this.children)
				if( StringUtils.equalsIgnoreCase( c.getLabel().trim(), label.trim())) cnt++;
		return cnt;
	}
	
	/**
	 * @return the allDocs
	 */
	@JsonIgnore
	public List<IClusterDocument> getAllDocs() {
		return this.allDocs;
	}
	
	/**
	 * @return the context
	 */
	@JsonIgnore
	public ICollectionContext getContext() {
		return this.context;
	}

	/**
	 * @return the docs assigned to this cluster, not taking
	 * into consideration any assigned children clusters
	 */
	@JsonIgnore
	public Map<Integer, IClusterDocument> getDocs() {
		return this.docs;
	}
	
	/**
	 * Retrieve the size of the cluster
	 * @return size of cluster.
	 */
	public int size() {
		if(this.docs != null)
			return this.docs.size();
		int size = 0;
		if(this.children != null)
			for(ICluster c: this.children ){
				size += c.size();
			}
		return size;
	}

	/**
	 * Get document with index i if it exists in cluster
	 * If document not present, null is returned.
	 * @param i document index in termDocMatrix
	 * @return document vector or null
	 */
	@JsonIgnore
	public IClusterDocument getDocument(int i){
		if(this.docs != null)
			return this.docs.get(Integer.valueOf(i));
		if(this.children != null)
			for(ICluster c: this.children ){
				IClusterDocument d = c.getDocument(i);
				if(d != null) return d;
			}
		return null;
	}
	
	/**
	 * Get a list of all contained documentsToCluster.  If cluster has
	 * children, children clusters will be flattened
	 * @return
	 */
	@JsonIgnore
	public List<IClusterDocument> getDocuments(){
		if(this.docs != null)
			return new ArrayList<IClusterDocument>( this.docs.values() );
		
		if(this.children != null){
			List<IClusterDocument> dl = new ArrayList<IClusterDocument>();
			for(ICluster c : this.children)
				if(c.getDocuments() != null) 
					dl.addAll(c.getDocuments());
			return dl;
		}
		return null;
	}
	
	/**
	 * Get a list of all contained documentsToCluster ids.  If cluster has
	 * children, children clusters will be flattened
	 * @return
	 */
	@JsonIgnore
	public List<Integer> getDocumentIds(){
		if(this.docs != null){
			List<Integer> docIds = new ArrayList<Integer>( this.docs.size() );
			for(Map.Entry<Integer, IClusterDocument> entry : this.docs.entrySet()){
				IClusterDocument clusterDoc = entry.getValue();
				docIds.add(clusterDoc.getIndex());
			}
			return docIds;
		}
		if(this.children != null){
			List<Integer> dl = new ArrayList<Integer>();
			for(ICluster c : this.children)
				if(c.getDocumentIds() != null) 
					dl.addAll(c.getDocumentIds());
			return dl;
		}
		return null;
	}
	
	/**
	 * Get a list of all contained documentsToCluster.  If cluster has
	 * children, children clusters will be flattened
	 * @return
	 */
	public List<String> getDocumentPaths(){
		if(this.docs != null){
			List<String> paths = new ArrayList<String>( this.docs.size() );
			for(Map.Entry<Integer, IClusterDocument> entry : this.docs.entrySet()){
				IClusterDocument clusterDoc = entry.getValue();
				paths.add(path + PATH_SEPARATOR + clusterDoc.getIndex());
			}
			return paths;
		}
		if(this.children != null){
			List<String> paths = new ArrayList<String>();
			for(ICluster c : this.children)
				paths.addAll(c.getDocumentPaths());
			return paths;
		}
		return null;
	}
	
	/**
	 * Add documentsToCluster with index i in allDocs
	 * to this cluster
	 * @param i document index in termDocMatrix
	 */
	public void addDocument(int i){
		if(this.docs == null) this.docs = new LinkedHashMap<Integer, IClusterDocument>();
		
		this.docs.put(i, this.allDocs.get(i)); //not valid if internal node
	}
	
	/**
	 * Remove document with index i from this cluster 
	 * @param i document index in termDocMatrix
	 */
	public void removeDocument(int i){
		if(this.docs != null)
			this.docs.remove(i);
		if(this.children != null)
			for(ICluster c: this.children ){
				c.removeDocument(i);
			}
	}

	/**
	 * Check whether cluster contains document with index i
	 * @param i document index in termDocMatrix
	 * @return whether document exists in cluster
	 */
	public boolean contains(int i){
		if(this.docs != null)
			return this.docs.containsKey(i);
		if(this.children != null)
			for(ICluster c: this.children )
				if( c.contains(i) ) return true;
		return false;
	}
	
	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return this.errors;
	}

	/**
	 * clear all current errors
	 */
	public void clearErrors(){
		this.errors.clear();
	}
	
	/**
	 * Add an error to the errors list
	 * @param error the error to be added
	 */
	public void addError(String error){
		this.errors.add(error);
	}

	
	/**
	 * Build html version of the cluster tree
	 * @return
	 */
	@JsonIgnore
	public String getHtmlClusterTree(){
		if(size() == 0) return "";
		String r = "<li class=\"";
		r += this.level == 0 ? "root" : this.level == 0 ? "folder-open" : "folder-closed";
		r +="\" id=\"cluster_" + this.id + "\" name=\"" + path + "\">";
		r += "<span>" + this.label + " (" + size() + ")</span>";
		
		if(this.children != null){
			r += "<ul>";
			for(ICluster c: this.children )
				r += c.getHtmlClusterTree();
			r += "</ul>";
		}
		
		r += "</li>";
		
		return r;
	}
	
	
	@Override
	public String toString(){
		String r = "Cluster " + this.id + " - " + this.label + " (" + size() + ")\n";
		if(this.docs != null)
			for(Map.Entry<Integer, IClusterDocument> entry : this.docs.entrySet()){
				ICWSearchResult queryResult = entry.getValue().getResultDoc();
				r += "\t" + queryResult.getId() + ". " + queryResult.getTitle() + "\n";
			}
		if(this.children != null)
			for(ICluster c: this.children )
				r += c.toString() + "\n";
		
		return r;
		
	}
	
	/**
	 * Create JSON representation of the cluster node
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject j = new JSONObject();
		
		//set data
		j.put("id", this.id);
		j.put("label", this.label);
		if(this.docs != null)
			j.put("pages", docs.keySet());
		else 
			j.put("pages", JSONObject.NULL);
		
		if(this.children != null){
			JSONArray k = new JSONArray();
			for(ICluster c: this.children )
				k.put(c.toJSON());
			j.put("children", k);
		} else {
			j.put("children", JSONObject.NULL);
		}
		
		return j;
	}
	
}
