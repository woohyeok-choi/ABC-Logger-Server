package kaist.iclab.abclogger.common

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GMailSender(
        private val email: String,
        password: String,
        recipients: List<String>
) {
    private val properties = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "465")
        put("mail.smtp.auth", "true")
        put("mail.smtp.ssl.enable", "true")
    }

    private val authenticator = object: Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(email, password)
        }
    }

    private val recipientsAddress = InternetAddress.parse(recipients.joinToString(","))

    private val session = Session.getInstance(properties, authenticator)

    fun send(subject: String, text: String) {
        if (recipientsAddress.isEmpty()) return
        if (email.isBlank()) return

        try {
            val message = MimeMessage(session).apply {
                setFrom(email)
                setRecipients(
                        Message.RecipientType.TO,
                        recipientsAddress
                )
                setSubject(subject)
                setText(text)
            }
            Transport.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}