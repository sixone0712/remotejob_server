package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

public class MailSender {
    public Boolean sendMessageWithAttachment(JavaMailSender emailSender,
                                             String from,
                                             String [] to,
                                             String subject,
                                             String body,
                                             List<String> pathToAttachment) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            for (String attach : pathToAttachment) {
                FileSystemResource file
                        = new FileSystemResource(new File(attach));
                helper.addAttachment(file.getFilename(), file);
            }

            emailSender.send(message);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
