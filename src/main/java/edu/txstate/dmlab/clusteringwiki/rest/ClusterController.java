package edu.txstate.dmlab.clusteringwiki.rest;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.cluster.BaseCluster;
import edu.txstate.dmlab.clusteringwiki.cluster.ClusterEditor;
import edu.txstate.dmlab.clusteringwiki.cluster.HierarchicalKMeansClusterer;
import edu.txstate.dmlab.clusteringwiki.cluster.HierarchicalFrequentPhraseClusterer;
import edu.txstate.dmlab.clusteringwiki.cluster.ICluster;
import edu.txstate.dmlab.clusteringwiki.cluster.IClusterEditor;
import edu.txstate.dmlab.clusteringwiki.cluster.IClusterer;
import edu.txstate.dmlab.clusteringwiki.cluster.ISimilarityCalculator;
import edu.txstate.dmlab.clusteringwiki.cluster.JaccardSimilarityCalculator;
import edu.txstate.dmlab.clusteringwiki.cluster.KMeansClusterer;
import edu.txstate.dmlab.clusteringwiki.cluster.FrequentPhraseClusterer;
import edu.txstate.dmlab.clusteringwiki.cluster.MinDocIndexJSONClusterQueue;
import edu.txstate.dmlab.clusteringwiki.dao.IClusterEditDao;
import edu.txstate.dmlab.clusteringwiki.dao.IQueryDao;
import edu.txstate.dmlab.clusteringwiki.entity.ClusterEdit;
import edu.txstate.dmlab.clusteringwiki.entity.Query;
import edu.txstate.dmlab.clusteringwiki.entity.User;
import edu.txstate.dmlab.clusteringwiki.eval.ExecutionTimes;
import edu.txstate.dmlab.clusteringwiki.preprocess.CollectionContext;
import edu.txstate.dmlab.clusteringwiki.preprocess.ICollectionContext;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResult;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResultCol;
import edu.txstate.dmlab.clusteringwiki.rest.BaseRestController;

/**
 * Controller class for all cluster related functionality
 * 
 * @author David C. Anastasiu
 *
 */
@Controller
public class ClusterController extends BaseRestController {


	public enum CLUSTER_TYPES {
		FLAT,
		HIERARCHICAL,
		STC,
		HSTC
	}
	
	public static Integer allUserId = null;
	
	@Autowired
	private IQueryDao queryDao;
	
	@Autowired
	private IClusterEditDao clusterEditDao;
	
	/**
	 * Id assigned to execution timers container
	 */
	public int executionTimersId = -1;
	
	/**
	 * @return the queryDao
	 */
	public IQueryDao getQueryDao() {
		return queryDao;
	}

	/**
	 * @param queryDao the queryDao to set
	 */
	public void setQueryDao(IQueryDao theQueryDao) {
		queryDao = theQueryDao;
	}



	/**
	 * @return the executionTimersId
	 */
	public int getExecutionTimersId() {
		return executionTimersId;
	}

	/**
	 * @param executionTimersId the executionTimersId to set
	 */
	public void setExecutionTimersId(int theExecutionTimersId) {
		executionTimersId = theExecutionTimersId;
	}

	/**
	 * Cluster documentsToCluster retrieved by search
	 * @param clusteringAlgo the type of cluster to create - flat (0), hierarchical (1)
	 * @param service the service the user has chosen to execute query in
	 * @param icwservice underlying ICWSearcher service to be used when searching
	 * @param query query to be executed
	 * @param numResults number of results to be retrieved
	 * @param start first result to be retrieved
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/clusterJson/{clusteringAlgo}/{service}/{icwservice}/{query}/{numResults}/{start}/{includeEdits}")
	public void clusterJson (
			@PathVariable("clusteringAlgo") String clusteringAlgo, 
			@PathVariable("service") String service, 
			@PathVariable("icwservice") String icwservice, 
			@PathVariable("query") String query, 
			@PathVariable("numResults") String numResults, 
			@PathVariable("start") String start,
			@PathVariable("includeEdits") String inclEdits,
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model
		) throws Exception {
		
		int includeEdits = 0;
		try {
			includeEdits = Integer.parseInt(inclEdits);
		} catch (NumberFormatException e) { /* do nothing */ }
		
		executionTimersId = ExecutionTimes.initiateTimers();
		ExecutionTimes.startTimer(executionTimersId, "total");
		
		//Execute search
		ICWSearchResultCol search = null;
		query = query.trim().toLowerCase();
		try {
			ExecutionTimes.startTimer(executionTimersId, "search");
			search = doSearch(service, icwservice, query, numResults, start);
			
			//if topic query, remove topic id from query string - not important for clustering
			if(query.indexOf("topic:") > -1){
				final int p = query.indexOf("topic:");
				int s = query.indexOf(" ", p + 1);
				if( s < 0 ){
					query = URLDecoder.decode(query, "UTF-8");
					s = query.indexOf(" ", p + 1);
				}
				if(s > 0)
					query = query.substring(s + 1);
			}
	
			ExecutionTimes.stopTimer(executionTimersId, "search");
			ExecutionTimes.startTimer(executionTimersId, "cluster");
			
			//identify user id for aggregated user
			if(allUserId == null){
				User u = applicationUser.getUserDao().selectUserByEmail("all");
				if(u == null){
					sendOutput(response, "{\"error\":\"The agregated user 'all' does not exist.\"}");
					return;
				}
				allUserId = u.getId();
			}
			
			//analyze query text
			//the cluster root
			ICluster root = clusterResults(search, query, clusteringAlgo);
			
			ExecutionTimes.stopTimer(executionTimersId, "cluster");
			
			//analyze and save query if necessary
			ICollectionContext ctx = root.getContext();
			String analyzedQuery = ctx.getAnalyzedQuery();

			int queryId = -1;
			int allQueryId = -1;
			
			//create JSON versions of cluster and results
			JSONObject cluster = null;
			JSONObject results = search.toJSON();
			
			//if initial search, store query updates and include edits in the cluster
			if(includeEdits == 1) {

				ExecutionTimes.startTimer(executionTimersId, "preferences");
				
				//identify or store query in database
				Integer userId = applicationUser.getUserId();
				Query q = findBestMatchingQuery(query, analyzedQuery, userId, allUserId, 
						search, icwservice, Integer.valueOf(numResults), Integer.valueOf(clusteringAlgo));
				Query qAll = queryDao.selectExistingUserQuery(allUserId, icwservice, 
						Integer.valueOf(numResults), query);
				
				if(applicationUser.isLoggedIn()){
					//save query for user 'all'
					if(qAll == null){
						List<String> urls = search.getTopKResponseUrls( ApplicationSettings.getTopKQueryUrls() );
						qAll = new Query(allUserId, icwservice, Integer.valueOf(numResults), 
								query, null, urls);
						qAll.setParsedText(analyzedQuery);
						queryDao.saveQuery(qAll);
					} else {
						long now = System.currentTimeMillis() - MILLISECS_PER_DAY;
						long then = qAll.getExecutedOn() != null ? qAll.getExecutedOn().getTime() : now;
						if( Long.valueOf(now).compareTo(then) > 0){
							//update query responses if they have not been updated in more than k days, k=1
							List<String> urls = search.getTopKResponseUrls( ApplicationSettings.getTopKQueryUrls() );
							qAll.setExecutedOn(new Date());
							qAll.updateResponses(urls);
							qAll.setParsedText(analyzedQuery);
							queryDao.saveQuery(qAll);
						}	
					}
				}
				List<ClusterEdit> edits = null;
				
				if(qAll != null) allQueryId = qAll.getId();
				
				//retrieve preferences to be applied to cluster
				if(q != null){ 
					queryId = q.getId();
					edits = clusterEditDao.selectClusterEditsForUserQuery(queryId, 
						Integer.valueOf(clusteringAlgo), q.getUserId().equals(allUserId));
				}
				if(edits != null && edits.size() > 0){ 
					//apply preferences to cluster
					IClusterEditor clusterEditor = new ClusterEditor(root, edits, ctx);
					cluster = clusterEditor.applyUserEdits();
				} else {
					cluster = root.toJSON();
				}
				
				ExecutionTimes.stopTimer(executionTimersId, "preferences");
			} else {
				cluster = root.toJSON();
			}
			
			//Recursively sort cluster labels in each level
			ExecutionTimes.startTimer(executionTimersId, "sort");
			if(cluster.get("children") != null && 
					cluster.get("children") instanceof JSONArray) {
				final JSONArray children = cluster.getJSONArray("children");
				if(children.length() > 0){
					final MinDocIndexJSONClusterQueue sortQueue = new MinDocIndexJSONClusterQueue(children.length(), cluster);
					cluster = sortChildren(cluster, sortQueue.clusterPages);
				}
			}
			ExecutionTimes.stopTimer(executionTimersId, "sort");
			
			//attach custom result label map
			cluster.put("customResultLabels", ctx.getCustomResultLabels());			
			
			
			ExecutionTimes.stopTimer(executionTimersId, "total");
			
			JSONObject timers = ExecutionTimes.toJSON(executionTimersId);
			
			
			//no more need for these timers
			ExecutionTimes.clear(executionTimersId);
			
			sendOutput(response, "{\"success\":true,\"cluster\":" + 
				cluster + ",\"results\":" + results + ",\"query_id\":" + queryId + 
				",\"all_query_id\":" + allQueryId + ",\"timers\":" + timers + "}");
			
		} catch (Exception e){
			sendOutput(response, "{\"error\":" + JSONObject.quote( e.getMessage() ) + "}");
			return;
		}
		
	}
	
	/**
	 * Recursively sort cluster labels in each level
	 * @param cluster
	 * @return
	 * @throws JSONException 
	 */
	protected JSONObject sortChildren(final JSONObject cluster, 
			final Map<String, List<Integer>> clusterPages) throws JSONException{
		JSONArray children = cluster.optJSONArray("children");
		if(children == null)
			return cluster;
		final int numChildren = children.length();
		if(numChildren < 2) return cluster;
		final MinDocIndexJSONClusterQueue sortQueue = 
			new MinDocIndexJSONClusterQueue(numChildren, clusterPages);
		for(int i = 0; i < numChildren; i++) {
			JSONObject child = sortChildren(children.getJSONObject(i), clusterPages);
			sortQueue.add(child);
		}
		for(int i = 0; i < numChildren; i++)
			children.put(i, sortQueue.pop());
		cluster.put("children", children);
		return cluster;
	}
	
	
	/**
	 * Find the best matching query for a user
	 * @param search
	 * @param userId
	 * @return
	 */
	protected Query findBestMatchingQuery(String query, String analyzedQuery, 
			Integer userId, Integer allUserId, ICWSearchResultCol search, 
			String service, Integer numResults, Integer clusteringAlgo){
		
		Query q;
		
		if(applicationUser.isLoggedIn()){
			q = transfer(query, analyzedQuery, userId, allUserId, true,
					search, service, numResults, clusteringAlgo);
			//save query for logged in user
			if(q == null){
				List<String> urls = search.getTopKResponseUrls( ApplicationSettings.getTopKQueryUrls() );
				q = new Query(userId, service, numResults, 
						query, null, urls);
				q.setParsedText(analyzedQuery);
				queryDao.saveQuery(q);
			} else {
				long now = System.currentTimeMillis() - MILLISECS_PER_DAY;
				long then = q.getExecutedOn() != null ? q.getExecutedOn().getTime() : now;
				if( Long.valueOf(now).compareTo(then) > 0){
					//update query responses if they have not been updated in more than k days, k=1
					List<String> urls = search.getTopKResponseUrls( ApplicationSettings.getTopKQueryUrls() );
					q.setExecutedOn(new Date());
					q.updateResponses(urls);
					queryDao.saveQuery(q);
				}
			}
		} else {
			//identify best match query transfer query (if any)
			q = transfer(query, analyzedQuery, allUserId, allUserId, false,
					search, service, numResults, clusteringAlgo);
		}
		
		return q;
	}
	
	
	
	/**
	 * Query transfer - get a similar query if the current query has 
	 * not already been executed
	 * @param query  Executed query
	 * @param analyzedQuery  Analyzed executed query string terms
	 * @param userId  User id for logged in user
	 * @param allUserId  User id for "all" user
	 * @param loggedIn  Whether user is logged in
	 * @param search  Search results collection
	 * @param service  Service used to execute search
	 * @param numResults  Number of results retrieved
	 * @param clusteringAlgo  Clustering algorithm used to cluster results
	 * @return
	 */
	protected Query transfer(String query, String analyzedQuery, 
			Integer userId, Integer allUserId, boolean loggedIn, ICWSearchResultCol search, 
			String service, Integer numResults, Integer clusteringAlgo){
		
		//query for logged in user
		Query q = queryDao.selectExistingUserQuery(userId, service, numResults, query);
		
		if(q != null) return q;
		
		List<Query> matches = queryDao.selectUserQueryMatchingSearch(query, analyzedQuery, allUserId,
				ApplicationSettings.getTermSimQueryResultsLimit());
		
		//find query with largest similarity
		double sim = 0.0D;
		Query qPrime = null;
		ISimilarityCalculator calc = new JaccardSimilarityCalculator();
		for(Query a : matches){
			double currentSim = calc.computeSimilarity(analyzedQuery, a.getParsedText());
			if( Double.compare( currentSim, sim ) > 0 ) {
				qPrime = a;
				sim = currentSim;
			}
		}
		//make sure it is similar enough
		if( Double.compare(sim, ApplicationSettings.getTermSimThreshold()) < 0 || qPrime == null ) return null;
		
		//check the result similarity between the top k results received and the query found
		List<String> responseUrls = search.getTopKResponseUrls( ApplicationSettings.getTopKQueryUrls() );
		Set<String> responseUrlsSet = new HashSet<String>(responseUrls);
		Set<String> queryUrlsSet = qPrime.retrieveTopKQueryResponseUrlsSet();
		Set<String> intersection = new HashSet<String>(responseUrlsSet);
		intersection.removeAll(queryUrlsSet);
		Set<String> union = responseUrlsSet;
		union.addAll(queryUrlsSet);
		sim = intersection.size() / (double) union.size();
		
		//make sure it is similar enough
		if( Double.compare(sim, ApplicationSettings.getResultSimThreshold()) < 0 || qPrime == null ) return null;
		
		//found q' that is similar enough to q
		//save q and copy preferences from q' to q
		if(loggedIn) {
			List<String> urls = search.getTopKResponseUrls( ApplicationSettings.getTopKQueryUrls() );
			//save new queries
			q = new Query(userId, service, numResults, 
					query, null, urls);
			q.setParsedText(analyzedQuery);
			queryDao.saveQuery(q);
			Query qAll = new Query(allUserId, service, numResults, 
					query, null, urls);
			qAll.setParsedText(analyzedQuery);
			queryDao.saveQuery(qAll);
			//associate new edits
			Integer queryId = q.getId();
			List<ClusterEdit> edits = clusterEditDao.selectClusterEditsForUserQuery(
				qPrime.getId(), clusteringAlgo, qPrime.getUserId().equals(allUserId));
			for(ClusterEdit ePrime : edits){
				ClusterEdit e = new ClusterEdit();
				e.setCardinality( ePrime.getCardinality() );
				e.setClusteringAlgo( ePrime.getClusteringAlgo() );
				e.setQueryId( queryId );
				e.setPath1(ePrime.getPath1());
				e.setPath2(ePrime.getPath2());
				e.setPath3(ePrime.getPath3());
				e.setPath4(ePrime.getPath4());
				e.setPath5(ePrime.getPath5());
				clusterEditDao.saveClusterEdit(e);
			}
		} else {
			q = qPrime;
		}
		return q;
	}
	
	/**
	 * Update paths for a given cluster edit
	 * @param qid  Query id of the executed query
	 * @param algo  Algorithm used for clustering
	 * @param request  HttpServletRequest
	 * @param response  HttpServletResponse
	 * @param model  Model
	 * @throws Exception
	 */
	@RequestMapping("/clusterUpdate/{queryId}/{clusteringAlgo}")
	public void savePaths(
			@PathVariable("queryId") String qid, 
			@PathVariable("clusteringAlgo") String algo, 
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model) throws Exception {

		try {
			
			Integer queryId = Integer.valueOf( _cleanExtensions(qid) );
			Integer clusteringAlgo = Integer.valueOf( _cleanExtensions(algo) );
			
			String data = null;
			InputStream is = request.getInputStream();
			
			if(is != null){
				try {
					StringBuilder sb = new StringBuilder();
					String line;
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					data = sb.toString();
				} finally {
					is.close(); 
				}
					
			}
			
			if(data == null){
				sendOutput(response, "{\"error\":\"No data received.\"}");
				return;
			}
			
			JSONArray paths = new JSONArray(data);
		
			for(int i = 0; i < paths.length(); i++){
				JSONObject j = paths.getJSONObject(i);
				Integer cardinality = j.getInt("cardinality");
				JSONArray path = j.getJSONArray("lPath");
				
				clusterEditDao.updatePath(queryId, clusteringAlgo, 
					path.optString(1).trim(), path.optString(2).trim(), path.optString(3).trim(), 
					path.optString(4).trim(), path.optString(5).trim(), cardinality);
			}
			
			sendOutput(response, "{\"success\":true}");
		
		} catch (Exception e){
			sendOutput(response, "{\"error\":" + JSONObject.quote( e.getMessage() ) + "}");
			return;
		}
	}
	
	/**
	 * Delete all edits for a given executed query
	 * @param qid
	 * @param algo
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/deleteClusterEdits/{queryId}/{clusteringAlgo}")
	public void deleteClusterEdits (
			@PathVariable("queryId") String qid, 
			@PathVariable("clusteringAlgo") String algo, 
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model) throws Exception {

		try {
			
			Integer queryId = Integer.valueOf( _cleanExtensions(qid) );
			Integer clusteringAlgo = Integer.valueOf( _cleanExtensions(algo) );
			
			clusterEditDao.deleteClusterEditsForUserQuery(queryId, clusteringAlgo);
			
			sendOutput(response, "{\"success\":true}");
		
		} catch (Exception e){
			sendOutput(response, "{\"error\":" + JSONObject.quote( e.getMessage() ) + "}");
			return;
		}
	}
	
	/**
	 * Retrieve a set of up to 10 most popular (edited by most users) queries with default
	 * parameters (google, 50, ffh)
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/getMostPopularQueries")
	public void getMostPopularQueries (
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model) throws Exception {

		try {
			List<String> queries = queryDao.getMostEditedQueries();
			JSONArray qs =  new JSONArray(queries);
			JSONObject j = new JSONObject();
			j.put("success", true);
			j.put("queries", qs);
			sendOutput(response, j.toString());
		
		} catch (Exception e){
			sendOutput(response, "{\"error\":" + JSONObject.quote( e.getMessage() ) + "}");
			return;
		}
	}

	/**
	 * Cluster a set of retrieved search results
	 * @param search  Search resutls collection
	 * @param query  Executed query
	 * @param clusteringAlgo  Executed clustering algo
	 * @return
	 * @throws Exception
	 */
	private ICluster clusterResults(ICWSearchResultCol search, String query, String clusteringAlgo)
		throws Exception{
	
		//the cluster root
		ICluster root = new BaseCluster("0", null);
		root.setLabel(BaseCluster.ROOT_LABEL);
		
		int cType = 0;
		CLUSTER_TYPES type = null;
		try {
			cType = Integer.parseInt(clusteringAlgo);
			type = CLUSTER_TYPES.values()[cType];
		} catch (Exception e){
			throw new Exception("Invalid algorithm parameter received.");
		}
		if(clusteringAlgo == null)
			throw new Exception("Invalid algorithm parameter received.");
		
		//initiate the pre-processing context
		ExecutionTimes.startTimer(executionTimersId, "preprocessing");
		CollectionContext context = new CollectionContext(
			search, 
			new ICWSearchResult.FIELDS[]{
				ICWSearchResult.FIELDS.TITLE, 
				ICWSearchResult.FIELDS.SNIPPET
			},
			query
		);
	
		
		//pre-process the received results
		ExecutionTimes.stopTimer(executionTimersId, "preprocessing");
		
		//cluster data
		ExecutionTimes.startTimer(executionTimersId, "clustering");
		IClusterer clusterer = null;
		if(type.equals(CLUSTER_TYPES.FLAT)){
			clusterer = new KMeansClusterer(context);
		} else if(type.equals(CLUSTER_TYPES.HIERARCHICAL)){
			clusterer = new HierarchicalKMeansClusterer(context);
		} else if(type.equals(CLUSTER_TYPES.STC)){
			clusterer = new FrequentPhraseClusterer(context);
		} else if(type.equals(CLUSTER_TYPES.HSTC)){
			clusterer = new HierarchicalFrequentPhraseClusterer(context);
		}
		root = clusterer.cluster();		
		root.makeChildLabelsUnique();
		ExecutionTimes.stopTimer(executionTimersId, "clustering");
		
		return root;
	}
	
}
