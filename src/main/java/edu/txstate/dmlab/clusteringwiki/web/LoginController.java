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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.txstate.dmlab.clusteringwiki.dao.ICredentialsRequestDao;
import edu.txstate.dmlab.clusteringwiki.dao.IUserDao;
import edu.txstate.dmlab.clusteringwiki.entity.CredentialsRequest;
import edu.txstate.dmlab.clusteringwiki.entity.User;

/**
 * Controller for the login page
 * 
 * @author David C. Anastasiu
 *
 */
@Controller
public class LoginController extends BaseController {

	public static final String TEST_USER_EMAIL = "testcw@cs.txstate.edu";
	
	public static final String ADMIN_USER_EMAIL = "admincw@cs.txstate.edu";
	
	/**
	 * The base email sender
	 * Contains smtp address to be used when sending email defined in spring-web-servlet.xml
	 */
	@Autowired
	private MailSender mailSender;
	
	/**
	 * The base mailMessage to be used when sending email
	 * Contains From address and subject defined in spring-web-servlet.xml
	 */
	@Autowired
    private SimpleMailMessage mailMessage;
	
	/**
	 * Dao for credentials requests
	 */
	@Autowired
	ICredentialsRequestDao credentialsRequestDao;

	
	/**
	 * @return the mailSender
	 */
	public MailSender getMailSender() {
		return mailSender;
	}

	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(MailSender theMailSender) {
		mailSender = theMailSender;
	}

	/**
	 * @return the mailMessage
	 */
	public SimpleMailMessage getMailMessage() {
		return mailMessage;
	}

	/**
	 * @param mailMessage the mailMessage to set
	 */
	public void setMailMessage(SimpleMailMessage theMailMessage) {
		mailMessage = theMailMessage;
	}

	/**
	 * @return the credentialsRequestDao
	 */
	public ICredentialsRequestDao getCredentialsRequestDao() {
		return credentialsRequestDao;
	}

	/**
	 * @param credentialsRequestDao the credentialsRequestDao to set
	 */
	public void setCredentialsRequestDao(
			ICredentialsRequestDao theCredentialsRequestDao) {
		credentialsRequestDao = theCredentialsRequestDao;
	}

	@RequestMapping("login.*")
	public String getLoginPage(Model model){
		return "login";
	}
	
	@RequestMapping("processLogin.*")
	public void processLogin(HttpServletRequest request, HttpServletResponse response, Model model){
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		if(email == null || password == null){
			sendOutput(response, "{\"error\":\"Invalid login request received.\"}");
			return;
		}
		
		email = email.toLowerCase();
		
		if(email.equals("test") && password.toLowerCase().equals("test")){
			email = TEST_USER_EMAIL;
			password = "";
		} else if(email.equals("admin")){
			email = ADMIN_USER_EMAIL;
			applicationUser.setAdmin(true);
		}
		
		applicationUser.setEmail(email);
		applicationUser.setPassword(password);
		try {
			applicationUser.logIn();
		} catch (Exception e){
			sendOutput(response, "{\"error\":\"Login error: " + e.getMessage() + "\"}");
			return;
		}

		sendOutput(response, "{\"success\":true}");
		
	}
	
	@RequestMapping("logout.*")
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model){
		applicationUser.logOut();
		HttpSession session = request.getSession();
		final String testExecutionId = (String) session.getAttribute("executionId");
		if(session != null)
			session.invalidate();
		return testExecutionId == null ? "redirect:index.html" : 
			"redirect:index.html?test=" + testExecutionId;
	}
	
	@RequestMapping("credentials.*")
	public String getCredentialsPage(Model model){
		return "credentials";
	}
	
	@RequestMapping("changePassword.*")
	public String getChangePasswordPage(HttpServletRequest request, HttpServletResponse response, Model model){
		
		CredentialsRequest cred;
		
		// user clicked on email message allowing change of password
		// change password and display new temporary password.
		// user asked to change password on next login.
		// send an email with link to allow changing password
		String action = request.getParameter("applAction");
		if(action != null && action.equals("changePw") && isAjaxRequest(request)){
			//requesting registration
			String key = request.getParameter("requestKey");
			String password = request.getParameter("password");
			
			if(key == null || password == null){
				sendOutput(response, "{\"error\":\"Invalid password change request received.\"}");
				return null;
			}
			
			cred = credentialsRequestDao.selectCredentialsRequestByKey(key);
			if(cred == null){
				sendOutput(response, "{\"error\":\"Invalid password change request received.\"}");
				return null;
			}
			
			if(!cred.isValidRequest()){
				sendOutput(response, "{\"error\":\"The password change request received is no longer valid.  " +
					"Please submit a new request.\"}");
				return null;
			}
			
			if(!cred.isExpired()){
				sendOutput(response, "{\"error\":\"The password change request received has expired.  " +
					"Please submit a new request.\"}");
				return null;
			}
			
			String email = cred.getEmail();
			
			IUserDao dao = applicationUser.getUserDao();
			
			User user = dao.selectUserByEmail(email);
			if(user == null){
				sendOutput(response, "{\"error\":\"Invalid email.  Please try again.\"}");
				return null;
			}
			
			user.changePassword(password);
			dao.saveUser(user);

			//invalidate current request
			cred.setValid(0);
			credentialsRequestDao.saveCredentialsRequest(cred);
			
			sendOutput(response, "{\"success\":true}");
			return null;
		}
		
		//user needs to enter new password
		String key = request.getParameter("key");
		cred = credentialsRequestDao.selectCredentialsRequestByKey(key);
		if(cred == null){
			request.setAttribute("message", "Invalid password change request received.");
			return "pageError";
		}
		
		if(!cred.isValidRequest()){
			request.setAttribute("message", "The password change request received is no longer valid.  " +
				"Please submit a new request.");
			return "pageError";
		}
		
		if(!cred.isExpired()){
			request.setAttribute("message", "The password change request received has expired.  " +
				"Please submit a new request.");
			return "pageError";
		}
		
		model.addAttribute("requestKey", key);
		return "changePassword";
	}
	
	@RequestMapping("reminder.*")
	public void sendReminder(HttpServletRequest request, HttpServletResponse response, Model model){
		// send an email with link to allow changing password
		String action = request.getParameter("applAction");
		if(action != null && action.equals("sendReminder") && isAjaxRequest(request)){
			//requesting registration
			String email = request.getParameter("email");
			
			if(email == null){
				sendOutput(response, "{\"error\":\"Invalid reminder request received.\"}");
				return;
			}
			
			email = email.toLowerCase();
			
			IUserDao dao = applicationUser.getUserDao();
			
			User user = dao.selectUserByEmail(email);
			if(user == null){
				sendOutput(response, "{\"error\":\"Invalid email.  Please try again.\"}");
				return;
			}
			
			//create password request link
			CredentialsRequest cred = new CredentialsRequest();
			cred.setEmail(email);
			String link = request.getRequestURL().toString();
			link = link.replace("reminder.html", "changePassword.html?key=" + cred.getKey());
			
			//Create a thread safe "sandbox" of the mailMessage
	        SimpleMailMessage msg = new SimpleMailMessage(mailMessage);
	        msg.setTo(email);
	        msg.setText(
	            "Dear "
	                + user.getFirstName() + ", \n\n"
	                + "We have received a forgot password request at ClusteringWiki for the account "
	                + "associated with this email address.  If you did not initiate this request, please "
	                + "ignore this email message.  Otherwise, copy and paste the link below in your "
	                + "browser to complete your password reset.  Please note this forgot pasword request "
                  + "will expire in one hour. \n\n "
	                + link + "\n\nThank you,\n\nClusterWiki Admin");
	        try{
	            mailSender.send(msg);
	        }
	        catch(MailException ex) {
	        	if( ex.contains(com.sun.mail.smtp.SMTPAddressFailedException.class) ){
	        		sendOutput(response, "{\"error\":\"The email address is no longer valid.  Please contact an administrator or create a new account.\"}");
	        	} else if( ex.contains(com.sun.mail.smtp.SMTPSendFailedException.class) ){
	        		//ignore not being able to send this message out.
	        		sendOutput(response, "{\"error\":\"Email message could not be sent.  Please try again later.\"}");
	        	} else 
	        		sendOutput(response, "{\"error\":\"Email message could not be sent: <br><br>" + 
	            		StringEscapeUtils.escapeJavaScript( ex.getMessage().replace("\n", "<br>") ) + "\"}");
				return;
	        }
	        
	        //make valid and save credentials request
			cred.setValid(1);
	        try {
				credentialsRequestDao.saveCredentialsRequest(cred);
			} catch (Exception e){
				sendOutput(response, "{\"error\":\"Credential request could not be saved.  Please try again.\"}");
				return;
			}
			
			sendOutput(response, "{\"success\":true}");
			return;
		}
	}
	
}
