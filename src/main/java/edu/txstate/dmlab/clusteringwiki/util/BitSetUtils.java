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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.util.OpenBitSet;

/**
 * Uitility class for bit set operations
 * 
 * @author David C. Anastasiu
 *
 */
public class BitSetUtils {

	private BitSetUtils(){ }
	
	public static void print(OpenBitSet set){
		List<Integer> s = new ArrayList<Integer>();
		for(int i = 0; i < set.size(); i++)
			if(set.fastGet(i)) s.add(i);
		System.out.println(s);
	}
}
