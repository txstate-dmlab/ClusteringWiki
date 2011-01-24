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

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.txstate.dmlab.clusteringwiki.entity.TestDetail;
import edu.txstate.dmlab.clusteringwiki.entity.TestStep;

/**
 * Data access object for a  TestDetail record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("testDetailDao")
@Transactional
public class TestDetailDao {

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
	public void saveTestDetail(TestDetail t) {
		hibernateTemplate.saveOrUpdate(t);
	}

	public TestDetail selectTestDetailById(Integer id) {
		return hibernateTemplate.get(TestDetail.class, id);
	}
	
}
