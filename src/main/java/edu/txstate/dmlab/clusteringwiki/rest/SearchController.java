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

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.txstate.dmlab.clusteringwiki.sources.BaseCWSearchResultCol;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResultCol;
import edu.txstate.dmlab.clusteringwiki.util.KeyGenerator;
import edu.txstate.dmlab.clusteringwiki.rest.BaseRestController;

/**
 * Controller for search abstraction
 * 
 * @author David C. Anastasiu
 *
 */

@Controller
public class SearchController extends BaseRestController {

	
	/**
	 * Execute search
	 * @param service the service the user has chosen to execute query in
	 * @param icwservice underlying ICWSearcher service to be used when searching
	 * @param query query to be executed
	 * @param numResults number of results to be retrieved
	 * @param start first result to be retrieved
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search/{service}/{icwservice}/{query}/{numResults}/{start}")
	public String search (
			@PathVariable("service") String service, 
			@PathVariable("icwservice") String icwservice, 
			@PathVariable("query") String query, 
			@PathVariable("numResults") String numResults, 
			@PathVariable("start") String start, 
			Model model) throws Exception {
		
		ICWSearchResultCol search = null;
		try {
			search = doSearch(service, icwservice, query, numResults, start);
		} catch (Exception e){
			search = new BaseCWSearchResultCol();
			search.addError(e.getMessage());
			model.addAttribute("search", search);
			return "searchResultsView";
		}
		
		String clusterKey = KeyGenerator.getKey();
		search.setClusterKey(clusterKey);
		
		JSONArray searchParams = new JSONArray();
		searchParams.put(service);
		searchParams.put(icwservice);
		searchParams.put(query);
		searchParams.put(numResults);
		searchParams.put(start);

		model.addAttribute("search", search);
	    return "searchResultsView";
	}
	
	/**
	 * Execute search
	 * @param service
	 * @param icwservice
	 * @param query
	 * @param numResults
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search/{service}/{icwservice}/{query}/{numResults}")
	public String search(
			@PathVariable("service") String service, 
			@PathVariable("icwservice") String icwservice, 
			@PathVariable("query") String query, 
			@PathVariable("numResults") String numResults, 
			Model model) throws Exception{
		
	    return search(service, icwservice, query, numResults, 
	    		String.valueOf(DEFAULT_SEARCH_START), model);
	}
	
	/**
	 * Execute search
	 * @param service
	 * @param icwservice
	 * @param query
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search/{service}/{icwservice}/{query}")
	public String search(
			@PathVariable("service") String service, 
			@PathVariable("icwservice") String icwservice, 
			@PathVariable("query") String query, 
			Model model) throws Exception{
		
	    return search(service, icwservice, query, String.valueOf(DEFAULT_SEARCH_NUM_RESULTS), 
	    	String.valueOf(DEFAULT_SEARCH_START), model);
	}
	
}
