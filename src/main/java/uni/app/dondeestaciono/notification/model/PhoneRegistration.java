package uni.app.dondeestaciono.notification.model;

import java.time.OffsetDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class PhoneRegistration {

  @Id private String id;
  private String token;
  private OffsetDateTime dateTime;
}
