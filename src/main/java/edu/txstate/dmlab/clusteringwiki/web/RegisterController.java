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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

import edu.txstate.dmlab.clusteringwiki.dao.IUserDao;
import edu.txstate.dmlab.clusteringwiki.entity.User;

/**
 * Controller for the register page
 * 
 * @author David C. Anastasiu
 *
 */
@Controller
public class RegisterController extends BaseController {

	
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
	
	@RequestMapping("register.*")
	public String getRegisterPage(HttpServletRequest request, HttpServletResponse response, Model model){
		
		String action = request.getParameter("applAction");
		if(action != null && action.equals("register") && isAjaxRequest(request)){
			//requesting registration
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String firstName = request.getParameter("firstname");
			String lastName = request.getParameter("lastname");
			
			if(email == null || password == null){
				sendOutput(response, "{\"error\":\"Invalid registration request received.\"}");
				return null;
			}
			
			email = email.toLowerCase();
			
			IUserDao dao = applicationUser.getUserDao();
			
			User user = dao.selectUserByEmail(email);
			if(user != null){
				sendOutput(response, "{\"error\":\"An account is already registered with this " + 
					"email. Please use the forgot password feature to retrieve your credentials " +
					"or choose an alternate email address.\"}");
				return null;
			}
			
			//Create a thread safe "sandbox" of the mailMessage
	        SimpleMailMessage msg = new SimpleMailMessage(mailMessage);
	        msg.setSubject("ClusteringWiki account created");
	        msg.setTo(email);
	        msg.setText(
	            "Dear "
	                + firstName + ", \n\n"
	                + "A ClusteringWiki account has been created for this email address.  Log in to ClusteringWiki to "
	                + "start editing search result clusters.  Right-click on nodes to access available "
	                + "editing operations.  Your edits will improve search for you as well as others "
	                + "quering similar things.  \n\n "
	                + "\n\nThank you,\n\nClusteringWiki Admin");
	        try{
	            mailSender.send(msg);
	        }
	        catch(MailException ex) {
	        	if( ex.contains(com.sun.mail.smtp.SMTPAddressFailedException.class) ){
	        		sendOutput(response, "{\"error\":\"Invalid email address.  Please specify a valid email address.\"}");
	        	} else if( !ex.contains(com.sun.mail.smtp.SMTPSendFailedException.class) ){
	        		//ignore not being able to send this message out.
	        		sendOutput(response, "{\"error\":\"Email message could not be sent: <br><br>" + 
	            		StringEscapeUtils.escapeJavaScript( ex.getMessage().replace("\n", "<br>") ) + "\"}");
	        	}
	        	return null;
	        }
			
			user = new User(); 
			user.setEmail(email);
			user.setPassword(password);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			try {
				dao.saveUser(user); 
			} catch (Exception e){
				sendOutput(response, "{\"error\":\"Registration failed: " + e.getMessage() + ".\"}");
				return null;
			}
			
			applicationUser.setEmail(email);
			applicationUser.setPassword(password);
			try {
				applicationUser.logIn();
			} catch (Exception e){
				sendOutput(response, "{\"error\":\"Login error: " + e.getMessage() + "\"}");
				return null;
			}
			
			sendOutput(response, "{\"success\":true}");
			return null;
		}
		
		return "register";
	}
	
	@RequestMapping("captcha.*")
	public void getCaptcha(HttpServletRequest request, HttpServletResponse response, Model model){

		//prepare vars
		HttpSession session = request.getSession();
		
		String action = request.getParameter("action");
		if(action == null){
			sendOutput(response, "Error: invalid action received.");
			return;
		}
		
		if(action.equals("verify")){
			String captcha = request.getParameter("captcha");
			String answer = (String) session.getAttribute("captchaAnswer");
			if(captcha == null || captcha.length() < 10 || answer == null) {
				sendOutput(response, "Error: invalid verify request received.");
				return;
			}
			if(captcha.substring(10).equals(answer)){
				sendOutput(response, "{\"status\":\"success\"}");
				return;
			} else {
				sendOutput(response, "{\"status\":\"error\"}");
				return;
			}
		}
		
		// tops: star: 120, heart: 0; bwm: 56; diamond: 185
		
		List<Integer> topPositions = new ArrayList<Integer>(4);
		topPositions.add(120);
		topPositions.add(0);
		topPositions.add(56);
		topPositions.add(185);
		
		//randomize images
		Collections.shuffle(topPositions);
		
		//generate new codes
		Random r = new Random();
		List<String> codes = new ArrayList<String>(4);
		codes.add( Long.toString(Math.abs(r.nextLong()), 36) );
		codes.add( Long.toString(Math.abs(r.nextLong()), 36) );
		codes.add( Long.toString(Math.abs(r.nextLong()), 36) );
		codes.add( Long.toString(Math.abs(r.nextLong()), 36) );

		//pick a random item to check against - between 0 and 4
		int chosen = r.nextInt(4);
		
		//create the html code
		String template = "<div class=\"captchaWrapper\" id=\"captchaWrapper\">" +
			"	<a href=\"#\" class=\"captchaRefresh\"></a>" +
			"	<div id=\"draggable_{c0}\" class=\"draggable\" " +
			"		style=\"left: 15px; background-position: -{t0}px -3px\"></div>" +
			"	<a href=\"#\" class=\"captchaRefresh\"></a>" +
			"	<div id=\"draggable_{c1}\" class=\"draggable\" " +
			"		style=\"left: 83px; background-position: -{t1}px -3px;\"></div>" +
			"	<a href=\"#\" class=\"captchaRefresh\"></a>" +
			"	<div id=\"draggable_{c2}\" class=\"draggable\" " +
			"		style=\"left: 151px; background-position: -{t2}px -3px;\"></div>" +
			"	<a href=\"#\" class=\"captchaRefresh\"></a>" +
			"	<div id=\"draggable_{c3}\" class=\"draggable\" " +
			"		style=\"left: 219px; background-position: -{t3}px -3px;\"></div>" +
			"	<div class=\"targetWrapper\">" +
			"		<div class=\"target\" style=\"background-position: -{t4}px -66px;\"></div>" +
			"	</div>" +
			"	<input type=\"hidden\" class=\"captchaAnswer\" name=\"captcha\" value=\"\" />" +
			"</div>";
		
		template = template.replace("{c0}", codes.get(0))
					.replace("{c1}", codes.get(1))
					.replace("{c2}", codes.get(2))
					.replace("{c3}", codes.get(3))
					.replace("{t0}", String.valueOf( topPositions.get(0) ) )
					.replace("{t1}", String.valueOf( topPositions.get(1) ) )
					.replace("{t2}", String.valueOf( topPositions.get(2) ) )
					.replace("{t3}", String.valueOf( topPositions.get(3) ) );
		// the chosen image
		template = template.replace("{t4}", String.valueOf( topPositions.get(chosen) ) );
		
		//store the chosen code in the session
		session.setAttribute("captchaAnswer", codes.get(chosen));
		
		//display captcha
		sendOutput(response, template);
		
	}
	
}
