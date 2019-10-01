package uni.app.dondeestaciono.notification.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.notification.model.PhoneRegistration;
import uni.app.dondeestaciono.notification.model.dto.PhoneMessageDto;
import uni.app.dondeestaciono.notification.model.dto.PhoneRegistrationDto;
import uni.app.dondeestaciono.notification.repository.NotificationRepository;
import uni.app.dondeestaciono.util.FirebaseService;

@RestController
@RequestMapping("/notification")
public class NotificationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

  private final NotificationRepository notificationRepository;
  private final FirebaseService firebaseService;

  public NotificationController(
      NotificationRepository notificationRepository, FirebaseService firebaseService) {
    this.notificationRepository = notificationRepository;
    this.firebaseService = firebaseService;
  }

  @GetMapping("/register")
  public Flux<PhoneRegistrationDto> getRegisterAll() {
    LOGGER.debug("getAll");
    return notificationRepository
        .findAll()
        .map(
            phoneRegistrationSaved -> {
              PhoneRegistrationDto phoneMessageDto = new PhoneRegistrationDto();
              BeanUtils.copyProperties(phoneRegistrationSaved, phoneMessageDto);
              return phoneMessageDto;
            });
  }

  @PostMapping("/push_message")
  public Mono<ResponseEntity<Object>> getPushMessage(@RequestBody PhoneMessageDto phoneMessageDto) {
    LOGGER.debug("getTest {}", phoneMessageDto);
    return firebaseService
        .pushMessage(phoneMessageDto)
        .map(response -> ResponseEntity.ok().build())
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }

  @PostMapping("/register")
  public Mono<ResponseEntity<Object>> postRegister(
      @RequestBody PhoneRegistrationDto phoneRegistrationDto) {
    LOGGER.debug("postRegister: {}", phoneRegistrationDto);
    PhoneRegistration phoneRegistration = new PhoneRegistration();
    BeanUtils.copyProperties(phoneRegistrationDto, phoneRegistration);
    return notificationRepository
        .save(phoneRegistration)
        .map(response -> ResponseEntity.ok().build())
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }
}
