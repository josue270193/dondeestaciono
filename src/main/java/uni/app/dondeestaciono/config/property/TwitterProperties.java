package uni.app.dondeestaciono.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "twitter", ignoreInvalidFields = true)
@Data
public class TwitterProperties {

  private Boolean debug;
  private String consumerKey;
  private String consumerSecret;
  private String accessToken;
  private String accessTokenSecret;
}
