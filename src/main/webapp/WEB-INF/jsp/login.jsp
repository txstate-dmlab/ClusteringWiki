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
<script type="text/javascript" src="includes/js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="includes/js/jquery.metadata.pack.js"></script>
<script type="text/javascript" src="includes/js/jquery.validate.pack.js"></script>

<script type="text/javascript" src="includes/js/login.pack.js"></script>
<script type="text/javascript" src="includes/js/menu.pack.js"></script>
<link rel="stylesheet" href="includes/css/login.pack.css" type="text/css" media="screen" >
<%@ include file="banner.jspf" %>
<!-- being index -->
	<div id="box">
		<div id="logo">
			<span id="logow"><fmt:message key="app.menuTitle"/></span>
		</div>
		<div id="title">
			<h2></h2>
		</div>
	</div>
	

    <div class="inner_login">
      <div align="left"  class="login_form">
        <form id="loginForm" action="processLogin.html" onSubmit="return false;">
        <h3>Login</h3>
        <div>
         <span>
          <label for="loginEmail">Email</label> &nbsp; <em class="error">*</em>
			<input type="text" name="loginEmail" id="loginEmail" title="Please enter your email.  <br>" class="required" maxLength="80" size="26" autocomplete="off">
		</span>
        </div>
        <div>
         <span>
         	<label for="loginPassword">Password</label> &nbsp; <em class="error">*</em>
			<input type="password" name="loginPassword" id="loginPassword" title="Please enter your password.  <br>" class="required" maxLength="20" size="26" autocomplete="off">
		</span>
        </div>
        <div>
        	<span>
        		<input type="image" name="login_btn" id="login_btn" src="includes/images/login.gif" onclick="login(); return false;" />
        	</span>
        </div>
        <br/>
        <span class="register"><a href="register.html">Register</a></span>
        <span class="forgotPass"><a href="credentials.html">forgot password?</a></span>
        </form>
      </div>
    </div>
	<div class="message">
		<br/>
		<strong>Note: </strong>Use <i>test / test</i> if you do not wish to register.
	</div>	
	<br/>
	
	<div id="loginValidation"></div>
	
	<div id="loginResponse" class="error"></div>
	
	<br/>
	<br/>
	
<!-- end index -->
<%@ include file="footer.jspf" %>  