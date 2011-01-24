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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Query response record
 * 
 * @author David C. Anastasiu
 *
 */
@Entity
@Table(name = "query_responses")
public class QueryResponse implements Serializable {

	private static final long serialVersionUID = -3309480606363913525L;

	private Integer id;
	
	private String url;

	public static final int MAX_URL_LENGTH = 255;
	
	public QueryResponse(){
		
	}
	
	public QueryResponse(String url){
		setUrl(url);
	}
	
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
	 * @return the url
	 */
	@Column(name = "url", nullable=false, length=255)
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String theUrl) {
		url = theUrl != null && theUrl.length() > MAX_URL_LENGTH ? 
				theUrl.substring(theUrl.length() - MAX_URL_LENGTH) : theUrl;
	}
	
}
