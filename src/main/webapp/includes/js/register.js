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
 
 
$().ready(function() {	
	$("#memberForm").validate({
		rules: {
			email: {
				required: true,
				maxlength: 80,
				email: true
			},
			password: {
				required: true,
				alphanumericonly2: true,
				rangelength: [3, 20]
			},
			confirm_password: {
				required: true,
				equalTo: "#password"
			},
			firstname: {
				maxlength: 30,
				lettersonly: true
			},			
			lastname: {			
				maxlength: 30,
				lettersonly: true
			},
			myCaptchaResp: {
				requiredCaptcha: true,				
				captchaCheck: true
			}
		},
		messages: {
			email: {
				email: "Please enter a valid email address"
			},
			password: {
				required: "Please provide a password",
				rangelength: "Your password must be between 3 and 20 characters long"
			},
			confirm_password: {
				required: "Please provide a password",
				equalTo: "Please enter the same password as above"
			}
		}
		
	});	
	
	onEnter('email', 'register');
	onEnter('password', 'register');
	onEnter('confirm_password', 'register');
	onEnter('firstname', 'register');
	onEnter('lastname', 'register');
	
	//set up captcha div
	$('#myCaptcha').sexyCaptcha('captcha.html');
	
});


/**
 * Register user via ajax
 * @return void
 */
function register(){
	$('#regResponse').removeClass('error').removeClass('success').html('');
	
	if(!$("#memberForm").valid()) return;
	
	$('#regResponse').html('Please wait...<img src="includes/images/loading.gif">');
	
	//get form values
	var useremail = $('#email').val();
	var password  = $('#password').val();
    var firstname = $('#firstname').val();
    var lastname  = $('#lastname').val();
    
    if(useremail == '' || password == ''){
        $('#regResponse').html('Email and password are required.');
        $('#regResponse').addClass('error');
        return;
    }
    
    $.ajax({
        url: 'register.html?applAction=register',	
        type: 'post',
		async: true,
		data: 'email=' + useremail + '&password=' + password + 
			'&firstname=' + firstname + '&lastname=' + lastname,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    	window.location.href = 'index.html';
		    } else {
		    	var err = 'Invalid server response.';
		    	if(typeof data.error) err = data.error;
		    	$('#regResponse').html(err);
		        $('#regResponse').addClass('error');
		        err = null;
		    }
		    useremail = null;
			password  = null;
		    firstname = null;
		    lastname  = null;
		},
		error: function(oXHR, status){
			$('#regResponse').html('Server connection error.  Please try again.');
			$('#regResponse').addClass('error');
		}
    });
    
}

