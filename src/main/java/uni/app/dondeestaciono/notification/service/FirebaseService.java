package uni.app.dondeestaciono.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import java.io.FileInputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.config.property.FirebaseProperties;
import uni.app.dondeestaciono.notification.model.dto.PhoneMessageDto;

@Service
public class FirebaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseService.class);
  private static final String MESSAGE_TAG = "message";

  private final FirebaseProperties firebaseProperties;

  public FirebaseService(FirebaseProperties firebaseProperties) {
    this.firebaseProperties = firebaseProperties;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void configuration() {
    try (FileInputStream serviceAccount = new FileInputStream(firebaseProperties.getJsonPath())) {
      FirebaseOptions options =
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .setDatabaseUrl(firebaseProperties.getUrl())
              .build();

      FirebaseApp.initializeApp(options);
    } catch (Exception e) {
      LOGGER.error("Error al configurar Firebase", e);
    }
  }

  public Mono<String> pushMessage(String registrationToken, String messageString) {
    String response = "";
    try {
      Message message =
          Message.builder().putData(MESSAGE_TAG, messageString).setToken(registrationToken).build();
      response = FirebaseMessaging.getInstance().send(message);
      LOGGER.info("Mensaje Enviado: {}", response);
    } catch (FirebaseMessagingException e) {
      LOGGER.error("Error al enviar Push Notification", e);
    }
    return Mono.just(response);
  }

  public BatchResponse pushMessage(List<String> registrationToken, String messageString) {
    BatchResponse response = null;
    try {
      MulticastMessage message =
          MulticastMessage.builder()
              .putData(MESSAGE_TAG, messageString)
              .addAllTokens(registrationToken)
              .build();
      response = FirebaseMessaging.getInstance().sendMulticast(message);
      LOGGER.info("Mensajes Enviado: {}", response.getSuccessCount());
    } catch (Exception e) {
      LOGGER.error("Error al enviar Push Notification", e);
    }
    return response;
  }

  public Mono<String> pushMessage(PhoneMessageDto phoneMessageDto) {
    return this.pushMessage(phoneMessageDto.getToken(), phoneMessageDto.getMessage());
  }
}
