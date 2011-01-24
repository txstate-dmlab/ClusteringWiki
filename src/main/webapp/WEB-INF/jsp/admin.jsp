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
<script type="text/javascript" src="includes/js/jquery.metadata.pack.js"></script>
<script type="text/javascript" src="includes/js/jquery.validate.pack.js"></script>
<script type="text/javascript" src="includes/js/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="includes/js/jquery.sexy-captcha-0.1.pack.js"></script>
<script type="text/javascript" src="includes/js/additional-methods.pack.js"></script>
<script type="text/javascript" src="includes/js/menu.pack.js"></script>
<script type="text/javascript" src="includes/js/admin.pack.js"></script>
<link rel="stylesheet" href="includes/css/captcha.pack.css" type="text/css" media="screen" >
<link rel="stylesheet" href="includes/css/regForm.pack.css" type="text/css" media="screen" >
<%@ include file="banner.jspf" %>
<%@page import="edu.txstate.dmlab.clusteringwiki.app.ApplicationSettings" %> 
<%@page import="edu.txstate.dmlab.clusteringwiki.cluster.JaccardSimilarityCalculator" %> 
<%@page import="edu.txstate.dmlab.clusteringwiki.cluster.CosineSimilarityCalculator" %> 
<!-- being index -->
	<div id="box">
		<div id="logo">
			<span id="logow"><fmt:message key="app.menuTitle"/></span>
		</div>
		<div id="title">
			<h2>Application settings administration</h2>
		</div>
	</div>
	
	<div id="rpanel">
	<strong>Edit the settings below and click Save.</strong> <br><br>
			<div id="adminForm" class="cmxform">
				<form id="settgingsForm" action="admin.html" onSubmit="return false;">
					<table cellpadding="1" cellspacing="2" border="0">
		      			<tr>
		      				<td class="label">
		     					<label for="timingEnabled">Timing enabled: </label>  			   			
		        			</td>
		       				<td class="field">
				     			<input type="checkbox" id="timingEnabled" name="timingEnabled" <% 
				     				if(ApplicationSettings.isTimingEnabled()) out.print("checked"); %>  />	
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
		        				<label for="topKQueryUrls">topKQueryUrls: </label>
		        			</td>
		       				<td class="field">
		          				<input type="text" id="topKQueryUrls" name="topKQueryUrls" value="<%
		          					out.print( ApplicationSettings.getTopKQueryUrls() ); %>" size="3" maxlength="3" />
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
		        				<label for="maxClusteringIterations">maxClusteringIterations: </label>
		        			</td>
		       				<td class="field">
		          				<input type="text" id="maxClusteringIterations" name="maxClusteringIterations" value="<%
		          					out.print( ApplicationSettings.getMaxClusteringIterations() ); %>" size="3" maxlength="3" />
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
		        				<label for="similarityCalculator">similarityCalculator: </label>
		        			</td>
		       				<td class="field">
		          				<select name="similarityCalculator" id="similarityCalculator">
		          					<option value=""></option>
		          					<option value="jaccard"<%
			          					if(ApplicationSettings.getSimilarityCalculator() instanceof JaccardSimilarityCalculator) 
			          						out.print(" selected");%>>jaccard</option>
		          					<option value="cosine"<%
			          					if(ApplicationSettings.getSimilarityCalculator() instanceof CosineSimilarityCalculator) 
			          						out.print(" selected");%>>cosine</option>
		          				</select>
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
		        				<label for="termSimQueryResultsLimit">termSimQueryResultsLimit: </label>
		        			</td>
		       				<td class="field">
		          				<input type="text" id="termSimQueryResultsLimit" name="termSimQueryResultsLimit" value="<%
		          					out.print( ApplicationSettings.getTermSimQueryResultsLimit() ); %>" size="3" maxlength="3" />
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
		        				<label for="termSimThreshold">termSimThreshold: </label>
		        			</td>
		       				<td class="field">
		          				<input type="text" id="termSimThreshold" name="termSimThreshold" value="<%
		          					out.print( ApplicationSettings.getTermSimThreshold() ); %>" size="3" maxlength="4" />
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
		        				<label for="resultSimThreshold">resultSimThreshold: </label>
		        			</td>
		       				<td class="field">
		          				<input type="text" id="resultSimThreshold" name="resultSimThreshold" value="<%
		          					out.print( ApplicationSettings.getResultSimThreshold() ); %>" size="3" maxlength="4" />
		        			</td>
		      			</tr>
		 			</table>
				</form>
				<br>
				<div>
					<span class="regLink center" ><a href="javascript:save()">Save</a></span>
				</div>
			</div> <br/>
			<div id="saveResponse"></div>
		</div>
<!-- end index -->
<%@ include file="footer.jspf" %>  