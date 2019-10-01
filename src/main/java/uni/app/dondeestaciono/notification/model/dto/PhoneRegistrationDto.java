package uni.app.dondeestaciono.notification.model.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class PhoneRegistrationDto {

  private String id;
  private String token;
  private OffsetDateTime dateTime;
}
