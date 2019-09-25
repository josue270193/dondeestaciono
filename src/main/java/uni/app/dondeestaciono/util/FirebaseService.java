package uni.app.dondeestaciono.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import java.io.FileInputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseService.class);
  private static final String MESSAGE_TAG = "message";

  @Value("${google.json-path}")
  private String googleJsonPath;

  @Value("${google.firebase-url}")
  private String googleFirebaseUrl;

  @EventListener(ApplicationReadyEvent.class)
  private void configuration() {
    try (FileInputStream serviceAccount = new FileInputStream(googleJsonPath)) {
      FirebaseOptions options =
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .setDatabaseUrl(googleFirebaseUrl)
              .build();

      FirebaseApp.initializeApp(options);
    } catch (Exception e) {
      LOGGER.error("Error al configurar Firebase", e);
    }
  }

  public void pushMessage(String registrationToken, String messageString) {
    try {
      Message message =
          Message.builder().putData(MESSAGE_TAG, messageString).setToken(registrationToken).build();
      String response = FirebaseMessaging.getInstance().send(message);
      LOGGER.info("Mensaje Enviado: {}", response);
    } catch (Exception e) {
      LOGGER.error("Error al enviar Push Notification", e);
    }
  }

  public void pushMessage(List<String> registrationToken, String messageString) {
    try {
      MulticastMessage message =
          MulticastMessage.builder()
              .putData(MESSAGE_TAG, messageString)
              .addAllTokens(registrationToken)
              .build();
      BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
      LOGGER.info("Mensajes Enviado: {}", response.getSuccessCount());
    } catch (Exception e) {
      LOGGER.error("Error al enviar Push Notification", e);
    }
  }
}
