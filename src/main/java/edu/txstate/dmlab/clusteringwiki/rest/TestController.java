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
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.txstate.dmlab.clusteringwiki.dao.TestStepDao;
import edu.txstate.dmlab.clusteringwiki.dao.TestStepExecutionDao;
import edu.txstate.dmlab.clusteringwiki.dao.TestTopicDao;
import edu.txstate.dmlab.clusteringwiki.entity.TestStep;
import edu.txstate.dmlab.clusteringwiki.entity.TestStepExecution;
import edu.txstate.dmlab.clusteringwiki.entity.TestTopic;



/**
 * Controller class for all test related functionality
 * 
 * @author David C. Anastasiu
 *
 */
@Controller
public class TestController extends BaseRestController {

	@Autowired
	private TestStepDao testStepDao;

	@Autowired
	private TestTopicDao testTopicDao;
	
	@Autowired
	private TestStepExecutionDao testStepExecutionDao;
	
	/**
	 * Get the details of the next test step, as JSON structure
	 * @param executionId the execution id for the current test
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/test/get/{executionId}")
	public void getStep (
			@PathVariable("executionId") String execId,
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model
		) throws Exception {
		
		final String executionId = _cleanExtensions(execId);
		
		if(!isValidTest(request, executionId)){
			sendOutput(response, "{\"error\":\"Invalid test execution id.\"}");
			return;
		}
		TestStep step;
		
		try {
			step = testStepDao.getNextStep(executionId);
		} catch (Exception e){
			sendOutput(response, "{\"error\":\"Could not retrieve next execution step.\"}");
			return;
		}
		if(step == null){
			sendOutput(response, "{\"success\":true,\"done\":true," +
				"\"description\":\"Thank you for completing this ClusteringWiki " +
				"test. You may now log out (if necessary) and click the 'End test' link at the top of the " +
				"screen. Ask the study coordinator for your next instructions.\"}");
			return;
		}
		
		final Integer loggedIn = step.getLoggedIn();
		if(loggedIn == 1){
			if(!isLoggedIn()){
				//user should be logged in for this step
				sendOutput(response, "{\"success\":true,\"logIn\":true,\"description\":\"Please log in to execute the next step.\"}");
				return;
			}
		} if(loggedIn == 0) {
			if(isLoggedIn()){
				//user should not be logged in for this step
				sendOutput(response, "{\"success\":true,\"logIn\":false,\"description\":\"Please log out to execute the next step.\"}");
				return;
			}
		}
		//a loggedIn value higher than 1 means we don't care if user is logged in or not
		
		JSONObject j = step.toJSONObject();
		
		TestTopic topic = testTopicDao.selectTestTopicById(step.getTopicId());
		
		if(topic == null){
			j.put("error", "Invalid step topic.");
			sendOutput(response, j.toString());
			return;
		}
		
		j.put("query", topic.getQuery());
		j.put("description", topic.getDescription());
		j.put("narrative", topic.getNarrative());
		j.put("executionId", executionId);
		
		j.put("success", true);
		sendOutput(response, j.toString());
		
	}
	
	
	
	/**
	 * Save the details of the last executed step
	 * @param executionId the execution id for the current test
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/test/put/{executionId}")
	public void saveStep (
			@PathVariable("executionId") String execId,
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model
		) throws Exception {
		
		String executionId = _cleanExtensions(execId);
		
		if(!isValidTest(request, executionId)){
			sendOutput(response, "{\"error\":\"Invalid test execution id.\"}");
			return;
		}
	
		try {
			
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
			
			final JSONObject info = new JSONObject(data);
		
			final Integer stepId = info.getInt("stepId");
			final String cluster = info.getJSONObject("cluster").toString();
			final String times = info.getJSONObject("times").toString();
			final JSONObject tagExecutionInfo = info.getJSONObject("tagExecutionInfo");
			
			int itemCount = 0;
			final Iterator keys = tagExecutionInfo.keys();
			while(keys.hasNext()){
				final String cnt = (String) keys.next();
				final Integer tagCount = Integer.valueOf(cnt);
				TestStepExecution exec = this.testStepExecutionDao.selectTestStepExecution(
						executionId, stepId, tagCount);
				
				if(exec != null){
					sendOutput(response, "{\"error\":\"This step has already been saved. Please contact an administrator.\"}");
					return;
				}
				
				final JSONObject itemInfo = tagExecutionInfo.getJSONObject(cnt);
				
				exec = new TestStepExecution();
				exec.setExecutionId(executionId);
				exec.setTagCount(tagCount);
				exec.setBaseEffort(itemInfo.getDouble("baseEffort"));
				exec.setBaseRelevantEffort(itemInfo.getDouble("baseRelevantEffort"));
				exec.setUserEffort(itemInfo.getDouble("userEffort"));
				exec.setCluster(cluster);
				exec.setTimes(times);
				exec.setStepId(stepId);
				exec.setTags(itemInfo.getJSONObject("tags").toString());
				
				if(this.isLoggedIn()){
					exec.setUid(applicationUser.getUserId());
				}
				
				this.testStepExecutionDao.saveTestStepExecution(exec);
				itemCount++;
			}
			
			//if no tagged items
			if(itemCount == 0){
				TestStepExecution exec = this.testStepExecutionDao.selectTestStepExecution(
						executionId, stepId, 0);
				
				if(exec != null){
					sendOutput(response, "{\"error\":\"This step has already been saved. Please contact an administrator.\"}");
					return;
				}
				
				exec = new TestStepExecution();
				exec.setExecutionId(executionId);
				exec.setTagCount(0);
				exec.setBaseEffort(0.0D);
				exec.setBaseRelevantEffort(0.0D);
				exec.setUserEffort(0.0D);
				exec.setCluster(cluster);
				exec.setTimes(times);
				exec.setStepId(stepId);
				exec.setTags("{}");
				
				if(this.isLoggedIn()){
					exec.setUid(applicationUser.getUserId());
				}
				
				this.testStepExecutionDao.saveTestStepExecution(exec);
			}
			
			sendOutput(response, "{\"success\":true}");
		
		} catch (Exception e){
			sendOutput(response, "{\"error\":" + JSONObject.quote( e.getMessage() ) + "}");
			return;
		}
		
		
	}
	
	/**
	 * Check that test execution id is valid
	 * @param request
	 * @param executionId
	 * @return
	 */
	private boolean isValidTest(HttpServletRequest request, String executionId){
		final String thisExecutionId = (String) request.getSession(true).getAttribute("executionId");
		return thisExecutionId != null && executionId != null && thisExecutionId.equals(executionId);
	}
	
}
