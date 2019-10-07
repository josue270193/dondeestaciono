package uni.app.dondeestaciono.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "caba", ignoreInvalidFields = true)
@Data
public class CabaProperties {

  private String clientId;
  private String clientSecret;
  private String urlBase;
  private CabaUrlsProperties urls;
}
