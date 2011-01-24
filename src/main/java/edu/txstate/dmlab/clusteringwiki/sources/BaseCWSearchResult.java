package edu.txstate.dmlab.clusteringwiki.sources;

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
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import edu.txstate.dmlab.clusteringwiki.util.URLUtils;

/**
 * Base implementation of a ClusteringWIki search result
 * Extended by others
 * 
 * @author David C. Anastasiu
 *
 */
@XStreamAlias("result")
public abstract class BaseCWSearchResult implements
		ICWSearchResult {

	@XStreamAlias("id")
	protected int id = -1;
	
	@XStreamAlias("index")
	protected int index = -1;
	
	@XStreamAlias("title")
	protected String title = "";
	
	@XStreamAlias("snippet")
	protected String snippet = "";
	
	@XStreamAlias("url")
	protected String url = "";
	
	@XStreamAlias("clickUrl")
	protected String clickUrl = "";
	
	@XStreamAlias("mimeType")
	protected String mimeType = "";
	
	@XStreamAlias("modificationDate")
	protected long modificationDate;
	
	@XStreamAlias("cacheUrl")
	protected String cacheUrl = "";
	
	@XStreamAlias("cacheSize")
	protected int cacheSize;
	
	@XStreamAlias("trecId")
	protected String trecId = "";
	
	@XStreamAlias("relevant")
	protected int relevant = 0;
	
	/**
	 * Document is not populated by the Searcher.
	 * However, the Search Result class should
	 * implement method to retrieve the full document
	 * referenced by the search result as a string.
	 * Once getDocument is called, this field should
	 * be populated with the document String.
	 */
	@XStreamOmitField
	@JsonIgnore
	protected String document = null;

	
	/**
	 * Dynamic method for retrieving string value of a
	 * given field in the ICW search result.  Returns 
	 * empty string if field is null or cannot be retrieved.
	 * @param field
	 * @return String field String value
	 */
	public String getFieldValue(FIELDS field){
		
		String method = fieldMethods[field.ordinal()];
		try {
			Method m = BaseCWSearchResult.class.getMethod(method);
			String val = (String) m.invoke(this);
			return val != null ? val : "";
		} catch (Exception e) {
			// do nothing
			return "";
		}
		
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int theId) {
		id = theId;
	}
	
	/**
	 * Get document collection index assigned to this document.
	 * Id is assigned by searcher when building the 
	 * search result collection
	 * @return document index
	 */
	public int getIndex(){
		return index;
	}
	
	/**
	 * Set the internal collection index for this document
	 */
	public void setIndex(int theIndex){
		index = theIndex;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String theTitle) {
		title = theTitle.replaceAll("[^\\p{ASCII}]", "");
	}

	/**
	 * @return the snippet
	 */
	public String getSnippet() {
		return snippet;
	}

	/**
	 * @param snippet the snippet to set
	 */
	public void setSnippet(String theSnippet) {
		snippet = theSnippet.replaceAll("[^\\p{ASCII}]", "");
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String theUrl) {
		url = theUrl;
	}

	/**
	 * @return the clickUrl
	 */
	public String getClickUrl() {
		return clickUrl;
	}

	/**
	 * @param clickUrl the clickUrl to set
	 */
	public void setClickUrl(String theClickUrl) {
		clickUrl = theClickUrl;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String theMimeType) {
		mimeType = theMimeType;
	}

	/**
	 * @return the modificationDate
	 */
	public long getModificationDate() {
		return modificationDate;
	}

	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(long theModificationDate) {
		modificationDate = theModificationDate;
	}

	/**
	 * @return the cacheUrl
	 */
	public String getCacheUrl() {
		return cacheUrl;
	}

	/**
	 * @param cacheUrl the cacheUrl to set
	 */
	public void setCacheUrl(String theCacheUrl) {
		cacheUrl = theCacheUrl;
	}

	/**
	 * @return the cacheSize
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * @param cacheSize the cacheSize to set
	 */
	public void setCacheSize(int theCacheSize) {
		cacheSize = theCacheSize;
	}
	

	/**
	 * @return the trecId
	 */
	public String getTrecId() {
		return trecId;
	}

	/**
	 * @param trecId the trecId to set
	 */
	public void setTrecId(String trecId) {
		this.trecId = trecId;
	}

	/**
	 * @return the relevant
	 */
	public int getRelevant() {
		return relevant;
	}

	/**
	 * @param relevant the relevant to set
	 */
	public void setRelevant(int relevant) {
		this.relevant = relevant;
	}
	
	/**
	 * Abstract method to be implemented by individual searchers
	 * @return the document
	 */
	@JsonIgnore
	public abstract String getDocument();
	
	/**
	 * Assumming document value was previously set, get internal 
	 * document value.  Can be called in getDocument().
	 * 
	 * @return the document
	 */
	@JsonIgnore
	public String getLocalDocument() {
		return document;
	}
	
	/**
	 * Get the document value form the web.
	 * Tries, in order, url, clickUrl, and cacheUrl,
	 * depending on which one is present.
	 * @param url
	 * @return
	 */
	@JsonIgnore
	public String getWebDocument(){
		String u = url;
		if(StringUtils.isEmpty(u))
			u = clickUrl;
		if(StringUtils.isEmpty(u))
			u = cacheUrl;
		try {
			return URLUtils.getWebPage(u);
		} catch (MalformedURLException e) {
			// do nothing
		} catch (IOException e) {
			// do nothing
		}
		return "";
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(String theDocument) {
		document = theDocument;
	}
	
	/**
	 * Create customized JSON representation of the object
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException{
		JSONObject j = new JSONObject();
		
		j.put("id", id);
		j.put("index", index);
		j.put("title", title);
		j.put("snippet", snippet);
		j.put("url", url);
		j.put("clickUrl", clickUrl);
		j.put("trecId", trecId);
		j.put("relevant", relevant);
		
		return j;
	}

}
