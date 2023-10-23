package com.company.sevice;

import org.springframework.stereotype.Component;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import org.springframework.stereotype.Service;


@Service
public class EmailService {
	public boolean sendEmail(String subject, String message, String to) {
		boolean f = false;
		String from = "rajansingh147580@gmail.com";
		String host ="smtp.gmail.com";
		
		Properties properties = System.getProperties();
		System.out.println("Properties "+ properties);
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("rajansingh147580@gmail.com", "dpbn ezpe tmrj txxb");
			}
		});
		
		session.setDebug(true);
		MimeMessage m = new MimeMessage(session);
		try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			m.setText(message);
			
			Transport.send(m);
			System.out.println("Sent success.......");
			f=true;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return f;
	}
	
} 
