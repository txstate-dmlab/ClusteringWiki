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
<script type="text/javascript" src="includes/js/credentials.js"></script>
<link rel="stylesheet" href="includes/css/captcha.pack.css" type="text/css" media="screen" >
<link rel="stylesheet" href="includes/css/regForm.pack.css" type="text/css" media="screen" >
<%@ include file="banner.jspf" %>
<!-- being index -->
	<div id="box">
		<div id="logo">
			<span id="logow"><fmt:message key="app.menuTitle"/></span>
		</div>
		<div id="title">
			<h2>Change password</h2>
		</div>
	</div>
	
	<div id="rpanel">
	<strong>Please enter your new password and verify it and then click Submit.</strong> <br><br>
			<div id="regForm" class="cmxform">
				<form id="changePasswordForm" action="changePassword.html" onSubmit="return false;">
					<table cellpadding="1" cellspacing="2" border="0">   			
		      			<tr>
		      				<td class="label">
		        				<label for="password">New password: </label>
		        				<em class="error">*</em>
		        			</td>
		       				<td class="field">
		          				<input type="password" id="password" name="password" size="25" />
		        			</td>
		      			</tr>
		      			<tr>
		      				<td class="label">
							    <label for="cpassword">Confirm password: </label>
		     					<em class="error">*</em>
		        			</td>
		       				<td class="field">
		          				<input type="password" id="confirm_password" name="confirm_password" size="25" />
		        			</td>
		      			</tr> 
		 			</table>
		 			<div class="myCaptchaMessage">Let's make sure you're human.  Drag or click the correct shape on the left to move it to the grey "drop area" on the right.</div>
		            <input type="hidden" name="requestKey" id="requestKey" value="${requestKey}" />
		            <input type="hidden" name="myCaptchaResp" id="myCaptchaResp" />
		            <div id="myCaptcha"></div>
				<br>
				<div>
					<span class="regLink center" ><a href="javascript:changePassword()">Submit</a></span>
				</div>
				</form>
			</div> <br/>
			<div id="regResponse"></div>
		</div>
<!-- end index -->
<%@ include file="footer.jspf" %>  