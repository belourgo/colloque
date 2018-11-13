//package ci.smile.colloque.helper;
//
//import java.io.File;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring4.SpringTemplateEngine;
//
//import ci.smile.sodexamcollecte.helper.contract.Response;
//import ci.smile.sodexamcollecte.helper.dto.UtilisateurDto;
//
//@Component
//public class EmailSenderUtils {
//	
//	public static final String FROM_NAME = "from_name";
//	public static final String FROM_EMAIL = "from_email";
//	
//	public static final String TO_NAME = "to_name";
//	public static final String TO_EMAIL = "to_email";
//	@Autowired
//	private TemplateEngine templateEngine;
//	
//	@Autowired
//	private SpringTemplateEngine templateEngineSpring;
//	
//	@Autowired
//	private Environment environment;
//	
//	private EmailSenderUtils() {
//	}
//	
//	private static EmailSenderUtils instance;
////	public static synchronized EmailSenderUtils getInstance(){
////		if (instance == null){
////			instance = new EmailSenderUtils();
////		}
////		return instance;
////	}
//
//	@Autowired
//	private ParamsUtils paramsUtils;
//
//	public void sendEmailWithAttachments(Map<String, String> from, List<Map<String, String>> toRecipients, String subject, String body, List<String> attachmentsFilesAbsolutePaths) {
//		
//		if (toRecipients == null || toRecipients.isEmpty()){
//			return;
//		}
//		
//		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
//		try {
//			Boolean auth = false;
//			javaMailSender.setHost(paramsUtils.getSmtpHost());
//			if (paramsUtils.getSmtpPort() != null && paramsUtils.getSmtpPort() > 0) {
//				javaMailSender.setPort(paramsUtils.getSmtpPort());
//			} else {
//				javaMailSender.setPort(25);
//			}
//			if (Utilities.notBlank(paramsUtils.getSmtpLogin()) && Utilities.notBlank(paramsUtils.getSmtpLogin())) {
//				javaMailSender.setUsername(paramsUtils.getSmtpLogin());
//				javaMailSender.setPassword(paramsUtils.getSmtpPassword());
//				auth = true;
//			}
//			javaMailSender.setJavaMailProperties(getMailProperties(paramsUtils.getSmtpHost(), auth));
//
//			MimeMessage message = javaMailSender.createMimeMessage();
//
//			MimeMessageHelper msgHelper = new MimeMessageHelper(message, true);
//
//			// sender
//			if (from != null && !from.isEmpty()) {
//				msgHelper.setFrom(new InternetAddress(from.get(FROM_EMAIL), from.get(FROM_NAME)));
//			} else {
//				msgHelper.setFrom(new InternetAddress(paramsUtils.getSmtpLogin(), "CEPICI"));
//			}
//
//			// recipients
//			List<InternetAddress> to = new ArrayList<InternetAddress>();
//			for (Map<String, String> recipient : toRecipients) {
//				String toName = recipient.get(TO_NAME);
//				if (toName != null && !toName.isEmpty()) {
//					to.add(new InternetAddress(recipient.get(TO_EMAIL), recipient.get(TO_NAME)));
//				} else {
//					to.add(new InternetAddress(recipient.get(TO_EMAIL)));
//				}
//			}
//			msgHelper.setTo(to.toArray(new InternetAddress[0]));
//
//			// Subject and body
//			msgHelper.setSubject(subject);
//			msgHelper.setText(body);
//
//			// Attachments
//			if (attachmentsFilesAbsolutePaths != null && !attachmentsFilesAbsolutePaths.isEmpty()) {
//				for (String attachmentPath : attachmentsFilesAbsolutePaths) {
//					File pieceJointe = new File(attachmentPath);
//					FileSystemResource file = new FileSystemResource(attachmentPath);
//					if (pieceJointe.exists() && pieceJointe.isFile()) {
//						msgHelper.addAttachment(file.getFilename(), file);
//					}
//				}
//			}
//
//			javaMailSender.send(message);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Properties getMailProperties(String host, Boolean auth) {
//		Properties properties = new Properties();
//		properties.setProperty("mail.transport.protocol", "smtp");
//		properties.setProperty("mail.smtp.auth", auth.toString());
//		properties.setProperty("mail.smtp.starttls.enable", "true");
//		properties.setProperty("mail.smtp.starttls.required", "true");
//		// properties.setProperty("mail.debug", "true");
//		if (host.equals("smtp.orange.ci"))
//			properties.setProperty("mail.smtp.ssl.trust", "smtp.orange.ci");
//		return properties;
//	}
//	
//	//Ajout SENDMAIL
//	
//	@Async
//	public Response<UtilisateurDto> sendEmail(Map<String, String> from, List<Map<String, String>> toRecipients, String subject,
//			String body, List<String> attachmentsFilesAbsolutePaths, Context context, String templateName,Locale locale) {
//		Response<UtilisateurDto>  response = new Response<UtilisateurDto>();
//		if (toRecipients == null || toRecipients.isEmpty()){
//			response.setHasError(Boolean.TRUE);
//			return response;
//		}
//		
//		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
//		try {
//			Boolean auth = false;
//			javaMailSender.setHost(paramsUtils.getSmtpHost());
//			if (paramsUtils.getSmtpPort() != null && paramsUtils.getSmtpPort() > 0) {
//				javaMailSender.setPort(paramsUtils.getSmtpPort());
//			} else {
//				javaMailSender.setPort(25);
//			}
//			if (Utilities.notBlank(paramsUtils.getSmtpLogin()) && Utilities.notBlank(paramsUtils.getSmtpPassword())) {
//				javaMailSender.setUsername(paramsUtils.getSmtpLogin());
//				javaMailSender.setPassword(paramsUtils.getSmtpPassword());
//				auth = true;
//			}
//			javaMailSender.setJavaMailProperties(getMailProperties(paramsUtils.getSmtpHost(), auth));
//
//			MimeMessage message = javaMailSender.createMimeMessage();
//
//			MimeMessageHelper msgHelper = new MimeMessageHelper(message, true);
//
//			// sender
//			if (from != null && !from.isEmpty()) {
//				msgHelper.setFrom(new InternetAddress(from.get(FROM_EMAIL), from.get(FROM_NAME)));
//			} else {
//				msgHelper.setFrom(new InternetAddress(paramsUtils.getSmtpLogin(), "OGO APPLICATION"));
//			}
//
//			// recipients
//			List<InternetAddress> to = new ArrayList<InternetAddress>();
//			for (Map<String, String> recipient : toRecipients) {
//				String toName = recipient.get(TO_NAME);
//				if (toName != null && !toName.isEmpty()) {
//					to.add(new InternetAddress(recipient.get(TO_EMAIL), recipient.get(TO_NAME)));
//				} else {
//					to.add(new InternetAddress(recipient.get(TO_EMAIL)));
//				}
//			}
//			msgHelper.setTo(to.toArray(new InternetAddress[0]));
//
//			// Subject and body
//			msgHelper.setSubject(subject);
//			body = templateEngine.process(templateName+"_"+locale, context);
//			msgHelper.setText(body,true);
//
//			// Attachments
//			if (attachmentsFilesAbsolutePaths != null && !attachmentsFilesAbsolutePaths.isEmpty()) {
//				for (String attachmentPath : attachmentsFilesAbsolutePaths) {
//					File pieceJointe = new File(attachmentPath);
//					FileSystemResource file = new FileSystemResource(attachmentPath);
//					if (pieceJointe.exists() && pieceJointe.isFile()) {
//						msgHelper.addAttachment(file.getFilename(), file);
//					}
//				}
//			}
//
//			javaMailSender.send(message);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return response;
//	}
//}
