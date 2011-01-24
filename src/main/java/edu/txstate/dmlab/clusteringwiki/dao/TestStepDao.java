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

import edu.txstate.dmlab.clusteringwiki.entity.TestStep;

/**
 * Data access object for a  TestStep record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("testStepDao")
@Transactional
public class TestStepDao {

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
	public void saveTestStep(TestStep t) {
		hibernateTemplate.saveOrUpdate(t);
	}

	public TestStep selectTestStepById(Integer id) {
		return hibernateTemplate.get(TestStep.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public TestStep getNextStep(String executionId){
		
		org.hibernate.Query query = hibernateTemplate.getSessionFactory()
		.getCurrentSession().createSQLQuery(
		"SELECT * FROM `test_steps` WHERE `id` = " +
		"( SELECT `step_id` FROM `test_details` WHERE `test_id` = " +
		"  (SELECT `test_id` FROM `test_executions` WHERE `id` = :eid1) " +
		"  AND `step_id` NOT IN (SELECT `step_id` FROM `test_step_executions` WHERE `execution_id` = :eid2) " +
		"  ORDER BY `step_order` ASC LIMIT 1 )  ")
		.addEntity(TestStep.class)
		.setParameter("eid1", executionId).setParameter("eid2", executionId);
		
		final List<TestStep> res = query.list();
		if(res != null && res.size() > 0) return res.get(0);
		
		return null;
	}
	
}
