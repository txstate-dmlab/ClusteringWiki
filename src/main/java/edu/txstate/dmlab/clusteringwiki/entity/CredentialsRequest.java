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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Credentials request record
 * 
 * @author David C. Anastasiu
 *
 */
@Entity
@Table(name = "credentials_requests")
public class CredentialsRequest implements Serializable {


	private static final long serialVersionUID = 3651356642856292544L;

	private static final long MILLISECS_PER_HOUR = (60 * 60 * 1000);

	/**
	 * Unique request id
	 */
	private Integer id;
	
	private String key = User.hashValue( String.valueOf( System.currentTimeMillis() ) ).toLowerCase();
	
	private String email;
	
	private Date requestTime = new Date();
	
	private Integer valid = new Integer(0);

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer theId) {
		id = theId;
	}

	/**
	 * @return the key
	 */
	@Column(name = "request_key", nullable=false, unique=true, length=32)
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String theKey) {
		key = theKey;
	}

	/**
	 * @return the email
	 */
	@Column(name = "email", nullable=false, length=80)
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
	 * @return the request_time
	 */
	@Column(name = "request_time", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getRequestTime() {
		return requestTime;
	}
	
	/**
	 * Check whether the request has expired
	 * @return
	 */
	@Transient
	public boolean isExpired(){
		long now = System.currentTimeMillis();
		long expirationTime = requestTime != null ? requestTime.getTime() + MILLISECS_PER_HOUR : now;
		return Long.valueOf(expirationTime).compareTo(now) >= 0;
	}

	/**
	 * @param requestTime the request_time to set
	 */
	public void setRequestTime(Date theRequestTime) {
		requestTime = theRequestTime;
	}

	/**
	 * @return the valid
	 */
	@Column(name = "valid", nullable = false)
	public Integer getValid() {
		return valid;
	}

	/**
	 * Check if request is still valid
	 * @return
	 */
	@Transient
	public boolean isValidRequest(){
		return valid != null ? valid == 1 : false;
	}
	
	/**
	 * @param valid the valid to set
	 */
	public void setValid(Integer isValid) {
		valid = isValid;
	}
	
}
