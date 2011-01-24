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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import edu.txstate.dmlab.clusteringwiki.app.IApplicationUser;

/**
 * Controller untility class for methods needed by multiple controllers
 * 
 * @author David C. Anastasiu
 *
 */
public class BaseController {

	IApplicationUser applicationUser;
	
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
     * @author David C. Anastasiu
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
	 * Check if request is coming from local server.  Some resources
	 * (ex: NYTimes data set) cannot be made available publically
	 * @return
	 */
	public static boolean isLocalRequest(HttpServletRequest request){
		return request.getRequestURL().indexOf(":8080") > -1;
	}
	
}
