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

import edu.txstate.dmlab.clusteringwiki.entity.TestStepExecution;

/**
 * Data access object for a testStepExecution record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("testStepExecutionDao")
@Transactional
public class TestStepExecutionDao {

	private HibernateTemplate hibernateTemplate;

	@Autowired 
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Transactional(readOnly = false)
	public void deleteTestTopic(TestStepExecution t) {
		hibernateTemplate.delete(t);
	}

	@Transactional(readOnly = false)
	public void saveTestStepExecution(TestStepExecution t) {
		hibernateTemplate.saveOrUpdate(t);
	}

	public TestStepExecution selectTestStepExecutionById(Integer id) {
		return hibernateTemplate.get(TestStepExecution.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public TestStepExecution selectTestStepExecution(String executionId, Integer stepId, Integer tagCount) {
		String hql = "from "
			+ TestStepExecution.class.getName() + " t where t.executionId = :executionId and " + 
			"t.stepId = :stepId and t.tagCount = :tagCount";
		hibernateTemplate.setMaxResults(1);
		List<TestStepExecution> l = hibernateTemplate.findByNamedParam(hql, 
				new String[] {"executionId", "stepId", "tagCount"}, 
				new Object[] {executionId, stepId, tagCount});
		hibernateTemplate.setMaxResults(0);
		return l.size() > 0 ? (TestStepExecution) l.get(0) : null;
	}
	
}
