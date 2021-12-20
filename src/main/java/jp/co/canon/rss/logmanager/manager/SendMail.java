package jp.co.canon.rss.logmanager.manager;


import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Properties;

@Slf4j
public class SendMail {
    protected String user;
    protected String password;
    protected String ipaddr;
    protected int port;
    protected String[] multiPath;
    protected String form;

    public SendMail(
            String user,
            String password,
            String ipaddr,
            int port,
            String form
    ) {
        this.user = user;
        this.password = password;
        this.ipaddr = ipaddr;
        this.port = port;
        this.form = form;
    }

    public StatusDetail emailSend(String[] to, String title, String contents, String type, String path) throws javax.mail.MessagingException {
        StatusDetail status = new StatusDetail();
        status.setStatus(status.STATUS_SUCCESS, status.DETAIL_NONE);
        String user = this.user;
        String password = this.password;
        String[] toResult = null;

        toResult = to;
        StringBuffer sb = new StringBuffer();
        sb.append("<h4>" + contents + "</h4>\n");
        contents = sb.toString();

        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart part = new MimeBodyPart();
        part.setContent(contents, "text/html; charset=utf-8");
        multipart.addBodyPart(part);

        if(path != null) {
            MimeBodyPart part1 = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(path);
            part1.setDataHandler(new DataHandler(fds));
            part1.setFileName(fds.getName());
            multipart.addBodyPart(part1);
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", this.ipaddr);
        prop.put("mail.smtp.port", this.port);
        prop.put("mail.smtp.connectiontimeout", "100000");
        prop.put("mail.smtp.timeout", "100000");

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.form));
            message.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(String.join(",", toResult), false));
            message.setSubject(title);
            message.setContent(multipart);

            Transport.send(message);
        } catch (AddressException e) {
            status.setStatus(status.STATUS_ERROR, e.toString());
            e.printStackTrace();
        } catch (javax.mail.MessagingException e) {
            status.setStatus(status.STATUS_ERROR, e.toString());
            e.printStackTrace();
        }

        return status;
    }

    public StatusDetail emailMultiSend(String[] to, String title, String contents, String type, String[] multiFile) throws javax.mail.MessagingException {
        StatusDetail status = new StatusDetail();
        status.setStatus(status.STATUS_SUCCESS, status.DETAIL_NONE);
        String user = this.user;
        String password = this.password;
        String[] toResult = null;
        String body;

        toResult = to;
        StringBuffer sb = new StringBuffer();
        sb.append(contents);
        body = sb.toString();
        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart part = new MimeBodyPart();
        part.setContent(body, "text/html; charset=utf-8");
        multipart.addBodyPart(part);

        for(int idx = 0; idx < multiFile.length; idx++) {
            MimeBodyPart part1 = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(multiFile[idx]);
            part1.setDataHandler(new DataHandler(fds));
            part1.setFileName(fds.getName());
            multipart.addBodyPart(part1);
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", this.ipaddr);
        prop.put("mail.smtp.port", this.port);
        prop.put("mail.smtp.connectiontimeout", "100000");
        prop.put("mail.smtp.timeout", "100000");

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.form));
            message.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(String.join(",",toResult), false));
            message.setSubject(title);
            message.setContent(multipart);

            Transport.send(message);
        } catch (AddressException e) {
            status.setStatus(status.STATUS_ERROR, e.toString());
            e.printStackTrace();
        } catch (javax.mail.MessagingException e) {
            status.setStatus(status.STATUS_ERROR, e.toString());
            e.printStackTrace();
        }

        return status;
    }
}