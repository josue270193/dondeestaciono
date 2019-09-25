package uni.app.dondeestaciono.notificacion.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uni.app.dondeestaciono.util.FirebaseService;

@RestController
@RequestMapping("/notification")
public class NotificationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

  private final FirebaseService firebaseService;

  public NotificationController(FirebaseService firebaseService) {
    this.firebaseService = firebaseService;
  }

  @GetMapping("/test")
  public void getTest(@RequestParam String token) {
    LOGGER.debug("getTest");
    firebaseService.pushMessage(token, "test");
  }

  @PostMapping("/register")
  public void postRegister(@RequestBody String requestBody) {
    LOGGER.debug("postRegister: {}", requestBody);
  }
}
