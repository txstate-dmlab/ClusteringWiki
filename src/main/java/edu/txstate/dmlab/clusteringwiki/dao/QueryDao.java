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

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.txstate.dmlab.clusteringwiki.entity.Query;

/**
 * Data access object for a query record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("queriesDao")
@Transactional
public class QueryDao implements IQueryDao {

	private HibernateTemplate hibernateTemplate;

	@Autowired 
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Transactional(readOnly = false)
	public void deleteQuery(Query query) {
		hibernateTemplate.delete(query);
	}

	@Transactional(readOnly = false)
	public void saveQuery(Query query) {
		hibernateTemplate.saveOrUpdate(query);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query selectExistingUserQuery(Integer userId, String service, 
			Integer numResults, String text) {
		String hql = "from "
			+ Query.class.getName() + " q where q.userId = :userId and q.service = :service " +
			"and q.numResults = :numResults and q.text = :text";
		hibernateTemplate.setMaxResults(1);
		List<Query> l = hibernateTemplate.findByNamedParam(hql, 
			new String[] {"userId", "service", "numResults", "text"}, 
			new Object[] {userId, service, numResults, text});
		hibernateTemplate.setMaxResults(0);
		return l.size() > 0 ? (Query) l.get(0) : null;
	}

	@Override
	public Query selectQueryById(Integer id) {
		return hibernateTemplate.get(Query.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Query> selectQueryMatchingSearch(String search, Integer limit){
		org.hibernate.Query query = hibernateTemplate.getSessionFactory()
			.getCurrentSession().createSQLQuery(
			"SELECT * FROM `queries` q WHERE q.id IN (SELECT lim.`query_id` FROM (SELECT qs.`query_id`, " +
			"MATCH(qs.`query_text`) AGAINST(:search1 IN BOOLEAN MODE) as score " +
			"FROM `query_search` qs WHERE MATCH(qs.`query_text`) " +
			"AGAINST(:search2 IN BOOLEAN MODE) ORDER BY score desc limit " + limit +") lim )")
			.addEntity(Query.class)
			.setParameter("search1", search).setParameter("search2", search);
		
		return query.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Query> selectUserQueryMatchingSearch(String search, String analyzedSearch, Integer userId, Integer limit){
		org.hibernate.Query query = hibernateTemplate.getSessionFactory()
			.getCurrentSession().createSQLQuery(
			"SELECT * FROM `queries` q WHERE q.user_id = :userId AND q.id IN " +
			"(SELECT lim.`query_id` FROM (SELECT qs.`query_id`, " +
			"MATCH(qs.`query_text`) AGAINST(:search1 IN BOOLEAN MODE) as score " +
			"FROM `query_search` qs WHERE MATCH(qs.`query_text`) " +
			"AGAINST(:search2 IN BOOLEAN MODE) ORDER BY score desc limit " + limit + ") lim )")
			.addEntity(Query.class).setParameter("userId", userId)
			.setParameter("search1", search).setParameter("search2", search);
		
		List<Query> l = query.list();
		
		if(l.size() == 0 && analyzedSearch != null){
			String[] searchArray = analyzedSearch.split("\\s+");
			if(searchArray.length == 0) return l;
			String q = "SELECT * FROM `queries` q WHERE q.`user_id` = :userId AND q.`id` IN " +
			"(SELECT lim.`id` FROM (SELECT qs.`id`, SUM( LENGTH(`parsed_query_text`) - LENGTH(REPLACE(`parsed_query_text`, ' ', ''))+1) as `num_words` " +
			"FROM `queries` qs WHERE ";
			for(int i = 0; i < searchArray.length; i++){
				q += " ' ' || `parsed_query_text` || ' ' like :search" + i + " ";
				if(i < searchArray.length - 1) q += "OR ";
			}
			q += " GROUP BY qs.`id` ORDER BY `num_words` asc limit " + limit + " ) lim )";

			query = hibernateTemplate.getSessionFactory()
					.getCurrentSession().createSQLQuery( q )
					.addEntity(Query.class).setParameter("userId", userId);
			for(int i = 0; i < searchArray.length; i++)
				query.setParameter("search" + i, "% " + searchArray[i] + " %");
			l = query.list();
		}
		
		return l;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> getMostEditedQueries(){
		org.hibernate.Query query = hibernateTemplate.getSessionFactory()
		.getCurrentSession().createSQLQuery(
		"SELECT t.`query_text` FROM (SELECT q.`query_text`, count(*) `cnt` FROM queries q " +
		"WHERE q.`user_id` <> (SELECT `id` from `users` WHERE `email` = 'all') " +
		"AND q.`service` = 'google' AND q.`num_results` = '50'  " +
		"AND 3 = (SELECT e.`clustering_algo` FROM `cluster_edits` e WHERE e.`query_id` = q.`id` LIMIT 1) " +
		"GROUP BY q.`query_text` ORDER BY `cnt` DESC LIMIT 0, 10) t").addScalar("query_text", Hibernate.STRING);
		try {
			hibernateTemplate.setMaxResults(10);
			List<String> l = query.list();
			hibernateTemplate.setMaxResults(0);
			return l;
		} catch(Exception e){
			System.out.println(e.getMessage());
			
			return null;
		}
	}

}
