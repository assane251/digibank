package gm.rahmanproperties.digibank.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Objects;
import java.util.Properties;

public class JavaMailer {
    private final String fromEmail;
    private final String password;
    private final String smtpHost;
    private final String smtpPort;

    public JavaMailer(String fromEmail, String password) {
        this(fromEmail, password, "smtp.gmail.com", "587");
    }

    public JavaMailer(String fromEmail, String password, String smtpHost, String smtpPort) {
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email de l'expéditeur ne peut pas être nulle ou vide.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être nul ou vide.");
        }
        this.fromEmail = fromEmail;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email du destinataire ne peut pas être nulle ou vide.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            String logoPath = Objects.requireNonNull(getClass().getResource("/assets/logo.png")).getPath();

            // 📌 Partie HTML avec logo intégré
            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = "<html>" +
                    "<body>" +
                    "<div style='text-align: center;'><img src='cid:logoImage' alt='DigiBank' width='150'></div>" +
                    "<h2>Bienvenue chez <span style='color: #007bff;'>DigiBank</span></h2>" +
                    "<p>Votre compte a été créé avec succès.</p>" +
                    "<p><strong>Email :</strong> " + to + "</p>" +
                    "<p><strong>Mot de passe par défaut :</strong> <span style='color: #007bff;'>passer123</span></p>" +
                    "<p>Nous vous recommandons de changer votre mot de passe dès votre première connexion.</p>" +
                    "<hr><p>&copy; 2025 DigiBank. Tous droits réservés.</p>" +
                    "</body></html>";
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new FileDataSource(logoPath);
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setHeader("Content-ID", "<logoImage>");
            imagePart.setDisposition(MimeBodyPart.INLINE);

            // 📌 Ajouter les parties à l'email
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(imagePart);
            message.setContent(multipart);

            // 📩 Envoyer le mail
            Transport.send(message);

            // ✅ Affichage d'un message en JavaFX
            javafx.application.Platform.runLater(() -> Popup.showSuccessMessage("Email envoyé avec succès à : " + to));

        } catch (Exception e) {
            throw new MessagingException("Échec de l'envoi de l'email: " + e.getMessage(), e);
        }
    }
}
