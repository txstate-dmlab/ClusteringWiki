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

import edu.txstate.dmlab.clusteringwiki.entity.CredentialsRequest;

/**
 * Data access object public interface for a credentials request record
 * 
 * @author David C. Anastasiu
 *
 */
public interface ICredentialsRequestDao {
	public void saveCredentialsRequest(CredentialsRequest request);
	public CredentialsRequest selectCredentialsRequestById(Integer id);
	public CredentialsRequest selectCredentialsRequestByKey(String key);
	public void deleteCredentialsRequest(CredentialsRequest request);
}
