import org.testng.annotations.Test;

import org.testng.annotations.Test;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    @Test
    public void TS0001() {
        String host = "mail.javatpoint.com";
        final String user = "ozansekerrr1@gmail.com";//change accordingly
        final String password = "quantumteorisi7";//change accordingly

        String to = "can.seker.cloud@gmail.com";//change accordingly

        //Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        //Compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("İşlem Sonucu");
            message.setText("Merhaba işleminiz onaylanmıştır");

            //send the message
            Transport.send(message);

            System.out.println("message sent successfully...");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
