package edu.txstate.dmlab.clusteringwiki.util;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Utility class for methods dealing with URLs
 * 
 * @author David C. Anastasiu
 *
 */
public class URLUtils {

	
	/**
	 * Encode a string value to URL encoding standards
	 * @param str string to encode
	 * @return str encoded string using URL standards
	 */
	public static String encodeValue(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}
	
	/**
	 * Decode a string value from URL encoded value
	 * @param str string to decode
	 * @return str decoded string using URL standards
	 */
	public static String decodeValue(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}
	
	/**
	 * Get a web page contents from its URL - simple implementation
	 * (should be grealy improved)
	 * @param url
	 * @return document string, empty if errors occured
	 */
	public static synchronized String getWebPage(String url)
		throws MalformedURLException, IOException{
		
		String doc;
		
		doc = "";  //cache miss
		String line = null;
		BufferedReader reader = null;
		try {
			
	        // Execute the search.
			URL req = new URL(url);
			reader = new BufferedReader(
				new InputStreamReader(req.openStream())); 
				while ((line = reader.readLine()) != null) {
					if(doc.length() != 0) doc += "\n";
					doc += line;
				}
	    } finally {
	    	if(reader != null)
				try { reader.close(); } 
	    		catch (IOException e) { /* do nothing */}
	    }
			    
		return doc;
	}


}
