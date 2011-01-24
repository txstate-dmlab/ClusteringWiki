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

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import edu.txstate.dmlab.clusteringwiki.dao.IUserDao;
import edu.txstate.dmlab.clusteringwiki.entity.User;
import edu.txstate.dmlab.clusteringwiki.web.AppContextListener;

/**
 * Represents an application user potentially logged into the
 * application
 * 
 * @author David C. Anastasiu
 *
 */
public class ApplicationUser implements IApplicationUser, Serializable {

	
	public static final long serialVersionUID = -1245088765258048673L;

	private Integer userId;
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	private String password;
	
	private boolean loggedIn = false;
	
	private boolean admin = false;
	
	private IUserDao userDao;
	
	private User userRecord;

	/**
	 * Log in to the application
	 * Email and Password must be set prior to login
	 * Throws exception on login failure
	 * @throws Exception
	 */
	public void logIn() throws Exception {
		if(userDao == null)
			userDao = (IUserDao) AppContextListener.getWebAppCtx().getBean("userDao");
		userRecord = userDao.selectUserByEmail(email);
		if(userRecord == null)
			throw new Exception("Invalid email or password.  Please try again.");
		
		if(!userRecord.checkPassword(password))
			throw new Exception("Invalid email or password.  Please try again.");
		
		firstName = userRecord.getFirstName();
		lastName = userRecord.getLastName();
		userId = userRecord.getId();
		loggedIn = true;
		userRecord.setLastLogin(new Date());
		userDao.saveUser(userRecord);
		
	}
	
	/**
	 * Log out the user
	 */
	public void logOut(){
		userRecord = null;
		email = null;
		firstName = null;
		lastName = null;
		loggedIn = false;
	}
	
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer theUserId) {
		userId = theUserId;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String theEmail) {
		email = theEmail;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String theFirstName) {
		firstName = theFirstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String theLastName) {
		lastName = theLastName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String thePassword) {
		password = thePassword;
	}

	/**
	 * @param loggedIn the loggedIn to set
	 */
	public void setLoggedIn(boolean isLoggedIn) {
		loggedIn = isLoggedIn;
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean isAdmin) {
		admin = isAdmin;
	}

	/**
	 * @return the userDao
	 */
	public IUserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao the userDao to set
	 */
	@Autowired
	public void setUserDao(IUserDao theUserDao) {
		userDao = theUserDao;
	}


}
