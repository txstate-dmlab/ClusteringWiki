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

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.txstate.dmlab.clusteringwiki.entity.ClusterEdit;

/**
 * Data access object for a cluster edit record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("clusterEditsDao")
@Transactional
public class ClusterEditDao implements IClusterEditDao {

	private HibernateTemplate hibernateTemplate;

	@Autowired 
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Transactional(readOnly = false)
	public void deleteClusterEdit(ClusterEdit ce) {
		hibernateTemplate.delete(ce);
	}

	@Transactional(readOnly = false)
	public void saveClusterEdit(ClusterEdit ce) {
		hibernateTemplate.saveOrUpdate(ce);
	}

	public ClusterEdit selectClusterEditById(Integer id) {
		return hibernateTemplate.get(ClusterEdit.class, id);
	}
	
	
	@SuppressWarnings("unchecked")
	public ClusterEdit selectClusterEditByPath(Integer queryId, Integer clusteringAlgo,
			String path1, String path2, String path3, String path4, String path5) {
		String hql = "from "
			+ ClusterEdit.class.getName() + " ce where ce.queryId = :queryId and " + 
			"ce.clusteringAlgo = :clusteringAlgo and ce.path1 = :path1 and " + 
			"ce.path2 = :path2 and ce.path3 = :path3 and ce.path4 = :path4 and " + 
			"ce.path5 = :path5";
		hibernateTemplate.setMaxResults(1);
		List<ClusterEdit> l = hibernateTemplate.findByNamedParam(hql, 
				new String[] {"queryId", "clusteringAlgo", "path1", "path2", "path3", "path4", "path5"}, 
				new Object[] {queryId, clusteringAlgo, path1, path2, path3, path4, path5});
		hibernateTemplate.setMaxResults(0);
		return l.size() > 0 ? (ClusterEdit) l.get(0) : null;
	}
	
	/**
	 * Insert, update or delete a path in order to maintain set of paths in database
	 * for given query id and clustering algorithm
	 * @param queryId
	 * @param clusteringAlgo
	 * @param path1
	 * @param path2
	 * @param path3
	 * @param path4
	 * @param cardinality
	 * @throws Exception
	 */
	public void updatePath(Integer queryId, Integer clusteringAlgo,
			String path1, String path2, String path3, String path4, String path5, 
			Integer cardinality) 
	throws Exception {
		ClusterEdit ce = selectClusterEditByPath(queryId, clusteringAlgo, 
				path1, path2, path3, path4, path5);
		
		try {
			if(ce == null){
				//path did not already exist
				ce = new ClusterEdit();
				ce.setQueryId(queryId);
				ce.setCardinality(cardinality);
				ce.setClusteringAlgo(clusteringAlgo);
				ce.setPath1(path1);
				ce.setPath2(path2);
				ce.setPath3(path3);
				ce.setPath4(path4);
				ce.setPath5(path5);
				saveClusterEdit(ce);
			} else if(ce.getCardinality().intValue() + cardinality.intValue() == 0){
				//paths cancel each other out
				deleteClusterEdit(ce);
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
		//if edit exists and paths do not cancel each other out then
		//path has same cardinality and thus nothing to do
		
	}

	/**
	 * Select edits for a given query.  Edits are selected from cluster_edits or
	 * cluster_edits_all if userAll is true
	 * @param queryId Query id edits are associated with
	 * @param clusteringAlgo clustering algorithm for the edits
	 * @param userAll Whether query is associated with user "all"
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ClusterEdit> selectClusterEditsForUserQuery(Integer queryId,
			Integer clusteringAlgo, boolean userAll) {
		if(!userAll){
			String hql = "from "
				+ ClusterEdit.class.getName() + " ce where ce.queryId = :queryId and " +
				"ce.clusteringAlgo = :clusteringAlgo order by ce.cardinality desc, ce.path1, " +
				"ce.path2, ce.path3, ce.path4, ce.path5";
			return (List<ClusterEdit>) hibernateTemplate.findByNamedParam(hql, 
				new String[] {"queryId", "clusteringAlgo"}, 
				new Object[] {queryId, clusteringAlgo});
		}
		
		org.hibernate.Query query = hibernateTemplate.getSessionFactory()
		.getCurrentSession().createSQLQuery(
		"SELECT * FROM `cluster_edits_all` ce WHERE ce.`query_id` = :queryId  " +
		"AND ce.`clustering_algo` = :clusteringAlgo ")
		.addEntity(ClusterEdit.class)
		.setParameter("queryId", queryId).setParameter("clusteringAlgo", clusteringAlgo);
	
		return query.list();
		
	}
	
	/**
	 * delete all cluster edits for a given user query
	 */
	public void deleteClusterEditsForUserQuery(Integer queryId,
			Integer clusteringAlgo) {
		
		String hql = "from "
			+ ClusterEdit.class.getName() + " ce where ce.queryId = :queryId and " +
			"ce.clusteringAlgo = :clusteringAlgo";
		hibernateTemplate.deleteAll(
			hibernateTemplate.findByNamedParam(hql, 
					new String[] {"queryId", "clusteringAlgo"}, 
					new Object[] {queryId, clusteringAlgo})
		);
	}

}
