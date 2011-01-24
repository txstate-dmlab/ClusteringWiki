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

import java.text.DecimalFormat;

/**
 * Keep track of the execution time of a given event
 * 
 * @author David C. Anastasiu
 *
 */
public class Timer {
	
	/**
	 * double value formatter
	 */
	public static final DecimalFormat decimals = new DecimalFormat("#.###");
	
	/**
	 * Event start time
	 */
	private long start;
	
	/**
	 * Event end time
	 */
	private long end;

	/**
	 * Constructor
	 */
	public Timer() {
		reset();
	}

	/**
	 * Start timer
	 */
	public void start() {
		start = System.nanoTime();
	}

	/**
	 * Stop timer
	 */
	public void stop() {
		end = System.nanoTime();
	}

	/**
	 * How long did the execution take, in nanoseconds
	 * @return
	 */
	public long time(){
		return end - start;
	}
  
	/**
	 * How long did the execution take, in seconds
	 * @return
	 */
	public double seconds(){
		return Double.valueOf( decimals.format( time()/1000000000.0D) );
	}
	
	/**
	 * How long did the execution take, in mili seconds
	 * @return
	 */
	public double miliSeconds(){
		return Double.valueOf( decimals.format( time()/1000000.0D) );
	}
	
	/**
	 * How long did the execution take, in micro seconds
	 * @return
	 */
	public double microSeconds(){
		return Double.valueOf( decimals.format( time()/1000.0D) );
	}

	/**
	 * Reset timer
	 */
	public void reset() {
		start = 0;  
		end   = 0;
	}
	
	@Override
	public String toString(){
		long duration = time();
		if(duration < 0) duration = 0;
		if(duration < 1000.0D) return decimals.format( duration/1000.0D ) + " micros";
		if(duration < 1000000.0D) return decimals.format( duration/1000000.0D ) + " ms";
		else return decimals.format( duration/1000000000.0D ) + " s";
	}

}
