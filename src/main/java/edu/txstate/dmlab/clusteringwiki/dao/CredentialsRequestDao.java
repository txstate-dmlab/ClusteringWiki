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

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.txstate.dmlab.clusteringwiki.entity.CredentialsRequest;

/**
 * Data access object for a credentials request record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("credentialsRequestDao")
@Transactional
public class CredentialsRequestDao implements ICredentialsRequestDao {

	/**
	 * How far back are requests valid
	 */
	public static long CREDENTIALS_REQUEST_MAX_TIME_INTERVAL = 1000*60*60*24L;
	
	private HibernateTemplate hibernateTemplate;

	@Autowired 
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	@Transactional(readOnly = false)
	public void deleteCredentialsRequest(CredentialsRequest request) {
		hibernateTemplate.delete(request);
	}

	@Transactional(readOnly = false)
	public void saveCredentialsRequest(CredentialsRequest request) {
		hibernateTemplate.saveOrUpdate(request);
	}

	public CredentialsRequest selectCredentialsRequestById(Integer id) {
		return hibernateTemplate.get(CredentialsRequest.class, id);
	}

	@SuppressWarnings("unchecked")
	public CredentialsRequest selectCredentialsRequestByKey(String key) {
		String hql = "from "
			+ CredentialsRequest.class.getName() + " r where r.key = :key and r.requestTime > :time";
		hibernateTemplate.setMaxResults(1);
		Date time = new Date( System.currentTimeMillis() - CREDENTIALS_REQUEST_MAX_TIME_INTERVAL );
		List<CredentialsRequest> l = hibernateTemplate.findByNamedParam(hql, 
			new String[] {"key", "time"}, 
			new Object[] {key, time});
		hibernateTemplate.setMaxResults(0);
		return l.size() > 0 ? (CredentialsRequest) l.get(0) : null;
	}

}
