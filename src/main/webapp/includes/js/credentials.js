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
	
	// validate the login form
	if($("#reminderForm").length > 0) {
		$("#reminderForm").validate({
			rules: {
				email: {
					required: true,
					maxlength: 80,
					email: true
				},
				myCaptchaResp: {
					requiredCaptcha: true,				
					captchaCheck: true
				}
			},
			messages: {
				email: {
					email: "Please enter a valid email address"
				}
			}
		
		});	
		
		//set onEnter events for username and password
		onEnter('email', 'sendReminder');
	}
	
	if($("#changePasswordForm").length > 0) {
		$("#changePasswordForm").validate({
			rules: {
				password: {
					required: true,
					alphanumericonly2: true,
					rangelength: [3, 20]
				},
				confirm_password: {
					required: true,
					equalTo: "#password"
				},
				myCaptchaResp: {
					requiredCaptcha: true,				
					captchaCheck: true
				}
			},
			messages: {
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
		
		//set onEnter events for password and confirmPassword
		onEnter('password', 'changePassword');
		onEnter('confirm_password', 'changePassword');
	}
	
	//set up captcha div
	$('#myCaptcha').sexyCaptcha('captcha.html');
	
});

function sendReminder(){

	$('#regResponse').removeClass('error').removeClass('success').html('');

	if(!$("#reminderForm").valid()) return;
	
	$('#regResponse').html('Please wait...<img src="includes/images/loading.gif">');
	
	//get form values
	var useremail = $('#email').val();
	useremail = $.trim(useremail);
    
    if(useremail == ''){
        $('#regResponse').html('Email cannot be empty.');
        $('#regResponse').addClass('error');
        return;
    }
    
    $.ajax({
        url: 'reminder.html?applAction=sendReminder',	
        type: 'post',
		async: true,
		data: 'email=' + useremail,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    		$('#reminderForm').hide();
		    		$('#regResponse').html('A password reminder email has been sent to the email address you specified.' +
		    			'  Please click on the link in the email to continue.');
		    		$('#regResponse').addClass('success');
		    } else {
		    	var err = 'Invalid server response.';
		    	if(typeof data.error) err = data.error;
		    	$('#regResponse').html(err);
		        $('#regResponse').addClass('error');
		        err = null;
		    }
		    useremail = null;
		},
		error: function(oXHR, status){
			$('#regResponse').html('Server connection error.  Please try again.');
			$('#regResponse').addClass('error');
		}
    });
}

function changePassword(){

	$('#regResponse').removeClass('error').removeClass('success').html('');

	if(!$("#changePasswordForm").valid()) return;
	
	$('#regResponse').html('Please wait...<img src="includes/images/loading.gif">');
	
	//get form values
	var key = $('#requestKey').val();
	key = $.trim(key);
	var password = $('#password').val();
    
    if(key == ''){
        $('#regResponse').html('Key cannot be empty.');
        $('#regResponse').addClass('error');
        return;
    }
    
    $.ajax({
        url: 'changePassword.html?applAction=changePw',	
        type: 'post',
		async: true,
		data: 'requestKey=' + key + '&password=' + password,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    		$('#changePasswordForm').hide();
		    		$('#regResponse').html('Password changed successfully.' +
		    			'  Please log in.');
		    		$('#regResponse').addClass('success');
		    } else {
		    	var err = 'Invalid server response.';
		    	if(typeof data.error) err = data.error;
		    	$('#regResponse').html(err);
		        $('#regResponse').addClass('error');
		        err = null;
		    }
		    key = null;
		    password = null;
		},
		error: function(oXHR, status){
			$('#regResponse').html('Server connection error.  Please try again.');
			$('#regResponse').addClass('error');
		}
    });
}