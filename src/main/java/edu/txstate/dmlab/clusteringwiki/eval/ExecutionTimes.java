package edu.txstate.dmlab.clusteringwiki.eval;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

/**
 * Execution time tracking utility class
 * Allows simultaneous tracking of multiple timers via uniquely 
 * generated ids and expiring timers
 * 
 * @author David C. Anastasiu
 *
 */
public class ExecutionTimes {


	private static Random rand = new Random();
	
	/**
	 * Number of seconds after which forgotten timers will expire and should be 
	 * cleaned - 5 minutes
	 */
	public final static double TIMER_EXPIRATION_TIME = 300.0D;
	
	/**
	 * store for timers
	 */
	private static Map<Integer, Map<String, Timer>> timers = new HashMap<Integer, Map<String, Timer>>();
	
	/**
	 * store for timer names
	 */
	private static Map<Integer, Timer> timerTimes = new HashMap<Integer, Timer>();
	
	
	/**
	 * Add a new timers container to be tracked
	 * @return id of timers container
	 */
	public static int initiateTimers(){
		//first check for old timers that may have been left behind...
		for(Integer k : timerTimes.keySet()){
			Timer t = timerTimes.get(k);
			if(Double.compare(t.seconds(), TIMER_EXPIRATION_TIME) > 0){
				ExecutionTimes.clear(k);
				timerTimes.remove(k);
			}
		}
		//get new timers id
		Integer i = rand.nextInt();
		while(timers.containsKey(i))
			i = rand.nextInt();
		//initiate timers container
		timers.put(i, new HashMap<String, Timer>());
		timerTimes.put(i, new Timer());
		return i;
	}
	
	
	/**
	 * @param assigned id
	 * @return the timers
	 */
	public static Map<String, Timer> getTimers(int id) {
		return timers.get(id);
	}

	/**
	 * 
	 * @param timers the timers to set
	 * @param id
	 */
	public static void setTimers(int id, Map<String, Timer> timers) {
		ExecutionTimes.timers.put(id, timers);
	}

	/**
	 * @return the timerNames for an id set of timers
	 */
	public static Set<String> getTimerNames(int id) {
		return timers.get(id).keySet();
	}
	
	/**
	 * Add a new timer
	 * @param key
	 */
	public static void addTimer(int id, String key){
		Map<String, Timer> t = timers.get(id);
		if(t == null) {
			t = new HashMap<String, Timer>();
			timers.put(id, t);
		}
		t.put(key, new Timer());
	}
	
	/**
	 * Retrieve a timer given its id and key
	 * @param key
	 * @return
	 */
	public static Timer getTimer(int id, String key){
		Map<String, Timer> t = timers.get(id);
		return t != null ? t.get(key) : null;
	}
	
	/**
	 * Start a timer given its id and key
	 * @param key
	 */
	public static void startTimer(int id, String key){
		Timer tm = getTimer(id, key);
		if(tm == null){
			addTimer(id, key);
			tm = getTimer(id, key);
		}
		tm.start();
	}
	
	/**
	 * Stop a timer given its id and key
	 * @param key
	 */
	public static void stopTimer(int id, String key){
		Timer tm = getTimer(id, key);
		if(tm != null)
			tm.stop();
	}
	
	/**
	 * Get timer duration in nanosecond thousands
	 * @param key
	 * @return
	 */
	public static long getDuration(int id, String key){
		Timer tm = getTimer(id, key);
		if(tm != null)
			return tm.time();
		return 0;
	}
	
	/**
	 * Get timer duration in seconds
	 * @param key
	 * @return
	 */
	public static double getSeconds(int id, String key){
		Timer tm = getTimer(id, key);
		if(tm != null)
			return tm.seconds();
		return 0.0D;
	}
	
	/**
	 * Remove all timers for an id
	 */
	public static void clear(int id){
		timers.remove(id);
	}
	
	/**
	 * Remove specific timer given its id and key
	 * @param key
	 */
	public static void clearTimer(int id, String key){
		Map<String, Timer> t = timers.get(id);
		if(t != null)
			t.remove(key);
	}
	
	/**
	 * Present times for all registered timers for an id
	 * @return
	 */
	public static String report(int id){
		String s = "";
		Timer t;
		Map<String, Timer> tms = timers.get(id);
		for(String k : tms.keySet()){
			t = tms.get(k);
			s += k + ": " + t + " \n";
		}
		return s;
	}
	
	/**
	 * JSON representations of timers for a given id
	 * @param id
	 * @return
	 */
	public static JSONObject toJSON(int id){
		Map<String, Timer> tms = timers.get(id);
		return tms != null ? new JSONObject(tms) : new JSONObject();
	}
}
