package edu.txstate.dmlab.clusteringwiki.web;

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

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Context Listener will register the application context statically
 * so it can be accessed from other parts of the application
 * 
 * @author David C. Anastasiu
 *
 */
public class AppContextListener extends ContextLoaderListener {
	
	private static WebApplicationContext webAppCtx = null;

	/**
	 * @return the webAppCtx
	 */
	public static WebApplicationContext getWebAppCtx() {
		return webAppCtx;
	}

	/**
	 * @param webAppCtx the webAppCtx to set
	 */
	public static void setWebAppCtx(WebApplicationContext webAppCtx) {
		AppContextListener.webAppCtx = webAppCtx;
	}

	public void contextInitialized( ServletContextEvent event ) {
        super.contextInitialized( event );
        webAppCtx = WebApplicationContextUtils.getRequiredWebApplicationContext( event.getServletContext() );
    }

    public void contextDestroyed( ServletContextEvent event ) {
        webAppCtx = null;
        super.contextDestroyed( event );
    }

    public static WebApplicationContext getWebApplicationContext() {
        return webAppCtx;
    }
    
}
