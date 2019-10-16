package uni.app.dondeestaciono.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "caba.urls", ignoreInvalidFields = true)
@Data
public class CabaUrlsProperties {

  private String estacionamientos;
  private String normalizador;
}
