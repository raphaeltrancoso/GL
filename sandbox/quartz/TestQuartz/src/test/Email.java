package test;

import java.util.Properties;
import java.util.Date;
import javax.mail.*;
import javax.mail.internet.*;

public class Email {
	
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String PORT_SMTP = "465";
	private static final String LOGIN_SMTP = "thebaseur@gmail.com";
	private static final String PASSWORD_SMTP = "billel75";
	
	public static void sendMessage(String subject, String text, String destinataire, String copyDest) { 
		
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
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(copyDest));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
    } 
}
