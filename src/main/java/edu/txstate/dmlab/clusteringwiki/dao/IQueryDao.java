package edu.txstate.dmlab.clusteringwiki.dao;

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

import java.util.List;

import edu.txstate.dmlab.clusteringwiki.entity.Query;

/**
 * Data access object public interface for a query record
 * 
 * @author David C. Anastasiu
 *
 */
public interface IQueryDao {
	public void saveQuery(Query query);
	public Query selectQueryById(Integer id);
	public Query selectExistingUserQuery(Integer userId, String service, 
		Integer numResults, String text);
	public void deleteQuery(Query query);
	public List<Query> selectQueryMatchingSearch(String search, Integer limit);
	public List<Query> selectUserQueryMatchingSearch(String search, String analyzedSearch, Integer userId, Integer limit);
	public List<String> getMostEditedQueries();
}
