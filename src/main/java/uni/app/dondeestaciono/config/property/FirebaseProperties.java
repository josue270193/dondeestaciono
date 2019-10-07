package uni.app.dondeestaciono.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "firebase", ignoreInvalidFields = true)
@Data
public class FirebaseProperties {

  private String url;
  private String jsonPath;
}
