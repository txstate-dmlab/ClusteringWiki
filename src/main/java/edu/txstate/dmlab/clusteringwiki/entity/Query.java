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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 * Query record
 * 
 * @author David C. Anastasiu
 *
 */
@Entity
@Table(name = "queries")
public class Query implements Serializable {

	private static final long serialVersionUID = 848648190673440244L;

	private Integer id;
	
	private Integer userId;
	
	private Date executedOn;
	
	private String service;
	
	private Integer numResults;
	
	private String text;
	
	private String parsedText;
	
	private List<QueryResponse> topKQueryResponses = new ArrayList<QueryResponse>(0);

	public Query(){
		
	}
	
	public Query(Integer theUserId, String theService, Integer theNumResults,
		String theText, String theParsedText, List<String> theUrls){
		userId = theUserId;
		service = theService;
		numResults = theNumResults;
		text = theText;
		parsedText = theParsedText;
		
		if (theUrls != null) {
			for(String url : theUrls){
				topKQueryResponses.add( new QueryResponse(url));
			}
		}
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
	 * @return the userId
	 */
	@Column(name = "user_id", nullable=false)
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
	 * @return the executedOn
	 */
	@Column(name = "executed_on", nullable=true)
	public Date getExecutedOn() {
		return executedOn;
	}

	/**
	 * @param executedOn the executedOn to set
	 */
	public void setExecutedOn(Date dateExecutedOn) {
		executedOn = dateExecutedOn;
	}

	/**
	 * @return the service
	 */
	@Column(name = "service", nullable=false, length=10)
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String theService) {
		service = theService;
	}

	/**
	 * @return the numResults
	 */
	@Column(name = "num_results", nullable=false)
	public Integer getNumResults() {
		return numResults;
	}

	/**
	 * @param numResults the numResults to set
	 */
	public void setNumResults(Integer theNumResults) {
		numResults = theNumResults;
	}

	/**
	 * @return the text
	 */
	@Column(name = "query_text", nullable=false, length=1000)
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String theText) {
		text = theText;
	}

	/**
	 * @return the parsedText
	 */
	@Column(name = "parsed_query_text", nullable=true, length=1000)
	public String getParsedText() {
		return parsedText;
	}

	/**
	 * @param parsedText the parsedText to set
	 */
	public void setParsedText(String theParsedText) {
		parsedText = theParsedText;
	}

	/**
	 * @return the topKQueryResponses
	 */
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="query_id", nullable=false) 
	public List<QueryResponse> getTopKQueryResponses() {
		return topKQueryResponses;
	}
    
    /**
     * Get set of top k query response urls
     * @return
     */
    public Set<String> retrieveTopKQueryResponseUrlsSet() {
		if(topKQueryResponses == null) return null;
		Set<String> set = new HashSet<String>();
		for(QueryResponse q : topKQueryResponses)
			set.add(q.getUrl());
		return set;
	}
    

	/**
	 * @param topKQueryResponses the topKQueryResponses to set
	 */
	public void setTopKQueryResponses(List<QueryResponse> theTopKQueryResponses) {
		topKQueryResponses = theTopKQueryResponses;
	}
	
	/**
	 * Update query response urls if necessary
	 * @param urls
	 */
	public void updateResponses(List<String> urls){
		int i = 0;
		for(QueryResponse r : getTopKQueryResponses()){
			String qr = urls.get(i++);
			if(qr == null) qr = "";
			if(!qr.equals(r.getUrl()))
				r.setUrl(qr);
		}
	}
	
	public String toString(){
		String s = userId + ": " + text + " | " + parsedText + "\n";
		return s;		
	}
}
