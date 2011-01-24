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
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.txstate.dmlab.clusteringwiki.dao.TestExecutionDao;

/**
 * Controller for the main index page
 * 
 * @author David C. Anastasiu
 *
 */
@Controller
public class IndexController extends BaseController {
	
	@Autowired
	private TestExecutionDao testExecutionDao;
	
	@RequestMapping("index.*")
	public String getIndexPage(
		HttpServletRequest request,
		HttpServletResponse response,
		Model model){
		if(request.getParameter("test") != null){
			String executionId = request.getParameter("test").toLowerCase();
			if(executionId.equals("train")){
				//user is requesting the training track.  Generate a new unique ID for this test
				executionId = testExecutionDao.addTrainingTest();
				return "redirect:index.html?test=" + executionId;
			}
			if(isValidExecutionId(executionId)){
				//starting test
				final HttpSession session = request.getSession(true);
				if(session.getAttribute("testId") != null && 
						!session.getAttribute("testId").equals(executionId)){
					request.setAttribute("message", "A test is already in progress in this session.<br>" +
							"Please finish the current test of stop the test from the top menu before starting another.");
					return "pageError";
				}
				session.setAttribute("executionId", executionId);
			} else {
				request.setAttribute("message", "Your test execution id is invalid.  Please try again.");
				return "pageError";
			}
		} else if(request.getParameter("endTest") != null){
			//end in progress test
			final HttpSession session = request.getSession(false);
			if(session != null) 
				session.removeAttribute("executionId");
		}
		return "index";
	}
	
	private boolean isValidExecutionId(String executionId){
		return testExecutionDao.getExecutionTestId(executionId) != null;
	}
	
}
