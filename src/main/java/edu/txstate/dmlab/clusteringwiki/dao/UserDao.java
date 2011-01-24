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

import edu.txstate.dmlab.clusteringwiki.entity.User;

/**
 * Data access object for a user record
 * 
 * @author David C. Anastasiu
 *
 */
@Repository("usersDao")
@Transactional
public class UserDao implements IUserDao {
	private HibernateTemplate hibernateTemplate;

	@Autowired 
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		user.setLastLogin(new Date());
		hibernateTemplate.saveOrUpdate(user);
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		hibernateTemplate.delete(user);
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUser(User user) {
		return (List<User>) hibernateTemplate.find("from "
				+ User.class.getName());
	}

	public User selectUserById(Integer id) {
		return hibernateTemplate.get(User.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public User selectUserByEmail(String email){
		String hql = "from "
			+ User.class.getName() + " u where u.email = :email";
		hibernateTemplate.setMaxResults(1);
		List<User> l = hibernateTemplate.findByNamedParam(hql, "email", email);
		hibernateTemplate.setMaxResults(0);
		return l.size() > 0 ? (User) l.get(0) : null;
	}

}