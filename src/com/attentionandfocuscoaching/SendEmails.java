package com.attentionandfocuscoaching;

import java.util.Properties;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
* Class for sending emails to clients of website.
**/
public class SendEmails extends HttpServlet {
	//send gift to user
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get form params
		String name = request.getParameter("name");
		String userEmail = request.getParameter("email");
		
		//validate form params
		boolean nameValid = true;
		boolean emailValid = true;
		
		String nameRegex = "^[[\\w]+[\\s]+[\\w]+[\\s]+]+$";
		String emailRegex = "(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
		
		if(!(name.matches(nameRegex))) {	
			nameValid = false;
		}
		if(!(userEmail.matches(emailRegex))) {	
			emailValid = false;
		}
		
		if((nameValid == false) && (emailValid == false)) {
			response.sendRedirect("giftNameAndEmailInvalid.html");
			return;
		}
		else if(nameValid == false) {
			response.sendRedirect("giftNameInvalid.html");
			return;
		}	
		else if(emailValid == false) {	
			response.sendRedirect("giftEmailInvalid.html");
			return;
		}

		//set email params
		String owner = "######################"; 
		String admin = "###################################";
		String username = "#############################";
		String password = "###################";
		String host = "##################";
		
		//set properties
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true"); 
		props.put("mail.smtp.starttls.enable", "true");
		
		//start session
		Session session = Session.getInstance(props,
		 new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
			   return new PasswordAuthentication(username, password);
			}
		 }
		);
		
		// Send gift to user
		try {
			//set email params
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(admin));
			message.setRecipient(Message.RecipientType.TO,new InternetAddress(userEmail));
			message.setSubject("Gift from Peter Resch at Attention and Focus Coaching LLC");
			
			//create multipart email
			Multipart multipart = new MimeMultipart();
			
			//email message body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Thank you for your interest in my services.  Enjoy your gift!");
			multipart.addBodyPart(messageBodyPart);
			
			//add attachment
			messageBodyPart = new MimeBodyPart();
			String fileName = System.getProperty("catalina.base") + "/webapps/CoachingWebsite/attachments/PeterReschResume.pdf";
			DataSource source = new FileDataSource(fileName);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			multipart.addBodyPart(messageBodyPart);
			//send gift email to customer
			message.setContent(multipart);
			Transport.send(message);
		}	
		catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		// Send user info to admin
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(admin));
			message.setRecipient(Message.RecipientType.TO,new InternetAddress(owner));
			message.setSubject("Customer Information from Website");
			
			//insert customer info into email body
			message.setText("Name: " + name + "\nEmail: " + userEmail);
			
			//send customer info to admin
			Transport.send(message);
		} 
		catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		response.sendRedirect("emailSentSuccess.html");
	}
}