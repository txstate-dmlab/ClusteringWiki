package edu.txstate.dmlab.clusteringwiki.entity;

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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * User record
 * 
 * @author David C. Anastasiu
 *
 */
@Entity
@Table(name = "users")
public class User implements Serializable {


	/**
	 * serial id for this object
	 */
	private static final long serialVersionUID = -5912737951372396758L;
	
	/**
	 * Unique user id
	 */
	private Integer id;
	
	private String email;
	
	private String password;
	
	private String firstName;
	
	private String lastName;
	
	private Date lastLogin = new Date();
	


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer theId) {
		id = theId;
	}

	/**
	 * @return the email
	 */
	@Column(name = "email", nullable=false, unique=true, length=80)
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
	 * @return the password
	 */
	@Column(name = "password", nullable=false, length=32)
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String thePassword) {
		if(id == null) password = hashValue(thePassword);
		else password = thePassword;
	}
	
	/**
	 * @param password the password to set
	 */
	public void changePassword(String thePassword) {
		password = hashValue(thePassword);
	}

	public boolean checkPassword(String pass) {
		String currentHash = password;
		String checkHash = hashValue(pass);
		
		return currentHash.equals( checkHash );
	}
	
	public static String hashValue(String pass){
		if(pass == null) return "";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			byte[] data = pass.getBytes(); 
			m.update(data,0,data.length);
			BigInteger i = new BigInteger(1,m.digest());
			return String.format("%1$032X", i);
		} catch (Exception e){
			System.out.println("BAD MD5 Hashing");
			//if this was real system, we would handle this better
			return "";
		}
	}
	
	/**
	 * @return the firstName
	 */
	@Column(name = "first_name", length=30)
	public String getFirstName() {
		return firstName != null ? firstName : email;
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
	@Column(name = "last_name", length=30)
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
	 * @return the lastLogin
	 */
	@Column(name = "last_login", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(Date dateLastLogin) {
		lastLogin = dateLastLogin;
	}
}
