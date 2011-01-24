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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.txstate.dmlab.clusteringwiki.entity.ClusterEdit;
import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;

/**
 * Apply cluster edits retrieved from data store to T-init cluster
 * 
 * @author David C. Anastasiu
 *
 */
public class ClusterEditor implements IClusterEditor {

	/**
	 * Index of result nodes, key = result label, value = page index in allDocs
	 */
	public final Map<String, Set<Integer>> pages;
	
	/**
	 * Index of result node clusters (clusters pages are in)
	 * key = page index in allDocs
	 * value = set of cluster paths
	 */
	public final Map<Integer, Set<String>> pageClusters;
	
	/**
	 * List of edits to be applied
	 */
	public final List<ClusterEdit> edits;
	
	/**
	 * Some results have the same URL + title, causing non-result set duplicate paths problems
	 * customResultLabels identifies duplicates and adds a prefix for copies of result lables
	 */
	public final Map<Integer, String> customResultLabels;
	
	/**
	 * Reference to the doc context
	 */
	public final ICollectionContext context;
	
	/**
	 * JSON data encoding of T-init
	 */
	protected JSONObject root;
	
	/**
	 * Next id to be assigned to new path clusters
	 */
	protected int nextClusterId = 0;
	
	
	/**
	 * Constructor
	 * @param cluster - root cluster
	 * @param edits - retrieved edits to be merged
	 * @throws Exception 
	 */
	public ClusterEditor(ICluster theCluster, List<ClusterEdit> theEdits, ICollectionContext theContext) 
		throws Exception {
		
		edits = theEdits;
		context = theContext;
		
		//get indexes of pages
		pages = theContext.getResultLabelIndex();
		customResultLabels = theContext.getCustomResultLabels();
		
		pageClusters = new HashMap<Integer, Set<String>>();
		populatePageClusters(theCluster);
		nextClusterId++;
		
		root = theCluster.toJSON();
	}
	
	/**
	 * Apply edits
	 * @return
	 */
	public JSONObject applyUserEdits(){
		for(ClusterEdit e : edits){
			if(e.getCardinality() > 0)
				addPath(e);
			else if(e.getCardinality() < 0)
				subtractPath(e);
		}
		
		return root;
	}
	
	
	/**
	 * Method to allow different application of aggregated edits
	 * @return
	 */
	public JSONObject applyAggregatedEdits(){
		return applyUserEdits();
	}
	
	/**
	 * add data to pageClusters from given cluster
	 * @param cluster
	 */
	protected void populatePageClusters(ICluster cluster){
		if(Integer.valueOf( cluster.getId() ) > nextClusterId)
			nextClusterId = Integer.valueOf( cluster.getId() );
		Map<Integer, IClusterDocument> docs = cluster.getDocs();
		if(docs != null) {
			for(Integer docIndex : docs.keySet()){
				//build cluster page indexes
				Set<String> clusters = pageClusters.get(docIndex);
				if(clusters == null) clusters = new HashSet<String>();
				clusters.add(cluster.getId());
				pageClusters.put(docIndex, clusters);
			}
		}
		List<ICluster> children = cluster.getChildren();
		if(children != null)
			for(ICluster c : children)
				populatePageClusters(c);
	}
	
	/**
	 * Get the path for the edit as a list of cluster label strings
	 * @param edit
	 * @return
	 */
	protected List<String> getPathList(ClusterEdit edit){
		List<String> path = new ArrayList<String>(5);
		if(!StringUtils.isBlank(edit.getPath1())) path.add(edit.getPath1());
		if(!StringUtils.isBlank(edit.getPath2())) path.add(edit.getPath2());
		if(!StringUtils.isBlank(edit.getPath3())) path.add(edit.getPath3());
		if(!StringUtils.isBlank(edit.getPath4())) path.add(edit.getPath4());
		if(!StringUtils.isBlank(edit.getPath5())) path.add(edit.getPath5());
		return path;
	}
	
	/**
	 * Get the result id for given path
	 * @param path
	 * @return
	 */
	protected Integer getResultId(List<String> path){
		String resultLabel = path.get(path.size() - 1);
		Set<Integer> p = pages.get(resultLabel);
		if(p != null)
			return p.iterator().next();
		return null;
	}
	
	/**
	 * Get a set of result ids for the given list of paths
	 * @param path
	 * @return
	 */
	protected Set<Integer> getResultIds(List<String> path){
		String resultLabel = path.get(path.size() - 1);
		return pages.get(resultLabel);
	}
	
	/**
	 * Get child cluster JSON structure from parent JSON cluster structure
	 * given label of child cluster
	 * @param cluster
	 * @param label
	 * @return
	 */
	protected JSONObject getChildCluster(JSONObject cluster, String label){
		try {
			JSONArray children = cluster.getJSONArray("children");
			for(int i = 0; i < children.length(); i++){
				String l = children.getJSONObject(i).getString("label");
				if(StringUtils.equalsIgnoreCase(l.trim(), label)) 
					return children.getJSONObject(i);
			}
			return null;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Create a new cluster as JSON structure given cluster label
	 * @param label
	 * @return
	 */
	protected JSONObject newCluster(String label){
		JSONObject c = new JSONObject();
		try {
			c.put("id", nextClusterId++);
			c.put("label", label);
			c.put("pages", JSONObject.NULL);
			c.put("children", JSONObject.NULL);
			c.put("addedPath", true);
		} catch (JSONException e) { /* do nothing */ }
		
		return c;
	}
	
	/**
	 * Add path from cluster edit
	 * @param edit
	 */
	protected void addPath(ClusterEdit edit){
		List<String> path = getPathList(edit);
		Set<Integer> pageIds = getResultIds(path);
		if(pageIds == null) return;
		
		JSONObject c = root;
		for(int i = 0; i < path.size() - 1; i++){ //path contains result label too
			JSONObject child = getChildCluster(c, path.get(i));
			if(child == null){
				//if child cluster does not exist we add it
				child = newCluster(path.get(i));
				try {
					JSONArray children = c.getJSONArray("children");
					children.put(child);
				} catch (JSONException e) {
					JSONArray children = new JSONArray();
					try {
						children.put(child);
						c.put("children", children);
					} catch (JSONException e1) { /* do nothing */ }
				}
			} 
			c = child;
		}
		JSONArray pages = null;
		try {
			pages = c.getJSONArray("pages");
		} catch (JSONException e) { 
			pages = new JSONArray();
		}
		boolean found = false;
		for(int j = 0; j < pages.length(); j++){
			try {
				if(pageIds.contains( pages.getInt(j) )) found = true;
			} catch (JSONException e) { /* do nothing */ }
		}
			
		if(!found){
			Integer pageId = pageIds.iterator().next();
			pages.put(pageId);
			//get cluster id
			String cid;
			try {
				cid = c.getString("id");
			} catch (JSONException e) { cid = null; }
			//update indexes for page that was added
			Set<String> clusters = pageClusters.get(pageId);
			clusters.add(cid);
			pageClusters.put(pageId, clusters);
			
			//signal added pages
			JSONArray addedPages = null;
			try {
				addedPages = c.getJSONArray("addedPages");
			} catch (JSONException e) { 
				addedPages = new JSONArray();
			}
			addedPages.put(pageId);
			try {
				c.put("pages", pages);
				c.put("addedPages", addedPages);
				c.put("addedPath", true);
			} catch (JSONException e) { /* do nothing */ }
		}
	}
	
	
	/**
	 * Remove path from cluster
	 * @param edit
	 */
	protected void subtractPath(ClusterEdit edit){
		List<String> path = getPathList(edit);
		Set<Integer> pageIds = getResultIds(path);
		if(pageIds == null) return;
		
		//page cannot be removed if there is only one copy in cluster
		Set<Integer> validPages = new HashSet<Integer>();
		for(Integer pageId : pageIds){
			Set<String> clusters = pageClusters.get(pageId);
			if(clusters != null && clusters.size() > 1) validPages.add(pageId);
		}
		if(validPages.size() == 0) return; //no page ids that can be removed
		
		JSONObject c = root;
		for(int i = 0; i < path.size() - 1; i++){ //path contains result label too
			c = getChildCluster(c, path.get(i));
			if(c == null) return; //path does not exist
		}
		try {
			JSONArray pages = c.getJSONArray("pages");
			JSONArray p = new JSONArray();
			for(int j = 0; j < pages.length(); j++){
				Integer pageId = pages.getInt(j);
				if(!validPages.contains( pageId )) p.put(pageId);
				else {
					//update indexes of removed pages
					Set<String> clusters = pageClusters.get(pageId);
					clusters.remove(c.getString("id"));
					pageClusters.put(pageId, clusters);
				}
			}
			c.put("pages", p);
			if(p.length() == 0)
				deleteNode(path.subList(0, path.size() - 1));
		} catch (JSONException e) { /* do nothing */ }
	}
	
	/**
	 * Delete a node in the cluster structure associated with given path
	 * @param path
	 */
	protected void deleteNode(List<String> path){

		JSONObject c = root;
		JSONObject parent = null;
		for(int i = 0; i < path.size(); i++){
			parent = c;
			c = getChildCluster(c, path.get(i));
			if(c == null) return; //path does not exist
		}
		if(parent != null)
			try {
				try {
					JSONArray pages = c.getJSONArray("pages");
					if(pages != null && pages.length() > 0) return;
				} catch (JSONException e){ /* do nothing */ }
				JSONArray children = null;
				try {
					children = c.getJSONArray("children");
					if(children != null && children.length() > 0) return;
				} catch (JSONException e){ /* do nothing */ }
				//trim node from parent's children
				children = parent.getJSONArray("children");
				JSONArray newch = new JSONArray();
				for(int j = 0; j < children.length(); j++)
					if(!StringUtils.equals(children.getJSONObject(j).getString("id"), c.getString("id")))
						newch.put( children.getJSONObject(j) );
				parent.put("children", newch);
				if(newch.length() == 0){
					if(path.size() > 1)
						deleteNode(path.subList(0, path.size() - 1));
				}
				
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}

	}
}
