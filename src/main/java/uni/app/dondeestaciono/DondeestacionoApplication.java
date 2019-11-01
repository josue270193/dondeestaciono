package uni.app.dondeestaciono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DondeestacionoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DondeestacionoApplication.class, args);
  }
}
