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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import edu.txstate.dmlab.clusteringwiki.app.IApplicationUser;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearchResultCol;
import edu.txstate.dmlab.clusteringwiki.sources.ICWSearcher;

/**
 * Controller utility class for methods needed by multiple 
 * search and clustering controllers
 * 
 * @author David C. Anastasiu
 *
 */
public class BaseRestController {

	IApplicationUser applicationUser;
	
	@Autowired
	protected Map<String, ICWSearcher> cWSearchers = null;
	
	public final static int DEFAULT_SEARCH_NUM_RESULTS = 100;
	public final static int DEFAULT_SEARCH_START = 1;
	public final static int DEFAULT_SEARCH_MAX = 500;
	//addition to cluster key to create search params cache key
	public final static String SEARCH_PARAMS_KEY = "_searchParams";
	
	public final static long MILLISECS_PER_DAY = (24 * 60 * 60 * 1000);

	/**
	 * @return the applicationUser
	 */
	public IApplicationUser getApplicationUser() {
		return applicationUser;
	}

	/**
	 * @param applicationUser the applicationUser to set
	 */
	@Autowired
	public void setApplicationUser(IApplicationUser theApplicationUser) {
		applicationUser = theApplicationUser;
	}
	
	/**
     * Check whether the request received is an ajax request
     * @param httpRequest - HttpServletRequest object
     * @return boolean - true if session is expired
     */
    protected boolean  isAjaxRequest(HttpServletRequest httpRequest)
    {
       String requestedWithHeader = httpRequest.getHeader("X-REQUESTED-WITH");
       return requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest");
    }
	
	/**
	 * Send output back to the browser
	 * @param response
	 * @param resp
	 */
	protected void sendOutput(HttpServletResponse response, String resp){
		try {
			response.getOutputStream().write(resp.getBytes());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * remove possibly added extension from a parameter value
	 */
	protected String _cleanExtensions(String param){
		if(param == null) return "";
		int dot = param.lastIndexOf('.');
		if(dot == -1) return param;
		String ext = param.substring(dot + 1, param.length()).toLowerCase();
		String[] extensions = {"xml", "json", "htm", "html"};
		for(int i = 0; i < extensions.length; i++){
			if(extensions[i].equals(ext))
				return param.substring(0, dot);
		}
		return param;
	}
	
	/**
	 * Check if user is logged in
	 * @return
	 */
	protected boolean isLoggedIn(){
		return applicationUser != null && applicationUser.isLoggedIn();
	}
	
	
	/**
	 * Execute a search given criteria
	 * @param service
	 * @param icwservice
	 * @param query
	 * @param numResults
	 * @param start
	 * @return
	 */
	protected ICWSearchResultCol doSearch (
		String service, 
		String icwservice, 
		String query, 
		String numResults, 
		String start) throws Exception {
		
		
		//Sanitize values
		service = service.toLowerCase();
		
		if(cWSearchers == null){
			throw new Exception("No cWSearchers have been configured.");
		}

		if(!cWSearchers.containsKey(service)){
			throw new Exception("Invalid Searcher service.");
		}
		if(!StringUtils.hasLength(query)){
			throw new Exception("Query must be provided.");
		}
		

		icwservice = _cleanExtensions(icwservice);
		query = _cleanExtensions(query);
		numResults = _cleanExtensions(numResults);
		start = _cleanExtensions(start);

		
		ICWSearcher cWSearcher = cWSearchers.get(service);
		cWSearcher.setKey(service);
		
		int numRes = DEFAULT_SEARCH_NUM_RESULTS;
		try {
			numRes = Integer.parseInt(numResults);
		} catch (NumberFormatException nfe){};
		
		int strt = DEFAULT_SEARCH_START;
		try {
			strt = Integer.parseInt(start);
		} catch (NumberFormatException nfe){};
		
		if(strt < 0){
			throw new Exception("Start value must be positive.");
		}
		
		if(numRes > DEFAULT_SEARCH_MAX){
			throw new Exception("The maximum number of requested results is " + 
				DEFAULT_SEARCH_MAX + ".");
		}
		
		
		//Execute search
		return cWSearcher.search(icwservice, query, strt, Integer.valueOf(numRes) );

	}
	
}
