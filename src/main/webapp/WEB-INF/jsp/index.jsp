<!--
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
-->
 
<%@ include file="documentHeaders.jspf" %>
<script type="text/javascript" src="includes/js/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="includes/js/jquery.scrollTo-min.js"></script>
<script type="text/javascript" src="includes/js/porter-stemmer.pack.js"></script>
<script type="text/javascript" src="includes/js/editableClusterTree.pack.js"></script>
<script type="text/javascript" src="includes/js/search.pack.js"></script>
<%@page import="edu.txstate.dmlab.clusteringwiki.app.IApplicationUser" %> 
<%@page import="edu.txstate.dmlab.clusteringwiki.web.AppContextListener" %>
<%@page import="edu.txstate.dmlab.clusteringwiki.web.BaseController" %>  
<%
  
	//get reference to application user
	IApplicationUser user = (IApplicationUser) AppContextListener.getWebAppCtx().getBean("applicationUser");

	//see if we are in a test
	String thisExecutionId = (String) request.getSession(true).getAttribute("executionId");
	boolean testCheck = thisExecutionId != null;

	%>
<script type="text/javascript">
	$(document).ready(function() {
		<% if(user != null && user.isLoggedIn()) { out.print("ect.setEditAllowed(true);"); } %>
		<% if(testCheck) { 
			out.print("startTest('" + thisExecutionId + "');"); 
		} %>
	});
</script>

<link rel="stylesheet" type="text/css" media="screen" href="includes/css/ecTree.pack.css" >
<%@ include file="banner.jspf" %>
<!-- being index -->
	<div id="box">
		<div id="logo">
			<span id="logow"><fmt:message key="app.menuTitle"/></span>
		</div>
		<div id="searchbox">
			<table border="0" cellpadding="2" cellspacing="2" width="600px">
			<tr>
				<td>
					<input type="text" name="query" id="query" title="Search" value="" maxlength="1000" size="40" class="search" autocomplete="off"/>
					<input type="text" name="queryTest" id="queryTest" title="Search" value="" maxlength="1000" size="40" class="search hidden" autocomplete="off" disabled="disabled"/>
					&nbsp;
					<img id="searchButton" src="includes/images/search.jpg" class="hovimg searchBtn" onclick="processQ()"/>
				</td>
			</tr>
			<tr>
				<td id="stats">
					&nbsp;
				</td>
			</tr>
			<tr id="r1">
				<td>
					Source:
					<select name="engine" id="engine">
						<option value="yahoo">Yahoo!</option>
						<option value="google" selected>Google</option>
						<option value="nytdata"<% if(!BaseController.isLocalRequest(request)){ %> disabled="disabled"<% } %>>NY Times Data</option>
						<option value="ap"<% if(!BaseController.isLocalRequest(request)){ %> disabled="disabled"<% } %>>AP Newswire</option>
						<option value="ft"<% if(!BaseController.isLocalRequest(request)){ %> disabled="disabled"<% } %>>Financial Times</option>
						<option value="ziff"<% if(!BaseController.isLocalRequest(request)){ %> disabled="disabled"<% } %>>Computer Select</option>
						<option value="cr"<% if(!BaseController.isLocalRequest(request)){ %> disabled="disabled"<% } %>>Congretional Record</option>
						<option value="patents"<% if(!BaseController.isLocalRequest(request)){ %> disabled="disabled"<% } %>>US Patent data</option>
					</select>
				
					&nbsp;
					Results:
					<select name="count" id="count">
						<option value="50" selected>50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
						<option value="400">400</option>
						<option value="500">500</option>
					</select>
				
					&nbsp;
					Algorithm:
					<select name="clusteringAlgo" id="clusteringAlgo">
						<option value="0">k-means flat</option>
						<option value="1">k-means hierarchical</option>
						<option value="2">frequent phrase flat</option>
						<option value="3" selected>frequent phrase hierarchical</option>
					</select>
				</td>
			</tr>		
			<tr id="r2">
				<td>
					Show cluster edits
					<input type="checkbox" name="showEdits" id="showEdits" />
					
					&nbsp;
					Show execution times
					<input type="checkbox" name="showTimes" id="showTimes" />
					
					&nbsp;
					Show popular queries
					<input type="checkbox" name="showQueries" id="showQueries" />
				</td>
			</tr>
			</table>
		</div>
	</div>		

</center>
	<div id="pageCenter">
	<br>
		<div id="testMessages" class="hidden"> Test instructions: &nbsp;
			<pre id="testMessagesMsg"></pre>
			<a id="testNext" class="hidden" href="#" onclick="nextTestStep()" >Next Step</a>
			&nbsp; &nbsp; &nbsp;
			<span id="remainingTagCount" class="hidden success"></span>
		</div>
		<div id="clusterEdits" class="hidden showBox">
			<pre id="clusterEditsMsg" class="showBoxMsg"></pre>
		</div>
		<div id="clusterTimes" class="hidden showBox">
			<pre id="clusterTimesMsg" class="showBoxMsg"></pre>
		</div>
		<div id="clusterQueries" class="hidden showBox">
			&nbsp; <a href="javascript:getLatestMostPopularQueries()">refresh</a> <br>
			<div id="clusterQueriesMsg" class="showBoxMsg"></div>
		</div>
		<div id="cluster">
			<div> &nbsp; &nbsp; <span id="ectMessage"></span> </div>
			<div id="clusterTree"> </div>
		</div>
		<div id="response">
			
		</div>
		<div id="responseFooter">
			
		</div>
	</div>
<center>
<!-- end index -->
<%@ include file="footer.jspf" %>  