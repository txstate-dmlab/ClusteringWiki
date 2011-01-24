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

/**
 * Defines public inteface for a search result abstracted from any
 * data source or provider
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract Search Result
 * 
 * @author David C. Anastasiu
 *
 */
public interface ICWSearchResult {
	
	/**
	 * List of available fields for the ICW Search Result
	 */
	public enum FIELDS {
		ID,
		INDEX,
		TITLE,
		SNIPPET,
		URL,
		CLICK_URL,
		MIME_TYPE,
		MODIFICATION_DATE,
		CACHE_URL,
		CACHE_SIZE
	}
	
	/**
	 * List of methods used to retrieve FIELDS values.
	 */
	public static final String[] fieldMethods = {
		"getId",
		"getIndex",
		"getTitle",
		"getSnippet",
		"getUrl",
		"getClickUrl",
		"getMimeType",
		"getModificationDate",
		"getCacheUrl",
		"getCacheSize"
	};
	
	/**
	 * Dynamic method for retrieving string value of a
	 * given field in the ICW search result
	 * @param field
	 * @return
	 */
	public String getFieldValue(FIELDS field);
	
	/**
	 * Get document id assigned to this document.
	 * Id is assigned by searcher when building the 
	 * search result collection
	 * @return document id
	 */
	public int getId();
	
	/**
	 * Set the internal id for this document
	 */
	public void setId(int id);
	
	/**
	 * Get document collection index assigned to this document.
	 * Id is assigned by searcher when building the 
	 * search result collection
	 * @return document index
	 */
	public int getIndex();
	
	/**
	 * Set the internal collection index for this document
	 */
	public void setIndex(int index);
	
	/**
     * The resource title.
     *
     * @return The resource title.
     */
	public String getTitle();

    /**
	 * @param title the title to set
	 */
	public void setTitle(String title);
	
    /**
     * Snippet from the resource.
     *
     * @return Snippet text.
     */
	public String getSnippet();
    
    /**
	 * @param snippet the snippet to set
	 */
	public void setSnippet(String snippet);
	
    /**
     * The display URL for the resource.
     *
     * @return The URL for the resource.
     */
	public String getUrl();

    /**
	 * @param url the url to set
	 */
	public void setUrl(String url);
	
    /**
     * The  URL for retrieving the resource.
     *
     * @return URL for retrieving the resource.
     */
	public String getClickUrl();

    /**
	 * @param clickUrl the clickUrl to set
	 */
	public void setClickUrl(String clickUrl);
	
    /**
     * The MIME type of the resource. <b>Optional.</b>
     *
     * @return The MIME type of the resource.
     */
	public String getMimeType();

    /**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType);
	
    /**
     * Last updated unix timestamp. <b>Optional.</b>
     *
     * @return The date the resource was last modified, in unix timestamp format.
     */
	public long getModificationDate();

    /**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(long modificationDate);
	
    /**
     * The cached result URL. <b>Optional.</b>
     *
     * @return The cached result URL.
     */
	public String getCacheUrl();
    
    /**
	 * @param cacheUrl the cacheUrl to set
	 */
	public void setCacheUrl(String cacheUrl);
	
    /**
     * 
     * @return The size of the cache result, if known, -1 otherwise.
     */
	public int getCacheSize();
    
    /**
	 * @param cacheSize the cacheSize to set
	 */
	public void setCacheSize(int cacheSize);
	

	/**
	 * @return the trecId
	 */
	public String getTrecId();

	/**
	 * @param trecId the trecId to set
	 */
	public void setTrecId(String trecId);

	/**
	 * @return the relevant
	 */
	public int getRelevant();

	/**
	 * @param relevant the relevant to set
	 */
	public void setRelevant(int relevant);
	
	/**
	 * Create customized JSON representation of the object
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException;
	
}
