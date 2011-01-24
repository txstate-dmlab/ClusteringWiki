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

import java.util.Date;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generates uniqueue set of keys to be used in the application
 * to identify cluster requests.  Values expire after a given interval.
 * The value store is checked for expiration every N'th request.
 * Class is thread safe.
 * 
 * @author David C. Anastasiu
 *
 */
public class KeyGenerator {

	/**
     * The default ttl value in minutes, 60
     */
    public static final int DEFAULT_TIME_TO_LIVE = 60;
    
    /**
     * The default expiration check interval, every 10th key request
     */
    public static final int DEFAULT_EXPIRATION_CHECK_INTERVAL = 10;

    /**
     * Default key length to return
     */
    public static final int DEFAULT_KEY_LENGTH = 10;
    
    /**
     * ttl in minutes
     */
    private static int timeToLive = DEFAULT_TIME_TO_LIVE;
    
    /**
     * expiration check interval
     */
    private static int expirationCheckInterval = DEFAULT_EXPIRATION_CHECK_INTERVAL;
    
    /**
     * Data store check count
     */
    private static volatile int accessCount = 0;

    /**
     * Data store for keys
     */
    private static final ConcurrentHashMap<String, Date> keys = new ConcurrentHashMap<String, Date>();
    
    /**
     * Random number generator
     */
    private static final Random random = new Random(System.currentTimeMillis());
    
    private KeyGenerator(){
    	//no instantiation allowed
    }


    public static String getKey(int length){
    	
    	//check if time to clean map
    	if(accessCount % expirationCheckInterval == 0)
    		cleanMap();
    	
    	//generate uniqueue key
    	String key = generateKey(length);
    	while(keys.containsKey(key))
    		key = generateKey(length);
    	keys.put(key, new Date()); //add new key
    	accessCount++;
    	
    	return key;
    }
    
    
    /**
     * Generate a string key of given length
     * @param length
     * @return
     */
    private static String generateKey(int length){

    	String key = "";
    	
    	while(key.length() < length)
    		key += Long.toString(Math.abs(random.nextLong()), 36); //generates random token of length 13
    	
    	return key.substring(0, length - 1);
    	
    }
    
    /**
     * Get starndard length key
     * @return
     */
    public static String getKey(){
    	return getKey(DEFAULT_KEY_LENGTH);
    }
    
    /**
     * Clean expired keys in data store
     */
    private static void cleanMap(){
    	Date now = new Date();
    	long expireTime = now.getTime() - 60L*1000L;
    	for(Entry<String, Date> e : keys.entrySet())
    		if(e.getValue().getTime() < expireTime)
    			keys.remove( e.getKey() );
    }
    
	/**
	 * @return the timeToLive
	 */
	public static int getTimeToLive() {
		return timeToLive;
	}


	/**
	 * @param timeToLive the timeToLive to set
	 */
	public static void setTimeToLive(int timeToLive) {
		KeyGenerator.timeToLive = timeToLive;
	}


	/**
	 * @return the expirationCheckInterval
	 */
	public static int getExpirationCheckInterval() {
		return expirationCheckInterval;
	}


	/**
	 * @param expirationCheckInterval the expirationCheckInterval to set
	 */
	public static void setExpirationCheckInterval(int expirationCheckInterval) {
		KeyGenerator.expirationCheckInterval = expirationCheckInterval;
	}
    
}
