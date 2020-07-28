package com.acsz.systemtest.utils;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public final class EmailUtils {

	public static final String SMTP_USERNAME = "test@gamil.com";
	public static final String SMTP_PASSWORD = "12312314";
	public static final String SMTP_HOST = "smtp.gmail.com";
	public static final int SMTP_PORT = 587;

	public static boolean emailSender(String[] emailIds, String msg, String subject, final String fileNameLocation) {
		boolean isSent = false;
		Properties prop = new Properties();
		prop.put("mail.smtp.host", SMTP_HOST);
		prop.put("mail.smtp.port", SMTP_PORT);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SMTP_USERNAME));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", emailIds)));
			message.setSubject(subject);

			Multipart multipart = new MimeMultipart();

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(msg);
			multipart.addBodyPart(messageBodyPart);

			MimeBodyPart attachPart = new MimeBodyPart();

			DataSource source = new FileDataSource(fileNameLocation);
			attachPart.setDataHandler(new DataHandler(source));
			attachPart.setFileName(new File(fileNameLocation).getName());

			multipart.addBodyPart(attachPart);

			message.setContent(multipart);
			Transport.send(message);
			isSent = true;
		} catch (MessagingException e) {
			isSent = false;
			e.printStackTrace();
		}
		return isSent;
	}

}
