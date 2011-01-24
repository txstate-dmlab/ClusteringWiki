package edu.txstate.dmlab.clusteringwiki.app;

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

import edu.txstate.dmlab.clusteringwiki.dao.IUserDao;

public interface IApplicationUser {
	
	/**
	 * Log in to the application
	 * Email and Password must be set prior to login
	 * Throws exception on login failure
	 * @throws Exception
	 */
	public void logIn() throws Exception;
	
	/**
	 * Clears internal data and sets loggedIn = false
	 */
	public void logOut();
	
	/**
	 * @return the userId
	 */
	public Integer getUserId();

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId);
	
	/**
	 * @return the email
	 */
	public String getEmail();

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email);

	/**
	 * @return the firstName
	 */
	public String getFirstName();

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName);

	/**
	 * @return the lastName
	 */
	public String getLastName();

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName);

	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password);

	/**
	 * @param loggedIn the loggedIn to set
	 */
	public void setLoggedIn(boolean loggedIn);

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn();
	
	/**
	 * @return the admin
	 */
	public boolean isAdmin();

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean admin);

	/**
	 * @return the userDao
	 */
	public IUserDao getUserDao();
	
}
