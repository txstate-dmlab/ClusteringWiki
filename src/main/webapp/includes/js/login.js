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
 
 /**
 * Actions to occur on page load
 * @author David C. Anastasiu
 */
$().ready(function() {
	//set onEnter events for email and password
	onEnter('loginEmail', 'login');
    onEnter('loginPassword', 'login');
	// validate the login form
	$("#loginForm").validate({
		errorLabelContainer: $("#loginValidation")
	});
});

/**
 * Execute AJAX login 
 * @author David C. Anastasiu
 * @return void
 */
function login(){
	
	$('#loginResponse').html('');
	
	if(!$("#loginForm").valid()) return;
	
	//get form values
    var email = $('#loginEmail').val();
    var password = $('#loginPassword').val();
    
    $.ajax({
        url: 'processLogin.html',
        type: 'post',
		async: true,
		data: 'email=' + email + '&password=' + password,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    	//login successful.  redirect to search page
			    window.location.href="index.html";
		    } else {
		    	var err = 'Invalid server response.';
		    	if(typeof data.error != "undefined") err = data.error;
		    	$('#loginResponse').html(err);
		    	err = null;
		    }
		    email = null;
		    password = null;
		},
		error: function(oXHR, status){
			$('#loginResponse').html('Server connection error.  Please try again.');
		}
    });
    
}