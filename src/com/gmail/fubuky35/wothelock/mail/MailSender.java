package com.gmail.fubuky35.wothelock.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;

import android.content.Context;

public class MailSender {

	private MailSender() {}
	
	public static boolean sendMail(Context context,final String subject, final String mainText){
		
		SaveLoadManager sm = SaveLoadManager.getInstance(context);
		
		String address = sm.loadFromAccountAddress();
		String password = sm.loadFromAccountPassword();
		String toAddress = sm.loadToAddress();
		
		if(null == address || null == password || null == toAddress){
			return false;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); // SMTPサーバ名
		props.put("mail.host", "smtp.gmail.com"); // 接続するホスト名
		props.put("mail.smtp.port", "587"); // SMTPサーバポート
		props.put("mail.smtp.auth", "true"); // smtp auth
		props.put("mail.smtp.starttls.enable", "true"); // STTLS

		// セッション
		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);

		MimeMessage msg = new MimeMessage(session);
		try {
			msg.setSubject(subject, "utf-8");
			msg.setFrom(new InternetAddress(address));
			msg.setSender(new InternetAddress(address));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			msg.setText(mainText, "utf-8");

			Transport t = session.getTransport("smtp");
			t.connect(address, password);
			t.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
