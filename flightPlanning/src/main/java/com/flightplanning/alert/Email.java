package com.flightplanning.alert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class Email{
	
	private static String SMTP_HOST;
    private static String PORT_SMTP;
	private static String LOGIN_SMTP;
	private static String PASSWORD_SMTP;
	
	private static final Logger logger = Logger.getLogger(Email.class);
	
	private Email(){
		
	}
	
	public static void sendMessage(String subject, String text, String destinataire){
		
		readConfig();
		
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.socketFactory.port", PORT_SMTP);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", PORT_SMTP);

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(LOGIN_SMTP,PASSWORD_SMTP);
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(LOGIN_SMTP));
			message.setReplyTo(InternetAddress.parse("mail-noreply@google.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(destinataire));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);
			
			logger.info("Done email");

		} catch (MessagingException e) {
			logger.error(e);
		} 
    }
	
	private static void readConfig(){
		String chaine[] = new String[4];
		String fichier ="config/email.conf";
		
		InputStream ips = null; 
		InputStreamReader ipsr = null;
		BufferedReader br = null;
		
		//lecture du fichier texte	
		try{
			ips = new FileInputStream(fichier); 
			ipsr = new InputStreamReader(ips);
			br = new BufferedReader(ipsr);
			String ligne;
			
			for(int i=0;i<4;i++){
				if((ligne=br.readLine())!=null)
					chaine[i]=ligne;
			}
			
			ips.close();
			ipsr.close();
			br.close();
			
			SMTP_HOST = chaine[0];
		    PORT_SMTP = chaine[1];
			LOGIN_SMTP = chaine[2];
			PASSWORD_SMTP = chaine[3];
		}		
		catch (Exception e){
			try {
				if(br!=null)
					br.close();
				if(ipsr!=null)
					ipsr.close();
				if(ips!=null)
					ips.close();
			} catch (IOException e1) {
				logger.error(e1);
			}
			logger.error(e);
		}
	}
}
