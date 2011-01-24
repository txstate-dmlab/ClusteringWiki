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

import junit.framework.Assert;

import org.junit.Test;

public class ArrayUtilsTest {

	@Test
	public void isContained(){
		final int[] test = new int[]{1,2};
		final int[][] against = new int[4][];
		against[0] = new int[]{1,2,4,5,1,3,5};
		against[1] = new int[]{1,2,3,4,5,1,3};
		against[2] = new int[]{1,2,3,4,5,1,3,4};
		against[3] = new int[]{1,2,3,4,5,1,3,5};
		
		final boolean actual = ArrayUtils.isSubphraseOf(test, against);
		final boolean expected = true;
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void contains(){
		final int[] test = new int[]{1,2,3,4,5,1,3,5};
		final int[][] against = new int[4][];
		against[0] = new int[]{1,2};
		against[1] = new int[]{1,2,3,4,5,1,3};
		against[2] = new int[]{1,2,3,4,5,1,3,4};
		against[3] = new int[]{1,2,3,4,5,1,3,5};
		
		final boolean actual = ArrayUtils.isSuperphraseOf(test, against);
		final boolean expected = true;
		Assert.assertEquals(expected, actual);
	}
	
	
	@Test
	public void isSubset(){
		final int[] test = new int[]{1,2};
		final int[][] against = new int[4][];
		against[0] = new int[]{1,2,4,5,1,3,5};
		against[1] = new int[]{1,2,3,4,5,1,3};
		against[2] = new int[]{1,2,3,4,5,1,3,4};
		against[3] = new int[]{1,2,3,4,5,1,3,5};
		
		final boolean actual = ArrayUtils.isSubsetOf(test, against);
		final boolean expected = true;
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void isSuperset(){
		final int[] test = new int[]{1,2,3,4,5,1,3,5};
		final int[][] against = new int[4][];
		against[0] = new int[]{1,2};
		against[1] = new int[]{1,2,3,4,5,1,3};
		against[2] = new int[]{1,2,3,4,5,1,3,4};
		against[3] = new int[]{1,2,3,4,5,1,3,5};
		
		final boolean actual = ArrayUtils.isSupersetOf(test, against);
		final boolean expected = true;
		Assert.assertEquals(expected, actual);
	}
	
	
	
}
