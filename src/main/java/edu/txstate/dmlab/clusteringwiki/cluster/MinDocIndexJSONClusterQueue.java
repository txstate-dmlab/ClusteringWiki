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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.util.PriorityQueue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.txstate.dmlab.clusteringwiki.util.NumberUtils;

public class MinDocIndexJSONClusterQueue extends PriorityQueue<JSONObject> {

	/**
	 * Index of cluster ids to page ids within that cluster
	 */
	public final Map<String, List<Integer>> clusterPages;
	
	/**
	 * Initialize queue with an already built clusterPages map
	 * @param size
	 * @param clusterPagesMap
	 */
	public MinDocIndexJSONClusterQueue(int size, Map<String, List<Integer>> clusterPagesMap){
		super.initialize(size);
		assert clusterPagesMap != null;
		clusterPages = clusterPagesMap;
	}
	
	/**
	 * Create a queue while initializing the clusterPages map
	 * @param size
	 * @param root
	 */
	public MinDocIndexJSONClusterQueue(int size, JSONObject root){
		super.initialize(size);
		clusterPages = new HashMap<String, List<Integer>>();
		//initialize clusterPages
		try {
			getClusterPages(root);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get list of pages for this cluster and all children
	 * Method caches pages and should only be used once all page changes have been completed
	 * @param cluster
	 * @return
	 */
	protected List<Integer> getClusterPages(JSONObject cluster)
		throws JSONException {
		final String cid = cluster.getString("id");
		List<Integer> cPages = clusterPages.get(cid);
		if(cPages != null) return cPages;
		final JSONArray children = cluster.optJSONArray("children");
		cPages = new ArrayList<Integer>();
		if(children != null){
			//cluster has children, thus it will not have pages
			//ask children to report pages and add to list
			for(int i = 0; i < children.length(); i++){
				final JSONObject child = children.getJSONObject(i);
				final List<Integer> childPages = getClusterPages(child);
				cPages.addAll(childPages);
			}
		} else {
			//cluster has pages
			JSONArray pages = cluster.optJSONArray("pages");
			if(pages != null && pages.length() > 0)
				for(int i = 0; i < pages.length(); i++)
					cPages.add(pages.getInt(i));
		}
		Collections.sort(cPages);
		clusterPages.put(cid, cPages);
		return cPages;
	}
	
	
	@Override
	protected boolean lessThan(JSONObject a, JSONObject b) {
		try {
			String l1 = a.getString("label");
			String l2 = b.getString("label");
			if(l1.equals(BaseCluster.OTHER_LABEL)){
				return false;
			}
			if(l2.equals(BaseCluster.OTHER_LABEL)){
				return true;
			}
			List<Integer> docIdsA = getClusterPages(a);
			List<Integer> docIdsB = getClusterPages(b);
			return NumberUtils.orderedLessThan(docIdsA, docIdsB);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
		
	}
}
