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

import edu.txstate.dmlab.clusteringwiki.entity.TestExecution;
import edu.txstate.dmlab.clusteringwiki.entity.TestStep;

/**
 * Data access object for a  TestExecution record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("testExecutionDao")
@Transactional
public class TestExecutionDao {

	private HibernateTemplate hibernateTemplate;

	@Autowired 
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Transactional(readOnly = false)
	public void deleteTestTopic(TestStep t) {
		hibernateTemplate.delete(t);
	}

	@Transactional(readOnly = false)
	public void saveTestExecution(TestExecution t) {
		hibernateTemplate.saveOrUpdate(t);
	}

	public TestExecution selectTestExecutionById(Integer id) {
		return hibernateTemplate.get(TestExecution.class, id);
	}
	
	
	@SuppressWarnings("unchecked")
	public Integer getExecutionTestId(String executionId){
		String hql = "from "
			+ TestExecution.class.getName() + " t where t.id = :id";
		hibernateTemplate.setMaxResults(1);
		List<TestExecution> l = hibernateTemplate.findByNamedParam(hql, "id", executionId);
		hibernateTemplate.setMaxResults(0);
		return l.size() > 0 ? ((TestExecution) l.get(0)).getTestId() : null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public String addTrainingTest(){
		org.hibernate.Query query = hibernateTemplate.getSessionFactory()
		.getCurrentSession().createSQLQuery(
		"SELECT CONCAT('train', LPAD(SUBSTR(`id`, 6) + 1, 5, '0')) `next_id` " +
		"FROM `test_executions` WHERE `id` LIKE 'train%' ORDER BY " +
		"SUBSTR(`id`, 6) + 0 DESC LIMIT 1").addScalar("next_id", Hibernate.STRING);
		hibernateTemplate.setMaxResults(1);
		List<String> l = query.list();
		hibernateTemplate.setMaxResults(0);
		String nextId = l.size() > 0 ? l.get(0) : null;
		TestExecution t = new TestExecution();
		t.setId(nextId);
		t.setTestId(1);
		this.saveTestExecution(t);
		return nextId;
	}
	
}
