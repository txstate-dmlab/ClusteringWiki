package edu.txstate.dmlab.clusteringwiki.web;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings;
import edu.txstate.dmlab.clusteringwiki.cluster.CosineSimilarityCalculator;
import edu.txstate.dmlab.clusteringwiki.cluster.JaccardSimilarityCalculator;

/**
 * Controller for the admin page
 * 
 * @author David C. Anastasiu
 *
 */
@Controller
public class AdminController extends BaseController {

	/**
	 * Get admin page
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("admin.*")
	public String getAdminPage(HttpServletRequest request, HttpServletResponse response, Model model){
		
		if(!applicationUser.isLoggedIn() || !applicationUser.isAdmin()){
			request.setAttribute("message", "You must be logged in as Administrator to access this page.");
			return "pageError";
		}
		
		String action = request.getParameter("applAction");
		if(action != null && action.equals("saveSettings") && isAjaxRequest(request)){
			
			JSONArray errors = new JSONArray();
			
			ApplicationSettings.setTimingEnabled( request.getParameter("timingEnabled") );
			
			try {
				int topKQueryUrls = Integer.parseInt( request.getParameter("topKQueryUrls") );
				if(topKQueryUrls > -1 && topKQueryUrls < 1001)
					ApplicationSettings.setTopKQueryUrls( topKQueryUrls );
				else
					errors.put("topKQueryUrls must be an integer between 0 and 1000.");
			} catch (NumberFormatException e){
				errors.put("topKQueryUrls must be an integer between 0 and 1000.");
			}
			
			try {
				int maxClusteringIterations = Integer.parseInt( request.getParameter("maxClusteringIterations"));
				if(maxClusteringIterations > -1 && maxClusteringIterations < 101)
					ApplicationSettings.setMaxClusteringIterations(maxClusteringIterations);
				else 
					errors.put("maxClusteringIterations must be an ineger between 0 and 100.");
			} catch (NumberFormatException e){
				errors.put("maxClusteringIterations must be an ineger between 0 and 100.");
			}
			
			String similarityCalculator = request.getParameter("similarityCalculator");
			if(similarityCalculator != null){
				if(similarityCalculator.equals("jaccard")){
					ApplicationSettings.setSimilarityCalculator( 
						new JaccardSimilarityCalculator()
					);
				} else if(similarityCalculator.equals("cosine")){
					ApplicationSettings.setSimilarityCalculator( 
						new CosineSimilarityCalculator()
					);
				} else {
					errors.put("Invalid similarityCalculator choice.");
				}
			}
			
			try {
				int termSimQueryResultsLimit = Integer.parseInt( request.getParameter("termSimQueryResultsLimit") );
				if(termSimQueryResultsLimit > -1 && termSimQueryResultsLimit < 1001)
					ApplicationSettings.setTermSimQueryResultsLimit( termSimQueryResultsLimit );
				else
					errors.put("termSimQueryResultsLimit must be an integer between 0 and 1000.");
			} catch (NumberFormatException e){
				errors.put("termSimQueryResultsLimit must be an integer between 0 and 1000.");
			}
			
			try {
				double termSimThreshold = Double.parseDouble( request.getParameter("termSimThreshold"));
				if(Double.compare(termSimThreshold, 0.0D) >= 0 && Double.compare(termSimThreshold, 1.0D) <= 0)
					ApplicationSettings.setTermSimThreshold(termSimThreshold);
				else 
					errors.put("termSimThreshold must be a double between 0.0 and 1.0.");
			} catch (NumberFormatException e){
				errors.put("termSimThreshold must be a double between 0.0 and 1.0.");
			}
			
			try {
				double resultSimThreshold = Double.parseDouble( request.getParameter("resultSimThreshold"));
				if(Double.compare(resultSimThreshold, 0.0D) >= 0 && Double.compare(resultSimThreshold, 1.0D) <= 0)
					ApplicationSettings.setResultSimThreshold(resultSimThreshold);
				else 
					errors.put("resultSimThreshold must be a double between 0.0 and 1.0.");
			} catch (NumberFormatException e){
				errors.put("resultSimThreshold must be a double between 0.0 and 1.0.");
			}
			
			if(errors.length() == 0){
				sendOutput(response, "{\"success\":true}");
			} else {
				sendOutput(response, "{\"errors\":" + errors + "}");
			}
			
			return null;
		}
		
		return "admin";
	}
	
}
